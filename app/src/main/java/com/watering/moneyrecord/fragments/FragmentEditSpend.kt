package com.watering.moneyrecord.fragments

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil.inflate
import androidx.databinding.Observable
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.*
import androidx.lifecycle.Observer

import com.watering.moneyrecord.MainActivity
import com.watering.moneyrecord.R
import com.watering.moneyrecord.databinding.FragmentEditSpendBinding
import com.watering.moneyrecord.entities.Spend
import com.watering.moneyrecord.entities.SpendCard
import com.watering.moneyrecord.entities.SpendCash
import com.watering.moneyrecord.model.Converter
import com.watering.moneyrecord.model.MyCalendar
import com.watering.moneyrecord.model.Processing
import com.watering.moneyrecord.viewmodel.ViewModelEditSpend
import kotlinx.coroutines.*
import java.util.*

class FragmentEditSpend : Fragment() {
    private lateinit var binding: FragmentEditSpendBinding
    private lateinit var processing: Processing
    private lateinit var spend: Spend
    private val mFragmentManager by lazy { fragmentManager as FragmentManager }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = inflate(inflater, R.layout.fragment_edit_spend, container, false)
        binding.lifecycleOwner = this
        binding.viewmodel = ViewModelProviders.of(this).get(ViewModelEditSpend::class.java)
        processing = Processing(binding.viewmodel, mFragmentManager)

        initLayout()

