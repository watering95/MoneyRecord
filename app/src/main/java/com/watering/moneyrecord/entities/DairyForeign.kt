package com.watering.moneyrecord.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tbl_Info_Dairy_Foreign")
class DairyForeign {
    @ColumnInfo(name = "_id")
    @PrimaryKey(autoGenerate = true)
    var id:Int? = null
    @ColumnInfo(name = "date")
    var date: String? = null
    @ColumnInfo(name = "id_account")
    var account:Int? = null
    @ColumnInfo(name = "principal")
    var principal: Double? = 0.0
    @ColumnInfo(name = "principal_krw")
    var principalKRW: Int? = 0
    @ColumnInfo(name = "rate")
    var rate:Double? = 0.0
    @ColumnInfo(name = "id_currency")
    var currency: Int? = 0
}