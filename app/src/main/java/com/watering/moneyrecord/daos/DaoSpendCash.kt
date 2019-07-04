package com.watering.moneyrecord.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.watering.moneyrecord.entities.SpendCash

@Dao
interface DaoSpendCash {
    @Query("SELECT * from tbl_spend_cash")
    fun getAll(): LiveData<List<SpendCash>>

    @Query("SELECT * from tbl_spend_cash WHERE spend_code = :code LIMIT 1")
    fun get(code: String?): LiveData<SpendCash>

    @Insert
    fun insert(spendCash: SpendCash)

    @Update
    fun update(spendCash: SpendCash)

    @Delete
    fun delete(spendCash: SpendCash)
}