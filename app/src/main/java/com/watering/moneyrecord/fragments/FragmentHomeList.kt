package com.watering.moneyrecord.fragments

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil.inflate
import androidx.recyclerview.widget.LinearLayoutManager
import com.watering.moneyrecord.R
import com.watering.moneyrecord.databinding.FragmentHomeListBinding
import com.watering.moneyrecord.entities.Home
import com.watering.moneyrecord.view.RecyclerViewAdapterHomeList

class FragmentHomeList : ParentFragment() {
    private lateinit var binding: FragmentHomeListBinding
    var group: String? = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = inflate(inflater, R.layout.fragment_home_list, container, false)
        initLayout()
        return binding.fragmentHomeList
    }

    private fun initLayout() {
        updateList()

        setHasOptionsMenu(false)
    }

    private fun updateList() {
        (if(group == "") mViewModel.allHomes else mViewModel.getHomesByGroup(group))
        .observeOnce { listOfHomes ->
            listOfHomes?.let {
                var totalEvaluation = 0

                it.forEach { home -> totalEvaluation += home.evaluationKRW!! }

                onChangedRecyclerView(it, totalEvaluation)
            }
        }
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