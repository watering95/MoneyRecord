package com.watering.moneyrecord.fragments

import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.watering.moneyrecord.R
import com.watering.moneyrecord.entities.Card
import com.watering.moneyrecord.view.RecyclerViewAdapterManagementCard
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FragmentManagementCard : ParentFragment() {
    private lateinit var mView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_management_card, container, false)
        initLayout()
        return mView
    }
    private fun initLayout() {
        mActionBar?.setTitle(R.string.title_management_card)

        setHasOptionsMenu(false)

        mViewModel.allCards.observe(viewLifecycleOwner, { cards -> cards?.let {
            mView.findViewById<RecyclerView>(R.id.recyclerview_fragment_management_card).apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(mView.context)
                adapter = RecyclerViewAdapterManagementCard(it) { position -> itemClicked(it[position]) }
            }
        } })

        val floating = mView.findViewById<FloatingActionButton>(R.id.floating_fragment_management_card)
        floating.setOnClickListener { replaceFragment(FragmentEditCard().initInstance(Card())) }
    }
    private fun itemClicked(item: Card) {
        replaceFragment(FragmentEditCard().initInstance(item))
    }
}