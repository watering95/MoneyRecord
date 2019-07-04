package com.watering.moneyrecord.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tbl_spend_cash")
class SpendCash {
    @ColumnInfo(name = "_id")
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
    @ColumnInfo(name = "spend_code")
    var code: String? = null
    @ColumnInfo(name = "id_account")
    var account: Int? = null
}