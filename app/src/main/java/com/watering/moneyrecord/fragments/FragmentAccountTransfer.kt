package com.watering.moneyrecord.fragments

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil.inflate
import androidx.databinding.Observable
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager

import com.watering.moneyrecord.MainActivity
import com.watering.moneyrecord.R
import com.watering.moneyrecord.databinding.FragmentAccountTransferBinding
import com.watering.moneyrecord.databinding.FragmentAccountsBinding
import com.watering.moneyrecord.entities.DairyTotal
import com.watering.moneyrecord.model.Converter
import com.watering.moneyrecord.model.MyCalendar
import com.watering.moneyrecord.model.Processing
import com.watering.moneyrecord.view.RecyclerViewAdapterAccounts
import com.watering.moneyrecord.viewmodel.ViewModelAccountTransfer
import com.watering.moneyrecord.viewmodel.ViewModelAccounts
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class FragmentAccountTransfer : Fragment() {
    private val mViewModel by lazy { (activity as MainActivity).mViewModel }
    private val mFragmentManager by lazy { (activity as MainActivity).supportFragmentManager }
    private lateinit var binding: FragmentAccountTransferBinding
    private lateinit var processing: Processing
    private var id_withDrawAccount: Int? = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = inflate(inflater, R.layout.fragment_account_transfer, container, false)
        binding.lifecycleOwner = this
        binding.viewmodel = ViewModelProviders.of(this).get(ViewModelAccountTransfer::class.java)
        processing = Processing(binding.viewmodel, fragmentManager)

        initLayout()
        return binding.root
    }

    fun initInstance(id_account:Int?):FragmentAccountTransfer {
        id_withDrawAccount = id_account
        return this
    }

    private fun initLayout() {
        binding.viewmodel?.run {
            Transformations.switchMap(getAccount(id_withDrawAccount)) { account ->
                Transformations.map(listOfAccount) { list ->
                    val index = list.indexOf(account.number + " " + account.institute + " " + account.description)
                    index
                }
            }.observeOnce(Observer { index -> index?.let { indexOfWithdrawAccount = it } })
        }

        binding.isUpdateDeposit = false
        binding.isUpdateWithdraw = false

        Converter.addConvertedTextChangedListener(binding.editAmountFragmentAccountTransfer)

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
            R.id.menu_edit_delete -> {}
        }
        return super.onOptionsItemSelected(item)
    }

    // TODO : DB 업데이트 실패 시 되돌리는 것도 검토 필요
    private fun save() {
        binding.viewmodel?.run {
            if(indexOfDepositAccount == indexOfWithdrawAccount || indexOfDepositAccount < 0 || indexOfWithdrawAccount < 0 || binding.amount!! < 0) {
                Toast.makeText(activity, R.string.toast_warning_input, Toast.LENGTH_SHORT).show()
            } else {
                runBlocking {
                    delay(100)
                    Transformations.switchMap(listOfAccount) { list ->
                        Transformations.switchMap(getAccountByNumber(list[indexOfWithdrawAccount].split(" ")[0])) { account ->
                            Transformations.map(loadingIOKRW(account.id, MyCalendar.getToday(), true)) { io -> io }
                        }
                    }.observeOnce( Observer { io ->  io?.let {
                        io.output = io.output!! + binding.amount!!
                        val job1 = if(io.id == null) insert(io) else update(io)

                        runBlocking {
                            job1.join()
                            processing.ioKRW(io.account, io.date, binding.isUpdateWithdraw!!)

                            Transformations.switchMap(listOfAccount) { list ->
                                Transformations.switchMap(getAccountByNumber(list[indexOfDepositAccount].split(" ")[0])) { account ->
                                    Transformations.map(loadingIOKRW(account.id, MyCalendar.getToday(), true)) { io -> io }
                                }
                            }.observeOnce( Observer { io ->  io?.let {
                                io.input = io.input!! + binding.amount!!
                                val job2 = if(io.id == null) insert(io) else update(io)

                                runBlocking {
                                    job2.join()
                                    Toast.makeText(activity, R.string.toast_transfer_success, Toast.LENGTH_SHORT).show()
                                    processing.ioKRW(io.account, io.date, binding.isUpdateDeposit!!)
                                }
                            } } )
                        }
                    } } )
                }
            }
        }
    }

    private fun itemClicked(position: Int) {
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