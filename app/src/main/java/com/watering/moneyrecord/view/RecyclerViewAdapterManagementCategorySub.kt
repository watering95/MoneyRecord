package com.watering.moneyrecord.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.watering.moneyrecord.R

class RecyclerViewAdapterManagementCategorySub(val lists:List<String>, private val clickListener: (Int) -> Unit): RecyclerView.Adapter<RecyclerViewAdapterManagementCategorySub.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardView = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_management_category_sub, parent, false)

        return ViewHolder(cardView)

    }

    override fun getItemCount(): Int {
        return lists.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(lists[position], position, clickListener)
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView = view.findViewById(R.id.text_name_card_management_category_sub)

        fun bind(list: String, position: Int, clickListener: (Int) -> Unit) {
            name.text = list
            view.setOnClickListener { clickListener(position) }
        }
    }
}