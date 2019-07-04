package com.watering.moneyrecord.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.watering.moneyrecord.MainActivity
import com.watering.moneyrecord.R
import com.watering.moneyrecord.view.PagerAdapterBook

class FragmentBook : Fragment() {
    private lateinit var mView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_book, container, false)
        initLayout()
        return mView
    }
    private fun initLayout() {
        val activity = activity as MainActivity
        activity.supportActionBar?.setTitle(R.string.title_book)

        setHasOptionsMenu(false)
        val viewPager = mView.findViewById<ViewPager>(R.id.viewpager_fragment_book)
        viewPager.adapter = PagerAdapterBook(childFragmentManager)
        viewPager.addOnPageChangeListener(object:ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                when(position) {
                    0 -> activity.supportActionBar?.setTitle(R.string.title_spend)
                    1 -> activity.supportActionBar?.setTitle(R.string.title_income)
                }
            }
        })
    }
}