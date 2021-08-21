package com.watering.moneyrecord.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.DialogFragment
import com.watering.moneyrecord.MainActivity
import com.watering.moneyrecord.R

class DialogInOut: DialogFragment() {

    interface Complete {
        fun onComplete(select:Int)
    }
    private lateinit var mActivity: MainActivity
    private lateinit var complete:Complete

    fun newInstance(complete:Complete): DialogInOut {
        this.complete = complete
        return this
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mActivity = activity as MainActivity
        val inflater = mActivity.layoutInflater
        val view = inflater.inflate(R.layout.dialog_inout, null)
        val builder = AlertDialog.Builder(mActivity)

        val list = listOf("원화", "외화", "계좌이체")
        val listView: ListView = view.findViewById(R.id.listView_dlg_Inout)

        listView.choiceMode = ListView.CHOICE_MODE_SINGLE
        listView.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, list)
        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            complete.onComplete(position)
            dismiss()
        }

        builder.setView(view).setTitle(getString(R.string.title_select))

        return builder.create()
    }
}