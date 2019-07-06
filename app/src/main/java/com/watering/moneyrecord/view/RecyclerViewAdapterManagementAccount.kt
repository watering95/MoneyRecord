package com.watering.moneyrecord.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.watering.moneyrecord.R
import com.watering.moneyrecord.entities.Account

class RecyclerViewAdapterManagementAccount(val lists:List<Account>, private val clickListener: (Int) -> Unit): RecyclerView.Adapter<RecyclerViewAdapterManagementAccount.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardView = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_management_account, parent, false)

        return ViewHolder(cardView)

    }

    override fun getItemCount(): Int {
        return lists.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(lists[position], position, clickListener)
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var number: TextView = view.findViewById(R.id.text_number_card_management_account)
        private var institute: TextView = view.findViewById(R.id.text_institute_card_management_account)
        private var description: TextView = view.findViewById(R.id.text_description_card_management_account)

        fun bind(account: Account, position: Int, clickListener: (Int) -> Unit) {
            number.text = account.number
            institute.text = account.institute
            description.text = account.description
            view.setOnClickListener { clickListener(position) }
        }
    }
}