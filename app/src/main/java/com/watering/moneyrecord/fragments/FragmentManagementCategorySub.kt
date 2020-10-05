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
import com.watering.moneyrecord.entities.CategorySub
import com.watering.moneyrecord.view.RecyclerViewAdapterManagementCategorySub
import com.watering.moneyrecord.viewmodel.ViewModelApp
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FragmentManagementCategorySub : Fragment() {
    private lateinit var mView: View
    private lateinit var mViewModel: ViewModelApp

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_management_category_sub, container, false)
        initLayout()
        return mView
    }
    private fun initLayout() {
        val activity = activity as MainActivity
        mViewModel = activity.mViewModel

        activity.supportActionBar?.setTitle(R.string.title_management_categorysub)

        setHasOptionsMenu(false)

        val button = mView.findViewById<Button>(R.id.button_automatic_fragment_management_category_sub)
        button.setOnClickListener { generateBasicCategory() }

        mViewModel.allCatSubs.observe(viewLifecycleOwner, Observer { categorySubs -> categorySubs?.let {
            mView.findViewById<RecyclerView>(R.id.recyclerview_fragment_management_category_sub).run {
                val list = mutableListOf<String>()
                it.forEach { catSub -> mViewModel.getCatMain(catSub.categoryMain).observeOnce(Observer {
                    list.add(catSub.name + " : " + it.name)
                    if(list.size == categorySubs.size) {
                        setHasFixedSize(true)
                        layoutManager = LinearLayoutManager(mView.context)
                        adapter = RecyclerViewAdapterManagementCategorySub(list) { position -> itemClicked(list[position]) }
                    }
                }) }
            }
        } })

        val floating = mView.findViewById<FloatingActionButton>(R.id.floating_fragment_management_category_sub)
        floating.setOnClickListener { mViewModel.replaceFragment(fragmentManager!!, FragmentEditCategorySub().initInstance(CategorySub())) }
    }
    private fun itemClicked(item: String) {
        mViewModel.run {
            val cat = item.split(" : ")
            getCatSub(cat[0], cat[1]).observeOnce(Observer { sub ->
                mViewModel.replaceFragment(fragmentManager!!, FragmentEditCategorySub().initInstance(sub))
            })
        }
    }
    private fun generateBasicCategory() {
        mViewModel.run {
            categoryOfSpend.keys.forEach {
                getCatMainByName(it).observeOnce(Observer { main ->
                    if(main != null) {
                        categoryOfSpend[it]?.forEach { sub ->
                            getCatSub(sub, it).observeOnce(Observer { catSub ->
                                if(catSub == null) {
                                    val category = CategorySub()
                                    category.categoryMain = main.id
                                    category.name = sub
                                    insert(category)
                                }
                            })
                        }
                    }
                })
            }
            categoryOfIncome.keys.forEach {
                getCatMainByName(it).observeOnce(Observer { main ->
                    if(main != null) {
                        categoryOfIncome[it]?.forEach { sub ->
                            getCatSub(sub, it).observeOnce(Observer { catSub ->
                                if(catSub == null) {
                                    val category = CategorySub()
                                    category.categoryMain = main.id
                                    category.name = sub
                                    insert(category)
                                }
                            })
                        }
                    }
                })
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