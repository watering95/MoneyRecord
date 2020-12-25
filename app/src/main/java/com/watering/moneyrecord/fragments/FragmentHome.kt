package com.watering.moneyrecord.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil.inflate
import androidx.databinding.Observable
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.LiveData
import com.watering.moneyrecord.R
import com.watering.moneyrecord.databinding.FragmentHomeBinding
import com.watering.moneyrecord.entities.Home
import com.watering.moneyrecord.view.PagerAdapterHome
import com.watering.moneyrecord.viewmodel.ViewModelApp.Companion.currentGroupId
import com.watering.moneyrecord.viewmodel.ViewModelHome

class FragmentHome : ParentFragment() {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
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
            getGroup(getCurrentGroupId()).observeOnce { group -> group?.let {
                listOfGroup.observeOnce { list -> list?.let {
                    indexOfGroup = list.indexOf(group.name)
                } }
            }}
            addOnPropertyChangedCallback(object: Observable.OnPropertyChangedCallback() {
                override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                    when(propertyId) {
                        BR.indexOfGroup -> onChangedIndexOfGroup()
                    }
                }
            })
        }
    }

    fun onChangedIndexOfGroup() {
        binding.viewmodel?.run {
            if(indexOfGroup == 0) {
                currentGroupId = -1
                changeList(allHomes)
            } else {
                listOfGroup.observeOnce { listOfGroup -> listOfGroup?.let {
                    getGroup(it[indexOfGroup]).observeOnce { group -> group?.let {
                        currentGroupId = group.id!!
                        changeList(getHomesByGroup(currentGroupId))
                    } }
                } }
            }
        }
    }

    private fun changeList(liveDataList: LiveData<List<Home>>)
    {
        binding.viewmodel?.run {
            liveDataList.observeOnce { listOfHomes -> listOfHomes?.let {
                totalEvaluation = 0
                totalPrincipal = 0
                listOfHomes.forEach { home ->
                    totalEvaluation += home.evaluationKRW!!
                    totalPrincipal += home.principalKRW!!
                }
                binding.viewpagerFragmentHome.run {
                    adapter = PagerAdapterHome(childFragmentManager)
                    adapter!!.notifyDataSetChanged()
                }
            } }
        }
    }
}