package com.watering.moneyrecord.fragments

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil.inflate
import androidx.databinding.Observable
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.*
import com.watering.moneyrecord.R
import com.watering.moneyrecord.databinding.FragmentEditIncomeBinding
import com.watering.moneyrecord.entities.Income
import com.watering.moneyrecord.model.Converter
import com.watering.moneyrecord.model.MyCalendar
import com.watering.moneyrecord.viewmodel.ViewModelEditIncome
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.util.*

class FragmentEditIncome : ParentFragment() {
    private lateinit var income: Income
    private lateinit var binding: FragmentEditIncomeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = inflate(inflater, R.layout.fragment_edit_income, container, false)
        binding.lifecycleOwner = this
        binding.viewmodel = application?.let { ViewModelEditIncome(it) }

        initLayout()
        return binding.root
    }
    fun initInstance(item: Income):FragmentEditIncome {
        income = item
        return this
    }

    private fun initLayout() {
        mActionBar?.setTitle(R.string.title_income)

        binding.viewmodel?.run {
            income = this@FragmentEditIncome.income
            if(income.id != null) idAccount = income.account
            binding.amount = income.amount

            getCatMainBySub(this@FragmentEditIncome.income.category).observeOnce { main ->
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
                        BR.indexOfAccount -> onChangedIndexOfAccount()
                    }
                }
            })
        }

        binding.isUpdateEvaluation = false

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
                mFragmentManager.let { dialog.show(it, "dialog") }
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
                        Toast.makeText(activity, R.string.toast_delete_success, Toast.LENGTH_SHORT).show()
                        processing?.ioKRW(idAccount,income.date, binding.isUpdateEvaluation!!)
                    }
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun onChangedIndexOfMain() {
        // 화면 갱신이 동시에 되도록
        binding.viewmodel?.run {
            listOfAccount.observeOnce { list ->
                list?.let {
                    getAccount(income.account).observeOnce { account ->
                        account?.let {
                            indexOfAccount =
                                list.indexOf(account.number + " " + account.institute + " " + account.description)
                        }
                    }
                }
            }
        }
    }

    fun onChangedListOfSub() {
        binding.viewmodel?.run {
            getCatSub(this@FragmentEditIncome.income.category).observeOnce { sub ->
                sub?.let {
                    listOfSub.observeOnce { list ->
                        list?.let {
                            indexOfSub = if (!list.isNullOrEmpty()) list.indexOf(sub.name) else 0
                            if(indexOfSub < 0) indexOfSub = 0
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
            }.observeOnce { sub -> sub?.let { income.category = sub.id } }
        }
    }
    fun onChangedIndexOfAccount() {
        binding.viewmodel?.run {
            Transformations.switchMap(listOfAccount) { list ->
                if(indexOfAccount < 0) null else getAccountByNumber(list[indexOfAccount].split(" ")[0])
            }.observeOnce { account ->
                account?.let {
                    idAccount = account.id
                    income.account = account.id
                }
            }
        }
    }
    private fun save() {
        income.apply { amount = binding.amount }
        runBlocking {
            delay(100)
            binding.viewmodel?.run {
                if(income.details.isNullOrEmpty() || indexOfMain < 0 || indexOfSub < 0 || indexOfAccount < 0) {
                    Toast.makeText(activity.baseContext, R.string.toast_warning_input, Toast.LENGTH_SHORT).show()
                } else {
                    val jobIncome = if(income.id == null) insert(income) else update(income)

                    runBlocking {
                        jobIncome.join()
                        Toast.makeText(activity, R.string.toast_save_success, Toast.LENGTH_SHORT).show()
                        processing?.ioKRW(idAccount, income.date, binding.isUpdateEvaluation!!)
                    }
                }
            }
        }
    }
}