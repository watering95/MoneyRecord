package com.watering.moneyrecord.fragments

import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.watering.moneyrecord.R
import com.watering.moneyrecord.view.RecyclerViewAdapterManagementMain

class FragmentManagement : ParentFragment() {
    private lateinit var mView: View

    private val mapOfFragments = mutableMapOf<Int, Fragments>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mView = inflater.inflate(R.layout.fragment_management, container, false)

        mapOfFragments[0] = Fragments("User",FragmentManagementUser())
        mapOfFragments[1] = Fragments("Group",FragmentManagementGroup())
        mapOfFragments[2] = Fragments("Account",FragmentManagementAccount())
        mapOfFragments[3] = Fragments("CategoryMain",FragmentManagementCategoryMain())
        mapOfFragments[4] = Fragments("CategorySub",FragmentManagementCategorySub())
        mapOfFragments[5] = Fragments("Card",FragmentManagementCard())
        mapOfFragments[6] = Fragments("DB",FragmentManagementDB())

        initLayout()

        return mView
    }
    private fun initLayout() {
        setHasOptionsMenu(false)

        mView.findViewById<RecyclerView>(R.id.recyclerview_fragment_management_main).run {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(mView.context)
            adapter = RecyclerViewAdapterManagementMain(mapOfFragments.values.map { it.title } as ArrayList<String>) { position -> itemClicked(position) }
        }
    }
    private fun itemClicked(position: Int) {
        mViewModel.run {
            mapOfFragments[position]?.fragment?.let { replaceFragment(it) }
        }
    }
}