package com.watering.moneyrecord.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.watering.moneyrecord.R
import com.watering.moneyrecord.entities.DairyTotal
import java.text.DecimalFormat
import java.util.*

class RecyclerViewAdapterAccounts(val lists:List<DairyTotal>, private val clickListener: (Int) -> Unit): RecyclerView.Adapter<RecyclerViewAdapterAccounts.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardView = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_accounts, parent, false)

        return ViewHolder(cardView)

    }

    override fun getItemCount(): Int {
        return lists.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(lists[position], position, clickListener)
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private var date: TextView = view.findViewById(R.id.text_date_card_accounts)
        private var principal: TextView = view.findViewById(R.id.text_principal_card_accounts)
        private var evaluation: TextView = view.findViewById(R.id.text_evaluation_card_accounts)
        private var rate: TextView = view.findViewById(R.id.text_rate_card_accounts)

        fun bind(dairyTotal: DairyTotal, position: Int, clickListener: (Int) -> Unit) {
            val df = DecimalFormat("#,###")
            date.text = dairyTotal.date
            principal.text = df.format(dairyTotal.principalKRW)
            evaluation.text = df.format(dairyTotal.evaluationKRW)
            rate.text = String.format(Locale.getDefault(),"%.2f",dairyTotal.rate)
            view.setOnClickListener { clickListener(position) }
        }
    }
}