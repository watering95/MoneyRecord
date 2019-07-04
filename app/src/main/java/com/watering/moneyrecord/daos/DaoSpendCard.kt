package com.watering.moneyrecord.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.watering.moneyrecord.entities.SpendCard

@Dao
interface DaoSpendCard {
    @Query("SELECT * from tbl_spend_card")
    fun getAll(): LiveData<List<SpendCard>>

    @Query("SELECT * from tbl_spend_card WHERE spend_code = :code LIMIT 1")
    fun get(code: String?): LiveData<SpendCard>

    @Insert
    fun insert(spendCard: SpendCard)

    @Update
    fun update(spendCard: SpendCard)

    @Delete
    fun delete(spendCard: SpendCard)
}