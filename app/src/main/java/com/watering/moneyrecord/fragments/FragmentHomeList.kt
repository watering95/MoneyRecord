package com.watering.moneyrecord.fragments

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil.inflate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.watering.moneyrecord.MainActivity
import com.watering.moneyrecord.R
import com.watering.moneyrecord.databinding.FragmentHomeListBinding
import com.watering.moneyrecord.entities.Home
import com.watering.moneyrecord.entities.Income
import com.watering.moneyrecord.view.RecyclerViewAdapterHomeList
import com.watering.moneyrecord.viewmodel.ViewModelApp

class FragmentHomeList : Fragment() {
    private lateinit var mViewModel: ViewModelApp
    private lateinit var binding: FragmentHomeListBinding
    var group: String? = ""
    private val mFragmentManager by lazy { (activity as MainActivity).supportFragmentManager as FragmentManager }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = inflate(inflater, R.layout.fragment_home_list, container, false)
        initLayout()
        return binding.root
    }

    fun updateList() {
        (if(group == "") mViewModel.allHomes else mViewModel.getHomesByGroup(group))
            .observe(this, Observer { listOfHomes -> listOfHomes?.let {
                var totalEvaluation = 0

                it.forEach { home ->
                    totalEvaluation += home.evaluationKRW!!
                }

                onChangedRecyclerView(it, totalEvaluation)
            } })
    }

    private fun initLayout() {
        val activity = activity as MainActivity

        mViewModel = activity.mViewModel
        updateList()

        setHasOptionsMenu(false)
    }

    private fun itemClicked(item: Int) {

    }

    private fun onChangedRecyclerView(list: List<Home>, totalEvaluation: Int) {
        binding.recyclerviewFragmentHomeList.run {
            if(adapter == null) {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(this@FragmentHomeList.context)
            }
            adapter = RecyclerViewAdapterHomeList(list, totalEvaluation.toDouble()) { position -> itemClicked(position) }
        }
    }
}