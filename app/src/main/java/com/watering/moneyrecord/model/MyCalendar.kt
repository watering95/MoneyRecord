package com.watering.moneyrecord.model

import java.util.*

object MyCalendar {
    fun getToday(): String {
        return calendarToStr(Calendar.getInstance())
    }
    fun changeDate(date:String?, amount:Int):Calendar {
        val calendar = strToCalendar(date)
        calendar.add(Calendar.DATE, amount)
        return calendar
    }
    fun calendarToStr(calendar:Calendar): String {
        return String.format(Locale.getDefault(),"%04d-%02d-%02d",calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1,calendar.get(Calendar.DATE))
    }
    fun strToCalendar(date: String?): Calendar {
        val calendar = Calendar.getInstance()

        date?.run {
            calendar.set(Integer.parseInt(substring(0,4)),Integer.parseInt(substring(5,7))-1,Integer.parseInt(substring(8,10)))
        }
        return calendar
    }
}