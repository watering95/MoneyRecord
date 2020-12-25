package com.watering.moneyrecord.fragments

import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.watering.moneyrecord.R
import com.watering.moneyrecord.entities.Account
import com.watering.moneyrecord.view.RecyclerViewAdapterManagementAccount
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FragmentManagementAccount : ParentFragment() {
    private lateinit var mView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mView = inflater.inflate(R.layout.fragment_management_account, container, false)
        initLayout()
        return mView
    }
    private fun initLayout() {
        mActionBar?.setTitle(R.string.title_management_account)

        setHasOptionsMenu(false)

        mViewModel.allAccounts.observe(viewLifecycleOwner, { accounts -> accounts?.let {
            mView.findViewById<RecyclerView>(R.id.recyclerview_fragment_management_account).apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(mView.context)
                adapter = RecyclerViewAdapterManagementAccount(it) { position -> itemClicked(it[position]) }
            }
        } })

        val floating = mView.findViewById<FloatingActionButton>(R.id.floating_fragment_management_account)
        floating.setOnClickListener { replaceFragment(FragmentEditAccount().initInstance(Account())) }
    }
    private fun itemClicked(item: Account) {
        replaceFragment(FragmentEditAccount().initInstance(item))
    }
}