package com.watering.moneyrecord.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.watering.moneyrecord.R

class RecyclerViewAdapterFileManagementDB(val lists:List<String>): RecyclerView.Adapter<RecyclerViewAdapterFileManagementDB.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardView = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_file_management_db, parent, false)

        return ViewHolder(cardView)

    }

    override fun getItemCount(): Int {
        return lists.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(lists[position], position)
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private var filename: TextView = view.findViewById(R.id.text_filename_management_db)

        fun bind(list: String, position: Int) {
            filename.text = list
        }
    }
}