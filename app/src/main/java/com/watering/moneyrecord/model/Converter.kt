package com.watering.moneyrecord.model

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.databinding.InverseMethod
import java.text.DecimalFormat

object Converter {
    private val df_int = DecimalFormat("#,###")
    private val df_double = DecimalFormat("#,##0.##")

    @InverseMethod("strToInt")
    @JvmStatic fun intToStr(value:Int): String {
        return try {
            df_int.format(value)
        } catch (e: Exception) {
            ""
        }
    }

    @JvmStatic fun strToInt(value:String): Int {
        return try {
            df_int.parse(value).toInt()
        } catch (e: Exception) {
            0
        }
    }

    @InverseMethod("strToDouble")
    @JvmStatic fun doubleToStr(value:Double): String {
        return try {
            df_double.format(value)
        } catch (e: Exception) {
            ""
        }
    }

    @JvmStatic fun strToDouble(value:String): Double {
        return try {
            df_double.parse(value).toDouble()
        } catch (e: Exception) {
            0.0
        }
    }

    fun addTextChangedListener(e: EditText) {
        e.run {
            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    setSelection(text.length)
                }
            })
        }
    }
}