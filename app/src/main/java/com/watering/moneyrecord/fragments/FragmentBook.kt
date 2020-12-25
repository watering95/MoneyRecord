package com.watering.moneyrecord.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil.inflate
import androidx.viewpager.widget.ViewPager
import com.watering.moneyrecord.R
import com.watering.moneyrecord.databinding.FragmentBookBinding
import com.watering.moneyrecord.model.MyCalendar
import com.watering.moneyrecord.view.PagerAdapterBook
import com.watering.moneyrecord.viewmodel.ViewModelBook

class FragmentBook : ParentFragment() {
    private lateinit var binding: FragmentBookBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = inflate(inflater, R.layout.fragment_book, container, false)
        binding.viewmodel = application?.let { ViewModelBook(it) }
        initLayout()
        return binding.root
    }
    private fun initLayout() {
        mActionBar?.setTitle(R.string.title_book)

        setHasOptionsMenu(false)
        binding.viewmodel?.run {
            val firstDate = MyCalendar.getFirstDate()
            val endDate = MyCalendar.getToday()
            sumOfIncome(firstDate, endDate).observe(viewLifecycleOwner, { sum -> sum?.let { totalIncome = it } })
            sumOfSpend(firstDate, endDate).observe(viewLifecycleOwner, { sum -> sum?.let { totalSpend = it } })
        }

        binding.viewpagerFragmentBook.run {
            adapter = PagerAdapterBook(childFragmentManager)
            addOnPageChangeListener(object:ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {

                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

                }

                override fun onPageSelected(position: Int) {
                    when(position) {
                        0 -> mActionBar?.setTitle(R.string.title_spend)
                        1 -> mActionBar?.setTitle(R.string.title_income)
                        2 -> mActionBar?.setTitle(R.string.title_statistics)
                    }
                }
            })

        }
    }
}