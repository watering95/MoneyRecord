package com.watering.moneyrecord.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil.inflate
import androidx.databinding.Observable
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import com.watering.moneyrecord.MainActivity
import com.watering.moneyrecord.R
import com.watering.moneyrecord.databinding.FragmentHomeBinding
import com.watering.moneyrecord.model.Processing
import com.watering.moneyrecord.view.PagerAdapterHome
import com.watering.moneyrecord.viewmodel.ViewModelHome

class FragmentHome : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var processing: Processing

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = inflate(inflater, R.layout.fragment_home, container, false)
        binding.lifecycleOwner = this
        binding.viewmodel = ViewModelProviders.of(this).get(ViewModelHome::class.java)
        processing = Processing(binding.viewmodel, fragmentManager)
        processing.initHome()
        initLayout()
        return binding.root
    }
    private fun initLayout() {
        setHasOptionsMenu(false)

        binding.viewmodel?.run {
            addOnPropertyChangedCallback(object: Observable.OnPropertyChangedCallback() {
                override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                    when(propertyId) {
                        BR.indexOfGroup -> onChangedIndexOfGroup()
                    }
                }
            })
        }
        binding.viewpagerFragmentHome.run{
            adapter = PagerAdapterHome(childFragmentManager)
        }
    }

    fun onChangedIndexOfGroup() {
        val viewModel = (activity as MainActivity).mViewModel
        binding.viewmodel?.run {
            if(indexOfGroup == 0) {
                viewModel.currentGroupId = -1
                allHomes.observe(this@FragmentHome, Observer { listOfHomes -> listOfHomes?.let {
                    totalEvaluation = 0
                    totalPrincipal = 0
                    it.forEach { home ->
                        totalEvaluation += home.evaluationKRW!!
                        totalPrincipal += home.principalKRW!!
                    }
                } })
            } else {
                listOfGroup.observe(this@FragmentHome, Observer { listOfGroup -> listOfGroup?.let {
                    getGroup(it[indexOfGroup]).observe(this@FragmentHome, Observer { group -> group?.let { viewModel.currentGroupId = group.id } })
                    getHomesByGroup(it[indexOfGroup]).observe(this@FragmentHome, Observer { listOfHomes -> listOfHomes?.let {
                        totalEvaluation = 0
                        totalPrincipal = 0
                        listOfHomes.forEach { home ->
                            totalEvaluation += home.evaluationKRW!!
                            totalPrincipal += home.principalKRW!!
                        }
                    } })
                } })
            }
            binding.viewpagerFragmentHome.run {
                if(indexOfGroup == 0) (adapter as PagerAdapterHome).setGroup("")
                else listOfGroup.observe(this@FragmentHome, Observer { listOfGroup -> listOfGroup?.let {
                    (adapter as PagerAdapterHome).setGroup(it[indexOfGroup])
                } })
            }
        }
    }
}