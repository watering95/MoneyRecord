package com.watering.moneyrecord.fragments

import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.watering.moneyrecord.MainActivity
import com.watering.moneyrecord.R
import com.watering.moneyrecord.entities.CategoryMain
import com.watering.moneyrecord.view.RecyclerViewAdapterManagementCategoryMain
import com.watering.moneyrecord.viewmodel.ViewModelApp
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FragmentManagementCategoryMain : Fragment() {
    private lateinit var mView: View
    private lateinit var mViewModel: ViewModelApp

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_management_category_main, container, false)
        initLayout()
        return mView
    }
    private fun initLayout() {
        val activity = activity as MainActivity
        mViewModel = activity.mViewModel

        activity.supportActionBar?.setTitle(R.string.title_management_categorymain)

        setHasOptionsMenu(false)

        val button = mView.findViewById<Button>(R.id.button_automatic_fragment_management_category_main)
        button.setOnClickListener { generateBasicCategory() }

        mViewModel.allCatMains.observe(this, Observer { categoryMains -> categoryMains?.let {
            mView.findViewById<RecyclerView>(R.id.recyclerview_fragment_management_category_main).run {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(mView.context)
                adapter = RecyclerViewAdapterManagementCategoryMain(it) { position -> itemClicked(it[position]) }
            }
        } })

        val floating = mView.findViewById<FloatingActionButton>(R.id.floating_fragment_management_category_main)
        floating.setOnClickListener { mViewModel.replaceFragment(fragmentManager!!, FragmentEditCategoryMain().initInstance(CategoryMain())) }
    }
    private fun itemClicked(item: CategoryMain) {
        mViewModel.replaceFragment(fragmentManager!!, FragmentEditCategoryMain().initInstance(item))
    }
    private fun generateBasicCategory() {
        mViewModel.run {
            allCatMains.observeOnce( Observer { categoryMains ->
                if(categoryMains.isNotEmpty()) {
                    categoryOfSpend.keys.forEach {
                        var isSame = false
                        categoryMains.forEach { main -> if(main.name == it) isSame = true }
                        if(!isSame) {
                            val category = CategoryMain()
                            category.kind = "spend"
                            category.name = it
                            insert(category)
                        }
                    }
                    categoryOfIncome.keys.forEach {
                        var isSame = false
                        categoryMains.forEach { main -> if(main.name == it) isSame = true }
                        if(!isSame) {
                            val category = CategoryMain()
                            category.kind = "income"
                            category.name = it
                            insert(category)
                        }
                    }
                } else {
                    categoryOfSpend.keys.forEach {
                        val category = CategoryMain()
                        category.kind = "spend"
                        category.name = it
                        insert(category)
                    }
                    categoryOfIncome.keys.forEach {
                        val category = CategoryMain()
                        category.kind = "income"
                        category.name = it
                        insert(category)
                    }
                }
            })

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