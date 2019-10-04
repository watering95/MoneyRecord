package com.watering.moneyrecord.model

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.databinding.InverseMethod
import java.text.NumberFormat

object Converter {
    private val nf_int = NumberFormat.getInstance()
    private val nf_double = NumberFormat.getInstance()

    @InverseMethod("strToInt")
    @JvmStatic fun intToStr(value:Int): String {
        return try {
            nf_int.format(value)
        } catch (e: Exception) {
            ""
        }
    }

    @JvmStatic fun strToInt(value:String): Int {
        return try {
            nf_int.parse(value).toInt()
        } catch (e: Exception) {
            0
        }
    }

    @InverseMethod("strToDouble")
    @JvmStatic fun doubleToStr(value:Double): String {
        return try {
            nf_double.format(value)
        } catch (e: Exception) {
            ""
        }
    }

    @JvmStatic fun strToDouble(value:String): Double {
        return try {
            nf_double.parse(value).toDouble()
        } catch (e: Exception) {
            0.0
        }
    }

    fun addConvertedTextChangedListener(e: EditText) {
        e.run {
            addTextChangedListener(object : TextWatcher {

                var length = 0
                var position = 0

                override fun afterTextChanged(s: Editable?) {
                    if(position == 0) setSelection(0) else setSelection(position+(text.length-length))
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    s?.let { length = it.length }
                    position = selectionStart
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })
        }
    }
}