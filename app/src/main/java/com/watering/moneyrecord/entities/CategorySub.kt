package com.watering.moneyrecord.entities

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "tbl_category_sub")
class CategorySub: Category() {
    @ColumnInfo(name = "id_main")
    var categoryMain:Int? = null
}