package com.watering.moneyrecord.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.watering.moneyrecord.R
import com.watering.moneyrecord.entities.Income
import java.text.DecimalFormat

class RecyclerViewAdapterBookIncome(val lists:List<Income>, private val clickListener: (Int) -> Unit): RecyclerView.Adapter<RecyclerViewAdapterBookIncome.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardView = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_book_income, parent, false)

        return ViewHolder(cardView)

    }

    override fun getItemCount(): Int {
        return lists.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(lists[position], position, clickListener)
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private var detail: TextView = view.findViewById(R.id.text_detail_card_book_income)
        var amount: TextView = view.findViewById(R.id.text_amount_card_book_income)

        fun bind(income: Income, position: Int, clickListener: (Int) -> Unit) {
            val df = DecimalFormat("#,###")
            detail.text = income.details
            amount.text = df.format(income.amount)
            view.setOnClickListener { clickListener(position) }
        }
    }
}