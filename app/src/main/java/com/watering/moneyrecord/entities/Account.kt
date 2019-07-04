package com.watering.moneyrecord.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tbl_Account")
class Account {
    @ColumnInfo(name = "_id")
    @PrimaryKey(autoGenerate = true)
    var id:Int? = null
    @ColumnInfo(name = "id_group")
    var group:Int? = null
    @ColumnInfo(name = "institute")
    var institute: String? = null
    @ColumnInfo(name = "number")
    var number: String? = null
    @ColumnInfo(name = "description")
    var description: String? = null
}