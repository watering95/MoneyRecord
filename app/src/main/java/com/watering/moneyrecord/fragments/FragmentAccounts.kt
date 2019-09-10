package com.watering.moneyrecord.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil.inflate
import androidx.databinding.Observable
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager

import com.watering.moneyrecord.MainActivity
import com.watering.moneyrecord.R
import com.watering.moneyrecord.databinding.FragmentAccountsBinding
import com.watering.moneyrecord.entities.DairyTotal
import com.watering.moneyrecord.model.MyCalendar
import com.watering.moneyrecord.view.RecyclerViewAdapterAccounts
import com.watering.moneyrecord.viewmodel.ViewModelAccounts

class FragmentAccounts : Fragment() {
    private val mViewModel by lazy { (activity as MainActivity).mViewModel }
    private lateinit var binding: FragmentAccountsBinding
    private val mFragmentManager by lazy { (activity as MainActivity).supportFragmentManager }
    private lateinit var logs:List<DairyTotal>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = inflate(inflater, R.layout.fragment_accounts, container, false)
        binding.lifecycleOwner = this
        binding.viewmodel = ViewModelProviders.of(this).get(ViewModelAccounts::class.java)
        initLayout()
        return binding.root
    }
    private fun initLayout() {
        setHasOptionsMenu(false)

        binding.viewmodel?.run {
            listOfAccount = Transformations.map(allAccounts) { list -> list.map { it.number } } as MutableLiveData<List<String?>>

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
                if(indexOfAccount < 0) {
                    Toast.makeText(activity?.baseContext, R.string.toast_warning_input, Toast.LENGTH_SHORT).show()
                } else {
                    val dialog = DialogInOut().newInstance(object : DialogInOut.Complete {
                        override fun onComplete(select: Int) {
                            val today = MyCalendar.getToday()
                            when(select) {
                                0 -> mViewModel.replaceFragment(mFragmentManager, FragmentEditInoutKRW().initInstance(idAccount, today))
                                1 -> mViewModel.replaceFragment(mFragmentManager, FragmentEditInoutForeign().initInstance(idAccount, today))
                                2 -> {}
                            }
                        }
                    })
                    fragmentManager?.let { it -> dialog.show(it, "dialog") }
                }

            }
        }
    }
    private fun itemClicked(position: Int) {
        binding.viewmodel?.run {
            val dialog = DialogInOut().newInstance(object : DialogInOut.Complete {
                override fun onComplete(select: Int) {
                    val date = logs[position].date
                    when(select) {
                        0 -> mViewModel.replaceFragment(mFragmentManager, FragmentEditInoutKRW().initInstance(binding.viewmodel?.idAccount, date))
                        1 -> mViewModel.replaceFragment(mFragmentManager, FragmentEditInoutForeign().initInstance(binding.viewmodel?.idAccount, date))
                        2 -> {}
                    }
                }
            })
            fragmentManager?.let { dialog.show(it, "dialog") }
        }
    }

    fun onIndexOfAccountChanged() {
        binding.viewmodel?.run {
            Transformations.switchMap(listOfAccount) { list -> list?.let {
                Transformations.map(getAccountByNumber(list[indexOfAccount])) { account -> account.id } }
            }.observe(this@FragmentAccounts, Observer { id -> id?.let {
                idAccount = id
                getDairyTotalOrderByDate(idAccount).observe(this@FragmentAccounts, Observer { logs -> logs?.let {
                    this@FragmentAccounts.logs = logs
                    binding.recyclerviewFragmentAccounts.run {
                        adapter = RecyclerViewAdapterAccounts(logs) { position -> itemClicked(position) }
                    }
                } })
            } })
        }
    }
}