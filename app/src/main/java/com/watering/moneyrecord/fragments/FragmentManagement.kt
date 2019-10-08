package com.watering.moneyrecord.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.watering.moneyrecord.MainActivity
import com.watering.moneyrecord.R
import com.watering.moneyrecord.view.RecyclerViewAdapterManagementMain
import com.watering.moneyrecord.viewmodel.ViewModelApp

class FragmentManagement : Fragment() {
    private lateinit var mView: View
    private lateinit var mViewModel: ViewModelApp

    private val mFragmentManagementGroup = FragmentManagementGroup()
    private val mFragmentManagementAccount = FragmentManagementAccount()
    private val mFragmentManagementCategoryMain = FragmentManagementCategoryMain()
    private val mFragmentManagementCategorySub = FragmentManagementCategorySub()
    private val mFragmentManagementCard = FragmentManagementCard()
    private val mFragmentManagementDB = FragmentManagementDB()

    val lists = arrayListOf("Group","Account","CategoryMain","CategorySub","Card","DB", "test")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_management, container, false)
        initLayout()
        return mView
    }
    private fun initLayout() {
        setHasOptionsMenu(false)

        mViewModel = (activity as MainActivity).mViewModel

        mView.findViewById<RecyclerView>(R.id.recyclerview_fragment_management_main).run {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(mView.context)
            adapter = RecyclerViewAdapterManagementMain(lists) { position -> itemClicked(position) }
        }
    }
    private fun itemClicked(position: Int) {
        mViewModel.run {
            when (position) {
                0 -> replaceFragment(fragmentManager!!, mFragmentManagementGroup)
                1 -> replaceFragment(fragmentManager!!, mFragmentManagementAccount)
                2 -> replaceFragment(fragmentManager!!, mFragmentManagementCategoryMain)
                3 -> replaceFragment(fragmentManager!!, mFragmentManagementCategorySub)
                4 -> replaceFragment(fragmentManager!!, mFragmentManagementCard)
                5 -> replaceFragment(fragmentManager!!, mFragmentManagementDB)
                6 -> test()
                else -> {}
            }
        }
    }

    private fun test() {
        mViewModel.run {
            sumOfPrincipal("2019-10-07").observeOnce(Observer { v -> v?.let {
                it
            } })
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