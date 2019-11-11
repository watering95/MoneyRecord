package com.watering.moneyrecord.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil.inflate
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.watering.moneyrecord.MainActivity
import com.watering.moneyrecord.R
import com.watering.moneyrecord.databinding.FragmentBookSpendBinding
import com.watering.moneyrecord.databinding.FragmentBookStatisticsBinding
import com.watering.moneyrecord.entities.Spend
import com.watering.moneyrecord.entities.StatisticsMonthly
import com.watering.moneyrecord.model.MyCalendar
import com.watering.moneyrecord.view.RecyclerViewAdapterBookSpend
import com.watering.moneyrecord.view.RecyclerViewAdapterBookStatistics
import com.watering.moneyrecord.viewmodel.ViewModelApp
import java.util.*

class FragmentBookStatistics : Fragment() {
    private lateinit var mViewModel: ViewModelApp
    private lateinit var binding: FragmentBookStatisticsBinding
    private val mFragmentManager by lazy { (activity as MainActivity).supportFragmentManager }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = inflate(inflater, R.layout.fragment_book_statistics, container, false)
        initLayout()
        return binding.root
    }

    private fun initLayout() {
        val activity = activity as MainActivity

        mViewModel = activity.mViewModel
        updateList()

        setHasOptionsMenu(false)
    }

    private fun updateList() {
        mViewModel.run {
            sumOfMonthlyStatistics().observe(this@FragmentBookStatistics, Observer { statistics -> statistics?.let {
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