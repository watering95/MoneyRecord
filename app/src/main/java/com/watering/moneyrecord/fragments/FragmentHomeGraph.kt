package com.watering.moneyrecord.fragments

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil.inflate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.watering.moneyrecord.MainActivity
import com.watering.moneyrecord.R
import com.watering.moneyrecord.databinding.FragmentHomeGraphBinding
import com.watering.moneyrecord.viewmodel.ViewModelApp

class FragmentHomeGraph : Fragment() {
    private lateinit var mViewModel: ViewModelApp
    private lateinit var binding: FragmentHomeGraphBinding
    private val mFragmentManager by lazy { (activity as MainActivity).supportFragmentManager as FragmentManager }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = inflate(inflater, R.layout.fragment_home_graph, container, false)
        initLayout()
        return binding.root
    }
    private fun initLayout() {
        val activity = activity as MainActivity

        mViewModel = activity.mViewModel

        setHasOptionsMenu(false)
    }

}