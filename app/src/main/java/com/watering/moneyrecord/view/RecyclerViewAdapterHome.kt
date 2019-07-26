package com.watering.moneyrecord.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.watering.moneyrecord.R
import com.watering.moneyrecord.entities.Home
import java.text.DecimalFormat
import java.util.*

class RecyclerViewAdapterHome(val lists:List<Home>, val total:Double, private val clickListener: (Int) -> Unit): RecyclerView.Adapter<RecyclerViewAdapterHome.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardView = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_home, parent, false)

        return ViewHolder(cardView)
    }

    override fun getItemCount(): Int {
        return lists.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(lists[position], total, position, clickListener)
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private var accountNumber: TextView = view.findViewById(R.id.text_account_num_card_home)
        private var accountDescription: TextView = view.findViewById(R.id.text_description_card_home)
        private var principal: TextView = view.findViewById(R.id.text_principal_card_home)
        private var evaluation: TextView = view.findViewById(R.id.text_evaluation_card_home)
        private var rate: TextView = view.findViewById(R.id.text_rate_card_home)
        private var ratioAccount: TextView = view.findViewById(R.id.text_rate_account_card_home)

        fun bind(home: Home, total:Double, position: Int, clickListener: (Int) -> Unit) {
            val df = DecimalFormat("#,###")
            val ratio = if(home.evaluationKRW == 0) 0.0 else home.evaluationKRW!!.toDouble()/total*100

            accountNumber.text = home.account
            accountDescription.text = home.description
            principal.text = df.format(home.principalKRW)
            evaluation.text = df.format(home.evaluationKRW)
            rate.text = String.format(Locale.getDefault(), "%.2f", home.rate)
            ratioAccount.text = String.format(Locale.getDefault(), "%.2f", ratio).plus("%")
            view.setOnClickListener { clickListener(position) }
        }
    }
}