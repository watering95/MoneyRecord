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
                        0 -> replaceFragment(FragmentEditInoutKRW().initInstance(idAccount, date))
                        1 -> replaceFragment(FragmentEditInoutForeign().initInstance(idAccount, date))
                        2 -> replaceFragment(FragmentAccountTransfer().initInstance(idAccount))
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
            allAccounts.observe(this@FragmentAccounts, { list -> list?.let {
                list[indexOfAccount].id?.let {
                    idAccount = it
                    getDairyTotalOrderByDate(idAccount).observe(this@FragmentAccounts, { logs -> logs?.let {
                        this@FragmentAccounts.logs = logs
                        binding.recyclerviewFragmentAccounts.run {
                            adapter = RecyclerViewAdapterAccounts(logs) { position -> itemClicked(position) }
                        }
                    } })
                }
            } })
        }
    }
}