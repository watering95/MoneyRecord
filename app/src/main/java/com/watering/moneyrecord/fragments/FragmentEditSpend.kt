package com.watering.moneyrecord.fragments

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil.inflate
import androidx.databinding.Observable
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.*

import com.watering.moneyrecord.R
import com.watering.moneyrecord.databinding.FragmentEditSpendBinding
import com.watering.moneyrecord.entities.Spend
import com.watering.moneyrecord.entities.SpendCard
import com.watering.moneyrecord.entities.SpendCash
import com.watering.moneyrecord.model.Converter
import com.watering.moneyrecord.model.MyCalendar

import com.watering.moneyrecord.viewmodel.ViewModelEditSpend
import kotlinx.coroutines.*
import java.util.*

class FragmentEditSpend : ParentFragment() {
    private lateinit var binding: FragmentEditSpendBinding
    private lateinit var spend: Spend

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = inflate(inflater, R.layout.fragment_edit_spend, container, false)
        binding.lifecycleOwner = this
        binding.viewmodel = application?.let { ViewModelEditSpend(it) }

        initLayout()

        return binding.root
    }

    fun initInstance(item: Spend):FragmentEditSpend {
        spend = item
        return this
    }

    private fun initLayout() {
        mActionBar?.setTitle(R.string.title_spend)

        binding.viewmodel?.run {
            spend = this@FragmentEditSpend.spend
            this@FragmentEditSpend.spend.code?.let { oldCode = it }
            binding.amount = spend.amount

            getCatMainBySub(this@FragmentEditSpend.spend.category).observeOnce { main ->
                main?.let {
                    Transformations.map(listOfMain) { list -> list.indexOf(main.name) }
                        .observeOnce { index -> index?.let { indexOfMain = index } }
                }
            }

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

        binding.isUpdateEvaluation = false

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
                mFragmentManager.let { dialog.show(it, "dialog") }
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
                            '1' -> getSpendCash(oldCode).observeOnce { cash ->
                                cash?.let {
                                    val jobDelete = delete(it)
                                    runBlocking {
                                        jobDelete.join()
                                        Toast.makeText(
                                            activity,
                                            R.string.toast_delete_success,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        processing?.ioKRW(
                                            idAccount,
                                            spend.date,
                                            binding.isUpdateEvaluation!!
                                        )
                                    }
                                }
                            }
                            '2' -> getSpendCard(oldCode).observeOnce { card ->
                                card?.let {
                                    val jobDelete = delete(it)
                                    runBlocking {
                                        jobDelete.join()
                                        Toast.makeText(
                                            activity,
                                            R.string.toast_delete_success,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        processing?.ioKRW(
                                            idAccount,
                                            spend.date,
                                            binding.isUpdateEvaluation!!
                                        )
                                    }
                                }
                            }
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
                    listOfPay2.observeOnce { list ->
                        list?.let {
                            getAccountByCode(oldCode).observeOnce { account ->
                                account?.let {
                                    indexOfPay2 =
                                        list.indexOf(account.number + " " + account.institute + " " + account.description)
                                    idAccount = account.id
                                }
                            }
                        }
                    }
                }
                '2' -> {
                    indexOfPay1 = 1
                    listOfPay2.observeOnce { list ->
                        list?.let {
                            getCardByCode(oldCode).observeOnce { card ->
                                card?.let {
                                    indexOfPay2 =
                                        list.indexOf(card.number + " " + card.company + " " + card.name)
                                    idCard = card.id
                                    idAccount = card.account
                                }
                            }
                        }
                    }
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
            }.observeOnce { sub -> sub?.let { spend.category = sub.id } }
        }
    }

    fun onChangedListOfSub() {
        binding.viewmodel?.run {
            getCatSub(this@FragmentEditSpend.spend.category).observeOnce { sub ->
                sub?.let {
                    listOfSub.observeOnce { list ->
                        indexOfSub = if (!list.isNullOrEmpty()) list.indexOf(sub.name) else 0
                        if(indexOfSub < 0) indexOfSub = 0
                    }
                }
            }
        }
    }
    fun onChangedIndexOfPay2() {
        binding.viewmodel?.run {
            when(newCode[0]) {
                '1' -> {
                    Transformations.switchMap(listOfPay2) { list -> list?.let {
                        if(list.isNotEmpty()) getAccountByNumber(it[indexOfPay2].split(" ")[0]) else null }
                    }.observeOnce { account -> account?.let { idAccount = account.id } }
                }
                '2' -> {
                    Transformations.switchMap(listOfPay2) { list -> list?.let{
                        if(list.isNotEmpty()) getCardByNumber(it[indexOfPay2].split(" ")[0]) else null }
                    } .observeOnce { card ->
                        card?.let {
                            idCard = card.id
                            idAccount = card.account
                        }
                    }
                }
            }
        }
    }

    private fun save() {
        spend.apply { amount = binding.amount }
        runBlocking {
            delay(100)
            binding.viewmodel?.run {
                if(spend.details.isNullOrEmpty() || indexOfMain < 0 || indexOfSub < 0 || indexOfPay1 < 0 || indexOfPay2 < 0) {
                    Toast.makeText(activity.baseContext, R.string.toast_warning_input, Toast.LENGTH_SHORT).show()
                }
                else {
                    Transformations.map(getLastSpendCode(spend.date)) { code ->
                        val index = code?.substring(10,12)?.toInt() ?: -1
                        newCode = newCode.replaceRange(10, 12, String.format("%02d", index + 1))
                        newCode
                    }.observeOnce { code ->
                        code?.let {
                            spend.code = code

                            val jobSpend1 = if (spend.id == null) insert(spend) else update(spend)
                            val jobSpend2 = jobSpend2(code)

                            runBlocking {
                                jobSpend1.join()
                                jobSpend2?.join()

                                if (spend.id != null) {
                                    when (oldCode[0]) {
                                        '1' -> getSpendCash(oldCode).observeOnce { cash ->
                                            cash?.let {
                                                deleteOldSpend(
                                                    it
                                                )
                                            }
                                        }
                                        '2' -> getSpendCard(oldCode).observeOnce { card ->
                                            card?.let {
                                                deleteOldSpend(
                                                    it
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    Toast.makeText(
                                        activity,
                                        R.string.toast_save_success,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    processing?.ioKRW(
                                        idAccount,
                                        spend.date,
                                        binding.isUpdateEvaluation!!
                                    )
                                }
                            }
                        }
                    }
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
                processing?.ioKRW(idAccount, spend.date, binding.isUpdateEvaluation!!)
            }
        }
    }
}