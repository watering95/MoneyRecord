package com.watering.moneyrecord.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tbl_income")
class Income {
    @ColumnInfo(name = "_id")
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
    @ColumnInfo(name = "date")
    var date: String? = null
    @ColumnInfo(name = "id_sub")
    var category: Int? = null
    @ColumnInfo(name = "amount")
    var amount: Int? = 0
    @ColumnInfo(name = "details")
    var details: String? = null
    @ColumnInfo(name = "id_account")
    var account: Int? = null
}