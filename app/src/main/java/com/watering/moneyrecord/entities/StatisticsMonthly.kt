package com.watering.moneyrecord.entities

import androidx.room.ColumnInfo

class StatisticsMonthly {
    @ColumnInfo(name = "date")
    var date: String? = ""
    @ColumnInfo(name = "income")
    var income: Int? = 0
    @ColumnInfo(name = "spend")
    var spend: Int? = 0

}