package com.watering.moneyrecord.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.watering.moneyrecord.entities.IOKRW

@Dao
interface DaoIOKRW {
    @Query("SELECT * FROM tbl_Info_IO")
    fun getAll(): LiveData<List<IOKRW>>

    @Query("SELECT * FROM tbl_Info_IO WHERE id_account = :id_account AND date = :date")
    fun get(id_account: Int?, date: String?): LiveData<IOKRW>

    @Query("SELECT * FROM tbl_Info_IO WHERE id_account = :id_account AND date <= :date ORDER BY date DESC LIMIT 1")
    fun getLast(id_account:Int?, date: String?): LiveData<IOKRW>

    @Query("SELECT SUM(input) FROM tbl_Info_IO WHERE id_account = :id_account AND date <= :date")
    fun sumOfInput(id_account: Int?, date: String?): LiveData<Int>

    @Query("SELECT SUM(output) FROM tbl_Info_IO WHERE id_account = :id_account AND date <= :date")
    fun sumOfOutput(id_account: Int?, date: String?): LiveData<Int>

    @Query("SELECT SUM(income) FROM tbl_Info_IO WHERE id_account = :id_account AND date <= :date")
    fun sumOfIncome(id_account: Int?, date: String?): LiveData<Int>

    @Query("SELECT SUM(spend_card) FROM tbl_Info_IO WHERE id_account = :id_account AND date <= :date")
    fun sumOfSpendCard(id_account: Int?, date: String?): LiveData<Int>

    @Query("SELECT SUM(spend_cash) FROM tbl_Info_IO WHERE id_account = :id_account AND date <= :date")
    fun sumOfSpendCash(id_account: Int?, date: String?): LiveData<Int>

    @Insert
    fun insert(io: IOKRW)

    @Update
    fun update(io: IOKRW)

    @Delete
    fun delete(io: IOKRW)
}