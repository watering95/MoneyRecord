package com.watering.moneyrecord.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tbl_card")
class Card {
    @ColumnInfo(name = "_id")
    @PrimaryKey(autoGenerate = true)
    var id:Int? = null
    @ColumnInfo(name = "number")
    var number: String? = null
    @ColumnInfo(name = "company")
    var company: String? = null
    @ColumnInfo(name = "name")
    var name: String? = null
    @ColumnInfo(name = "date_draw")
    var drawDate: String? = null
    @ColumnInfo(name = "id_account")
    var account: Int? = null
}