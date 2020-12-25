package com.watering.moneyrecord.fragments

import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.watering.moneyrecord.R
import com.watering.moneyrecord.entities.CategoryMain
import com.watering.moneyrecord.view.RecyclerViewAdapterManagementCategoryMain
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FragmentManagementCategoryMain : ParentFragment() {
    private lateinit var mView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mView = inflater.inflate(R.layout.fragment_management_category_main, container, false)
        initLayout()
        return mView
    }
    private fun initLayout() {
        mActionBar?.setTitle(R.string.title_management_categorymain)

        setHasOptionsMenu(false)

        val button = mView.findViewById<Button>(R.id.button_automatic_fragment_management_category_main)
        button.setOnClickListener { generateBasicCategory() }

        mViewModel.allCatMains.observe(viewLifecycleOwner, { categoryMains -> categoryMains?.let {
            mView.findViewById<RecyclerView>(R.id.recyclerview_fragment_management_category_main).run {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(mView.context)
                adapter = RecyclerViewAdapterManagementCategoryMain(it) { position -> itemClicked(it[position]) }
            }
        } })

        val floating = mView.findViewById<FloatingActionButton>(R.id.floating_fragment_management_category_main)
        floating.setOnClickListener { replaceFragment(FragmentEditCategoryMain().initInstance(CategoryMain())) }
    }
    private fun itemClicked(item: CategoryMain) {
        replaceFragment(FragmentEditCategoryMain().initInstance(item))
    }
    private fun generateBasicCategory() {
        mViewModel.run {
            allCatMains.observeOnce { categoryMains ->
                if (categoryMains.isNotEmpty()) {
                    categoryOfSpend.keys.forEach {
                        var isSame = false
                        categoryMains.forEach { main -> if (main.name == it) isSame = true }
                        if (!isSame) {
                            val category = CategoryMain()
                            category.kind = "spend"
                            category.name = it
                            insert(category)
                        }
                    }
                    categoryOfIncome.keys.forEach {
                        var isSame = false
                        categoryMains.forEach { main -> if (main.name == it) isSame = true }
                        if (!isSame) {
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
            }

        }
    }
}