package com.watering.moneyrecord.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil.inflate
import androidx.databinding.Observable
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.Transformations
import com.watering.moneyrecord.R
import com.watering.moneyrecord.databinding.FragmentHomeBinding
import com.watering.moneyrecord.view.PagerAdapterHome
import com.watering.moneyrecord.viewmodel.ViewModelApp.Companion.currentGroupId
import com.watering.moneyrecord.viewmodel.ViewModelHome

class FragmentHome : ParentFragment() {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = inflate(inflater, R.layout.fragment_home, container, false)
        binding.lifecycleOwner = this
        binding.viewmodel = application?.let { ViewModelHome(it) }
        processing?.initHome()
        initLayout()
        return binding.root
    }
    private fun initLayout() {
        setHasOptionsMenu(false)

        binding.viewmodel?.run {
            if(currentGroupId > 0) getGroup(currentGroupId).observeOnce { group ->
                group?.let {
                    Transformations.map(listOfGroup) { list -> list.indexOf(group.name) }
                        .observeOnce { index -> index?.let { indexOfGroup = index } }
                }
            }

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
        binding.viewmodel?.run {
            if(indexOfGroup == 0) {
                currentGroupId = -1
                allHomes.observeOnce { listOfHomes -> listOfHomes?.let {
                    totalEvaluation = 0
                    totalPrincipal = 0
                    it.forEach { home ->
                        totalEvaluation += home.evaluationKRW!!
                        totalPrincipal += home.principalKRW!!
                    }
                } }
            } else {
                listOfGroup.observeOnce { listOfGroup -> listOfGroup?.let {
                    getGroup(it[indexOfGroup]).observeOnce { group -> group?.let {
                            currentGroupId = group.id!!
                        }
                    }
                    getHomesByGroup(it[indexOfGroup]).observeOnce { listOfHomes -> listOfHomes?.let {
                        totalEvaluation = 0
                        totalPrincipal = 0
                        listOfHomes.forEach { home ->
                            totalEvaluation += home.evaluationKRW!!
                            totalPrincipal += home.principalKRW!!
                        }
                    } }
                } }
            }
            binding.viewpagerFragmentHome.run {
                if(indexOfGroup == 0) (adapter as PagerAdapterHome).setGroup("")
                else listOfGroup.observeOnce { listOfGroup -> listOfGroup?.let {
                    (adapter as PagerAdapterHome).setGroup(it[indexOfGroup])
                } }
                adapter!!.notifyDataSetChanged()
            }
        }
    }
}