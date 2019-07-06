package com.watering.moneyrecord.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.watering.moneyrecord.R
import com.watering.moneyrecord.entities.Card

class RecyclerViewAdapterManagementCard(val lists:List<Card>, private val clickListener: (Int) -> Unit): RecyclerView.Adapter<RecyclerViewAdapterManagementCard.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardView = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_management_card, parent, false)

        return ViewHolder(cardView)

    }

    override fun getItemCount(): Int {
        return lists.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(lists[position], position, clickListener)
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView = view.findViewById(R.id.text_name_card_management_card)
        private var company: TextView = view.findViewById(R.id.text_company_card_management_card)
        var number: TextView = view.findViewById(R.id.text_number_card_management_card)

        fun bind(card: Card, position: Int, clickListener: (Int) -> Unit) {
            name.text = card.name
            company.text = card.company
            number.text = card.number
            view.setOnClickListener { clickListener(position) }
        }
    }
}