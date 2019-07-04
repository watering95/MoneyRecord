package com.watering.moneyrecord.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.watering.moneyrecord.entities.Card

@Dao
interface DaoCard {
    @Query("SELECT * from tbl_card")
    fun getAll(): LiveData<List<Card>>

    @Query("SELECT * FROM tbl_card WHERE _id = :id LIMIT 1")
    fun get(id: Int?): LiveData<Card>

    @Query("SELECT * FROM tbl_card WHERE _id = (SELECT id_card FROM tbl_spend_card WHERE spend_code = :code)")
    fun getByCode(code: String?): LiveData<Card>

    @Query("SELECT * FROM tbl_card WHERE number = :number")
    fun getByNumber(number: String?): LiveData<Card>

    @Insert
    fun insert(card: Card)

    @Update
    fun update(card: Card)

    @Delete
    fun delete(card: Card)
}