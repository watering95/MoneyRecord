package com.watering.moneyrecord.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tbl_home")
class Home {
    @ColumnInfo(name = "_id")
    @PrimaryKey(autoGenerate = true)
    var id:Int? = null
    @ColumnInfo(name = "id_account")
    var id_account: Int? = 0
    @ColumnInfo(name = "group")
    var group: String? = null
    @ColumnInfo(name = "account")
    var account: String? = null
    @ColumnInfo(name = "principalKRW")
    var principalKRW: Int? = 0
    @ColumnInfo(name = "evaluationKRW")
    var evaluationKRW: Int? = 0
    @ColumnInfo(name = "rate")
    var rate: Double? = 0.0
    @ColumnInfo(name = "description")
    var description: String? = null
}