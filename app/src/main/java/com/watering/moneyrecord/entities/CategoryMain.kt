package com.watering.moneyrecord.entities

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "tbl_category_main")
class CategoryMain: Category() {
    @ColumnInfo(name = "kind")
    var kind: String? = null
}