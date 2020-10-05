package com.watering.moneyrecord.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.watering.moneyrecord.MainActivity
import com.watering.moneyrecord.R
import com.watering.moneyrecord.entities.Group
import com.watering.moneyrecord.view.RecyclerViewAdapterManagementGroup
import com.watering.moneyrecord.viewmodel.ViewModelApp
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FragmentManagementGroup : Fragment() {
    private lateinit var mView: View
    private lateinit var mViewModel: ViewModelApp

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_management_group, container, false)
        initLayout()
        return mView
    }
    private fun initLayout() {
        val activity = activity as MainActivity
        mViewModel = activity.mViewModel

        activity.supportActionBar?.setTitle(R.string.title_management_group)

        setHasOptionsMenu(false)

        mViewModel.allGroups.observe(viewLifecycleOwner, Observer { groups -> groups?.let {
            mView.findViewById<RecyclerView>(R.id.recyclerview_fragment_management_group).run {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(mView.context)
                adapter = RecyclerViewAdapterManagementGroup(it) { position -> itemClicked(it[position]) }
            }
        } })

        val floating = mView.findViewById<FloatingActionButton>(R.id.floating_fragment_management_group)
        floating.setOnClickListener {
            mViewModel.replaceFragment(fragmentManager!!, FragmentEditGroup().initInstance(Group()))
        }
    }
    private fun itemClicked(item: Group) {
        mViewModel.replaceFragment(fragmentManager!!, FragmentEditGroup().initInstance(item))
    }
}