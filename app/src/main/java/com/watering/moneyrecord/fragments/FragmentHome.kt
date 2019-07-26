package com.watering.moneyrecord.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil.inflate
import androidx.databinding.Observable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.watering.moneyrecord.BR
import com.watering.moneyrecord.MainActivity
import com.watering.moneyrecord.R
import com.watering.moneyrecord.databinding.FragmentHomeBinding
import com.watering.moneyrecord.entities.Home
import com.watering.moneyrecord.model.ModelCalendar
import com.watering.moneyrecord.view.RecyclerViewAdapterHome
import com.watering.moneyrecord.viewmodel.ViewModelHome

class FragmentHome : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val mFragmentManager by lazy { (activity as MainActivity).supportFragmentManager as FragmentManager }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = inflate(inflater, R.layout.fragment_home, container, false)
        binding.lifecycleOwner = this
        binding.viewmodel = ViewModelProviders.of(this).get(ViewModelHome::class.java)
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
    }

    private fun itemClicked(position: Int) {
        binding.viewmodel?.run {
        }
    }

    private fun onChangedRecyclerView(list: List<Home>) {
        binding.viewmodel?.run {
            binding.recyclerviewFragmentHome.run {
                if(adapter == null) {
                    setHasFixedSize(true)
                    layoutManager = LinearLayoutManager(this@FragmentHome.context)
                }
                adapter = RecyclerViewAdapterHome(list, totalEvaluation.toDouble()) { position -> itemClicked(position) }
            }
        }
    }

    fun onChangedIndexOfGroup() {
        binding.viewmodel?.run {
            if(indexOfGroup == 0) {
                allHomes.observe(this@FragmentHome, Observer { listOfHomes -> listOfHomes?.let {
                    it.forEach {
                        totalEvaluation += it.evaluationKRW!!
                    }
                    onChangedRecyclerView(it)
                } })
            } else {
                listOfGroup.observe(this@FragmentHome, Observer { listOfGroup -> listOfGroup?.let {
                    getHomesByGroup(it[indexOfGroup]).observe(this@FragmentHome, Observer { listOfHome -> listOfHome?.let {
                        listOfHome.forEach {
                            totalEvaluation += it.evaluationKRW!!
                        }
                        onChangedRecyclerView(it)
                    } })
                } })
            }
        }
    }

    private fun <T> LiveData<T>.observeOnce(observer: Observer<T>) {
        observeForever(object: Observer<T> {
            override fun onChanged(t: T) {
                observer.onChanged(t)
                removeObserver(this)
            }
        })
    }
}