        return binding.root
    }

    fun initInstance(item: Spend):FragmentEditSpend {
        spend = item
        return this
    }

    private fun initLayout() {
        (activity as MainActivity).supportActionBar?.setTitle(R.string.title_spend)

        binding.viewmodel?.run {
            spend = this@FragmentEditSpend.spend
            this@FragmentEditSpend.spend.code?.let { oldCode = it }

            getCatMainBySub(this@FragmentEditSpend.spend.category).observeOnce( Observer { main -> main?.let {
                Transformations.map(listOfMain) { list -> list.indexOf(main.name) }.observeOnce( Observer { index -> index?.let { indexOfMain = index } })
            } })

            addOnPropertyChangedCallback(object: Observable.OnPropertyChangedCallback() {
                override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                    when(propertyId) {
                        BR.indexOfMain -> onChangedIndexOfMain()
                        BR.listOfSub -> onChangedListOfSub()
                        BR.indexOfSub -> onChangedIndexOfSub()
                        BR.indexOfPay2 -> onChangedIndexOfPay2()
                    }
                }
            })
        }

        binding.buttonDateFragmentEditSpend.setOnClickListener {
            binding.viewmodel?.run {
                val dialog = DialogDate().newInstance(spend.date, object:DialogDate.Complete {
                    override fun onComplete(date: String?) {
                        val select = MyCalendar.strToCalendar(date)
                        when {
                            Calendar.getInstance().before(select) -> Toast.makeText(activity, R.string.toast_date_error, Toast.LENGTH_SHORT).show()
                            else -> spend = spend.apply { this.date = MyCalendar.calendarToStr(select) }
                        }
                    }
                })
                fragmentManager?.let { dialog.show(it, "dialog") }
            }
        }

        Converter.addConvertedTextChangedListener(binding.editAmountFragmentEditSpend)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.menu_edit,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_edit_save -> save()
            R.id.menu_edit_delete -> binding.viewmodel?.run {
                runBlocking {
                    delay(100)
                    val job = delete(spend)

                    runBlocking {
                        job.join()

                        when(oldCode[0]) {
                            '1' -> getSpendCash(oldCode).observeOnce( Observer { cash -> cash?.let {
                                val jobDelete = delete(it)
                                runBlocking {
                                    jobDelete.join()
                                    Toast.makeText(activity, R.string.toast_delete_success, Toast.LENGTH_SHORT).show()
                                    processing.ioKRW(idAccount, spend.date)
                                }
                            }})
                            '2' -> getSpendCard(oldCode).observeOnce( Observer { card -> card?.let {
                                val jobDelete = delete(it)
                                runBlocking {
                                    jobDelete.join()
                                    Toast.makeText(activity, R.string.toast_delete_success, Toast.LENGTH_SHORT).show()
                                    processing.ioKRW(idAccount, spend.date)
                                }
                            }})
                        }
                    }
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun onChangedIndexOfMain() {
        // 화면 갱신이 동시에 되도록
        binding.viewmodel?.run {
            when(oldCode[0]) {
                '1' -> {
                    indexOfPay1 = 0
                    listOfPay2.observeOnce( Observer { list -> list?.let {
                        getAccountByCode(oldCode).observeOnce( Observer { account -> account?.let {
                            indexOfPay2 = list.indexOf(account.number + " " + account.institute + " " + account.description)
                            idAccount = account.id
                        } })
                    } })
                }
                '2' -> {
                    indexOfPay1 = 1
                    listOfPay2.observeOnce( Observer { list -> list?.let {
                        getCardByCode(oldCode).observeOnce( Observer { card -> card?.let {
                            indexOfPay2 = list.indexOf(card.number + " " + card.company + " " + card.name)
                            idCard = card.id
                            idAccount = card.account
                        } })
                    } })
                }
            }
        }
    }
    fun onChangedIndexOfSub() {
        binding.viewmodel?.run {
            Transformations.switchMap(listOfMain) { listOfMain ->
                Transformations.switchMap(listOfSub) { listOfSub ->
                    if(indexOfSub < 0 || indexOfMain < 0) null
                    else getCatSub(listOfSub[indexOfSub], listOfMain[indexOfMain])
                }
            }.observeOnce( Observer { sub -> sub?.let { spend.category = sub.id } })
        }
    }

    fun onChangedListOfSub() {
        binding.viewmodel?.run {
            getCatSub(this@FragmentEditSpend.spend.category).observeOnce( Observer { sub -> sub?.let {
                listOfSub.observeOnce(Observer { list ->
                    indexOfSub = if(!list.isNullOrEmpty()) list.indexOf(sub.name) else 0
                })
            } })
        }
    }
    fun onChangedIndexOfPay2() {
        binding.viewmodel?.run {
            when(newCode[0]) {
                '1' -> {
                    Transformations.switchMap(listOfPay2) { list -> list?.let {
                        if(list.isNotEmpty()) getAccountByNumber(it[indexOfPay2].split(" ")[0]) else null }
                    }.observeOnce( Observer { account -> account?.let { idAccount = account.id } })
                }
                '2' -> {
                    Transformations.switchMap(listOfPay2) { list -> list?.let{
                        if(list.isNotEmpty()) getCardByNumber(it[indexOfPay2].split(" ")[0]) else null }
                    } .observeOnce( Observer { card -> card?.let {
                        idCard = card.id
                        idAccount = card.account
                    } })
                }
            }
        }
    }

    private fun save() {
        runBlocking {
            delay(100)
            binding.viewmodel?.run {
                if(spend.details.isNullOrEmpty() || indexOfMain < 0 || indexOfSub < 0 || indexOfPay1 < 0 || indexOfPay2 < 0) {
                    Toast.makeText(activity?.baseContext, R.string.toast_warning_input, Toast.LENGTH_SHORT).show()
                }
                else {
                    Transformations.map(getLastSpendCode(spend.date)) { code ->
                        val index = code?.substring(10,12)?.toInt() ?: -1
                        newCode = newCode.replaceRange(10, 12, String.format("%02d", index + 1))
                        newCode
                    }.observeOnce( Observer { code -> code?.let {
                        spend.code = code

                        val jobSpend1 = if(spend.id == null) insert(spend) else update(spend)
                        val jobSpend2 = jobSpend2(code)

                        runBlocking {
                            jobSpend1.join()
                            jobSpend2?.join()

                            if(spend.id != null) {
                                when(oldCode[0]) {
                                    '1' -> getSpendCash(oldCode).observeOnce( Observer { cash -> cash?.let { deleteOldSpend(it) } })
                                    '2' -> getSpendCard(oldCode).observeOnce( Observer { card -> card?.let { deleteOldSpend(it) } })
                                }
                            } else {
                                Toast.makeText(activity, R.string.toast_save_success, Toast.LENGTH_SHORT).show()
                                processing.ioKRW(idAccount, spend.date)
                            }
                        }
                    } })
                }
            }
        }
    }
    private fun jobSpend2(code: String): Job? {
        return binding.viewmodel?.run {
            val job = when(code[0]) {
                '1' -> {
                    val spendCash = SpendCash().apply {
                        this.code = code
                        account = idAccount
                    }
                    insert(spendCash)
                }
                '2' -> {
                    val spendCard = SpendCard().apply {
                        this.code = code
                        card = idCard
                    }
                    insert(spendCard)
                }
                else -> insert(null)
            }
            job
        }
    }
    private fun <T> deleteOldSpend(t: T) {
        binding.viewmodel?.run {
            val job = delete(t)

            runBlocking {
                job.join()
                Toast.makeText(activity, R.string.toast_save_success, Toast.LENGTH_SHORT).show()
                processing.ioKRW(idAccount, spend.date)
            }
        }
    }

    private fun <T> LiveData<T>.observeOnce(observer: Observer<T>) {
        observeForever(object: Observer<T> {
            override fun onChanged(t: T) {
                observer.onChanged(t)
                removeObserver(this)
            }
        })
    }
}