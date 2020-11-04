package com.watering.moneyrecord.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil.inflate
import androidx.databinding.Observable
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.recyclerview.widget.LinearLayoutManager

import com.watering.moneyrecord.R
import com.watering.moneyrecord.databinding.FragmentAccountsBinding
import com.watering.moneyrecord.entities.DairyTotal
import com.watering.moneyrecord.model.MyCalendar
import com.watering.moneyrecord.view.RecyclerViewAdapterAccounts
import com.watering.moneyrecord.viewmodel.ViewModelAccounts
import com.watering.moneyrecord.viewmodel.ViewModelApp.Companion.currentAccountId

class FragmentAccounts : ParentFragment() {
    private lateinit var binding: FragmentAccountsBinding
    private lateinit var logs:List<DairyTotal>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = inflate(inflater, R.layout.fragment_accounts, container, false)
        binding.lifecycleOwner = this
        binding.viewmodel = application?.let { ViewModelAccounts(it) }
        initLayout()
        return binding.root
    }
    private fun initLayout() {
        setHasOptionsMenu(false)

        binding.viewmodel?.run {
            listOfAccount = Transformations.map(allAccounts) { list -> list.map { it.number + " " + it.institute + " " + it.description } } as MutableLiveData<List<String?>>

            if(currentAccountId > 0) getAccount(currentAccountId).observeOnce { account ->
                account?.let { Transformations.map(listOfAccount) { list -> list.indexOf(account.number + " " + account.institute + " " + account.description) }
                    .observeOnce { index -> index?.let { indexOfAccount = index } }
                }
            }

            addOnPropertyChangedCallback(object: Observable.OnPropertyChangedCallback() {
                override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                    when(propertyId) {
                        BR.indexOfAccount -> onIndexOfAccountChanged()
                    }
                }
            })
        }

        binding.recyclerviewFragmentAccounts.run {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@FragmentAccounts.context)
        }

        binding.floatingFragmentAccounts.setOnClickListener {
            binding.viewmodel?.run {
                if(indexOfAccount < 0) Toast.makeText(activity.baseContext, R.string.toast_warning_input, Toast.LENGTH_SHORT).show()
                else showDialogInout(MyCalendar.getToday())
            }
        }
    }

    private fun showDialogInout(date: String?) {
        val dialog = DialogInOut().newInstance(object : DialogInOut.Complete {
            override fun onComplete(select: Int) {
                binding.viewmodel?.run {
                    when(select) {
                        0 -> replaceFragment(FragmentEditInoutKRW().initInstance(currentAccountId, date))
                        1 -> replaceFragment(FragmentEditInoutForeign().initInstance(currentAccountId, date))
                        2 -> replaceFragment(FragmentAccountTransfer().initInstance(currentAccountId))
                    }
                }
            }
        })
        dialog.show(mFragmentManager, "dialog")
    }

    private fun itemClicked(position: Int) {
        showDialogInout(logs[position].date)
    }

    fun onIndexOfAccountChanged() {
        binding.viewmodel?.run {
            allAccounts.observeOnce { list ->
                list?.let {
                    list[indexOfAccount].id?.let {
                        currentAccountId = it
                        getDairyTotalOrderByDate(currentAccountId).observeOnce { logs ->
                            logs?.let {
                                this@FragmentAccounts.logs = logs
                                binding.recyclerviewFragmentAccounts.run {
                                    adapter = RecyclerViewAdapterAccounts(logs) { position ->
                                        itemClicked(position)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}