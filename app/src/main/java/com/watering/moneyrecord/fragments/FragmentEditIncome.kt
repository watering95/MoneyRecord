package com.watering.moneyrecord.fragments

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil.inflate
import androidx.databinding.Observable
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.lifecycle.Observer

import com.watering.moneyrecord.MainActivity
import com.watering.moneyrecord.R
import com.watering.moneyrecord.databinding.FragmentEditIncomeBinding
import com.watering.moneyrecord.entities.Income
import com.watering.moneyrecord.model.Converter
import com.watering.moneyrecord.model.MyCalendar
import com.watering.moneyrecord.model.Processing
import com.watering.moneyrecord.viewmodel.ViewModelEditIncome
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.util.*

class FragmentEditIncome : Fragment() {
    private lateinit var income: Income
    private lateinit var binding: FragmentEditIncomeBinding
    private lateinit var processing: Processing

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = inflate(inflater, R.layout.fragment_edit_income, container, false)
        binding.lifecycleOwner = this
        binding.viewmodel = ViewModelProviders.of(this).get(ViewModelEditIncome::class.java)
        processing = Processing(binding.viewmodel, fragmentManager)

        initLayout()
        return binding.root
    }
    fun initInstance(item: Income):FragmentEditIncome {
        income = item
        return this
    }

    private fun initLayout() {
        (activity as MainActivity).supportActionBar?.setTitle(R.string.title_income)

        binding.viewmodel?.run {
            income = this@FragmentEditIncome.income
            if(income.id == null) { income = income.apply { this.date = MyCalendar.getToday() } }
            else idAccount = income.account

            getCatMainBySub(this@FragmentEditIncome.income.category).observeOnce( Observer { main -> main?.let {
                Transformations.map(listOfMain) { list -> list.indexOf(main.name) }.observeOnce( Observer { index -> index?.let { indexOfMain = index } })
            } })

            listOfAccount.observeOnce( Observer { list -> list?.let {
                getAccount(income.account).observeOnce( Observer { account -> account?.let {
                    indexOfAccount = list.indexOf(account.number + " " + account.institute + " " + account.description)
                } })
            } })

            addOnPropertyChangedCallback(object: Observable.OnPropertyChangedCallback() {
                override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                    when(propertyId) {
                        BR.listOfSub -> onChangedListOfSub()
                        BR.indexOfSub -> onChangedIndexOfSub()
                        BR.indexOfAccount -> onChangedIndexOfAccount()
                    }
                }
            })
        }

        binding.buttonDateFragmentEditIncome.setOnClickListener {
            binding.viewmodel?.run {
                val dialog = DialogDate().newInstance(income.date, object:DialogDate.Complete {
                    override fun onComplete(date: String?) {
                        val select = MyCalendar.strToCalendar(date)
                        when {
                            Calendar.getInstance().before(select) -> Toast.makeText(activity, R.string.toast_date_error, Toast.LENGTH_SHORT).show()
                            else -> income = income.apply { this.date = MyCalendar.calendarToStr(select) }
                        }
                    }
                })
                fragmentManager?.let { it -> dialog.show(it, "dialog") }
            }
        }

        Converter.addConvertedTextChangedListener(binding.editAmountFragmentEditIncome)
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

                    val job = delete(income)
                    runBlocking {
                        job.join()
                        delay(100)
                        Toast.makeText(activity, R.string.toast_delete_success, Toast.LENGTH_SHORT).show()
                        processing.ioKRW(idAccount,income.date)
                    }
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun onChangedListOfSub() {
        binding.viewmodel?.run {
            getCatSub(this@FragmentEditIncome.income.category).observeOnce( Observer { sub -> sub?.let {
                listOfSub.observeOnce( Observer { list-> list?.let { indexOfSub = list.indexOf(sub.name) } })
            } })
        }
    }

    fun onChangedIndexOfSub() {
        binding.viewmodel?.run {
            Transformations.switchMap(listOfMain) { listOfMain ->
                Transformations.switchMap(listOfSub) { listOfSub ->
                    if(indexOfSub < 0 || indexOfMain < 0) null
                    else getCatSub(listOfSub[indexOfSub], listOfMain[indexOfMain])
                }
            }.observeOnce( Observer { sub -> sub?.let { income.category = sub.id } })
        }
    }
    fun onChangedIndexOfAccount() {
        binding.viewmodel?.run {
            Transformations.switchMap(listOfAccount) { list ->
                if(indexOfAccount < 0) null else getAccountByNumber(list[indexOfAccount].split(" ")[0])
            }.observeOnce( Observer { account -> account?.let {
                idAccount = account.id
                income.account = account.id
            } })
        }
    }
    private fun save() {
        binding.viewmodel?.run {
            runBlocking {
                delay(100)

                if(income.details.isNullOrEmpty() || indexOfMain < 0 || indexOfSub < 0 || indexOfAccount < 0) {
                    Toast.makeText(activity?.baseContext, R.string.toast_warning_input, Toast.LENGTH_SHORT).show()
                } else {
                    val jobIncome = if(income.id == null) insert(income) else update(income)

                    runBlocking {
                        jobIncome.join()
                        delay(100)
                        Toast.makeText(activity, R.string.toast_save_success, Toast.LENGTH_SHORT).show()
                        processing.ioKRW(idAccount, income.date)
                    }
                }
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