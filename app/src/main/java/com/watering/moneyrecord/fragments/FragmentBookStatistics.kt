package com.watering.moneyrecord.fragments

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil.inflate
import androidx.recyclerview.widget.LinearLayoutManager
import com.watering.moneyrecord.R
import com.watering.moneyrecord.databinding.FragmentBookStatisticsBinding
import com.watering.moneyrecord.entities.StatisticsMonthly
import com.watering.moneyrecord.view.RecyclerViewAdapterBookStatistics
import com.watering.moneyrecord.viewmodel.ViewModelBookStatistics

class FragmentBookStatistics : ParentFragment() {
    private lateinit var binding: FragmentBookStatisticsBinding
    private val viewModel by lazy { application?.let { ViewModelBookStatistics(it) } }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = inflate(inflater, R.layout.fragment_book_statistics, container, false)
        initLayout()
        return binding.root
    }

    private fun initLayout() {
        updateList()

        setHasOptionsMenu(false)
    }

    private fun updateList() {
        viewModel?.run {
            sumOfMonthlyStatistics().observe(viewLifecycleOwner, { statistics -> statistics?.let {
                onChangedRecyclerView(it)
            } })
        }
    }

    private fun onChangedRecyclerView(list: List<StatisticsMonthly>) {
        binding.recyclerviewFragmentBookStatistics.run {
            if(adapter == null) {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(this@FragmentBookStatistics.context)
            }
            adapter = RecyclerViewAdapterBookStatistics(list) { position -> itemClicked(position) }
        }
    }

    private fun itemClicked(item: Int) {

    }
}