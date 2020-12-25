package com.watering.moneyrecord.view

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.watering.moneyrecord.fragments.FragmentBookIncome
import com.watering.moneyrecord.fragments.FragmentBookSpend
import com.watering.moneyrecord.fragments.FragmentBookStatistics

class PagerAdapterBook(fm: FragmentManager): FragmentPagerAdapter(fm) {
    private val fragmentBookSpend = FragmentBookSpend()
    private val fragmentBookIncome = FragmentBookIncome()
    private val fragmentBookStatistics = FragmentBookStatistics()
    private val titles = arrayOf("지출", "수입", "통계")

    override fun getItem(position: Int): Fragment {
        return when(position) {
            0 -> { fragmentBookSpend }
            1 -> { fragmentBookIncome }
            2 -> { fragmentBookStatistics }
            else -> { fragmentBookSpend }
        }
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence {
        return titles[position]
    }
}