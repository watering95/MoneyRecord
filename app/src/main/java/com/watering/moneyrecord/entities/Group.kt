package com.watering.moneyrecord.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tbl_Group")
class Group {
    @ColumnInfo(name = "_id")
    @PrimaryKey(autoGenerate = true)
    var id:Int? = null
    @ColumnInfo(name = "name")
    var name: String? = null
}