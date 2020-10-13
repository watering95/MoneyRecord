package com.watering.moneyrecord.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.watering.moneyrecord.R
import com.watering.moneyrecord.entities.StatisticsMonthly
import java.text.DecimalFormat

class RecyclerViewAdapterBookStatistics(val lists:List<StatisticsMonthly>, private val clickListener: (Int) -> Unit): RecyclerView.Adapter<RecyclerViewAdapterBookStatistics.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardView = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_book_statistics, parent, false)

        return ViewHolder(cardView)
    }

    override fun getItemCount(): Int {
        return lists.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(lists[position], position, clickListener)
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private var yearMonth: TextView = view.findViewById(R.id.text_yearmonth_card_book_statistics)
        private var income: TextView = view.findViewById(R.id.text_income_card_book_statistics)
        private var spend: TextView = view.findViewById(R.id.text_spend_card_book_statistics)

        fun bind(statistics: StatisticsMonthly, position: Int, clickListener: (Int) -> Unit) {
            val df = DecimalFormat("#,###")

            yearMonth.text = statistics.date
            income.text = df.format(statistics.income)
            spend.text = df.format(statistics.spend)
            view.setOnClickListener { clickListener(position) }
        }
    }
}