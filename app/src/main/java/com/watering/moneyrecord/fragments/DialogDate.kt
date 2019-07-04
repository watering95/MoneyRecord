package com.watering.moneyrecord.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.watering.moneyrecord.MainActivity
import com.watering.moneyrecord.R
import java.util.*

class DialogDate: DialogFragment() {

    interface Complete {
        fun onComplete(date:String?)
    }
    private lateinit var mActivity: MainActivity
    var date:String? = null
    private lateinit var complete:Complete

    fun newInstance(date: String?, complete:Complete): DialogDate {
        this.date = date
        this.complete = complete
        return this
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mActivity = activity as MainActivity
        val inflater = mActivity.layoutInflater
        val view = inflater.inflate(R.layout.dialog_date, null)
        val datePicker = view.findViewById<DatePicker>(R.id.datepicker_dialog_date)
        val builder = AlertDialog.Builder(mActivity)

        datePicker.init(datePicker.year, datePicker.month, datePicker.dayOfMonth,
            DatePicker.OnDateChangedListener { _, year, monthOfYear, dayOfMonth ->
                val select = Calendar.getInstance().set(year, monthOfYear, dayOfMonth)

                when {
                    Calendar.getInstance().before(select) -> {
                        Toast.makeText(mActivity.applicationContext, R.string.toast_date_error, Toast.LENGTH_SHORT).show()
                        return@OnDateChangedListener
                    }
                    else -> date = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, monthOfYear + 1, dayOfMonth)
                }
            }
        )

        builder.setView(view).setTitle(getString(R.string.title_select_date))
        builder.setPositiveButton(getString(R.string.select)) { _, _ -> complete.onComplete(date) }
        builder.setNegativeButton(getString(R.string.cancel)) { _, _ -> }
        return builder.create()
    }
}