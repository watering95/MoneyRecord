package com.watering.moneyrecord.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tbl_Info_IO_Foreign")
class IOForeign {
    @ColumnInfo(name = "_id")
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
    @ColumnInfo(name = "date")
    var date: String? = null
    @ColumnInfo(name = "id_account")
    var account: Int? = null
    @ColumnInfo(name = "input")
    var input: Double? = 0.0
    @ColumnInfo(name = "output")
    var output: Double? = 0.0
    @ColumnInfo(name = "input_krw")
    var inputKRW: Int? = 0
    @ColumnInfo(name = "output_krw")
    var outputKRW: Int? = 0
    @ColumnInfo(name = "id_currency")
    var currency: Int? = 0
    @ColumnInfo(name = "evaluation")
    var evaluationKRW: Double? = 0.0
}