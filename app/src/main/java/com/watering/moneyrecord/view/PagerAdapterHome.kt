package com.watering.moneyrecord.view

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.watering.moneyrecord.fragments.FragmentBookIncome
import com.watering.moneyrecord.fragments.FragmentBookSpend
import com.watering.moneyrecord.fragments.FragmentHomeGraph
import com.watering.moneyrecord.fragments.FragmentHomeList

class PagerAdapterHome(fm: FragmentManager): FragmentPagerAdapter(fm) {
    private val fragmentHomeList = FragmentHomeList()
    private val fragmentHomeGraph = FragmentHomeGraph()
    private val titles = arrayOf("목록", "그래프")

    fun setGroup(group: String?) {
        fragmentHomeList.group = group
        fragmentHomeList.updateList()
    }

    override fun getItem(position: Int): Fragment {
        return when(position) {
            0 -> { fragmentHomeList }
            1 -> { fragmentHomeGraph }
            else -> { fragmentHomeList }
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titles[position]
    }
}