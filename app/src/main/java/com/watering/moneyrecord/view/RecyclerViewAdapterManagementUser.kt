package com.watering.moneyrecord.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.watering.moneyrecord.R
import com.watering.moneyrecord.entities.User

class RecyclerViewAdapterManagementUser(val lists:List<User>, private val clickListener: (Int) -> Unit): RecyclerView.Adapter<RecyclerViewAdapterManagementUser.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardView = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_management_user, parent, false)

        return ViewHolder(cardView)

    }

    override fun getItemCount(): Int {
        return lists.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(lists[position], position, clickListener)
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView = view.findViewById(R.id.text_name_card_management_user)
        private var id: TextView = view.findViewById(R.id.text_id_card_management_user)

        fun bind(user: User, position: Int, clickListener: (Int) -> Unit) {
            name.text = user.name
            id.text = user.id
            view.setOnClickListener { clickListener(position) }
        }
    }
}