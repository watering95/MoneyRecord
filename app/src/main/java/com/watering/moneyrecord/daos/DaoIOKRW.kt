package com.watering.moneyrecord.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.watering.moneyrecord.entities.IOKRW
import com.watering.moneyrecord.entities.StatisticsMonthly

@Dao
interface DaoIOKRW {
    @Query("SELECT * FROM tbl_Info_IO")
    fun getAll(): LiveData<List<IOKRW>>

    @Query("SELECT * FROM tbl_Info_IO WHERE id_account = :id_account AND date = :date")
    fun get(id_account: Int?, date: String?): LiveData<IOKRW>

    @Query("SELECT * FROM tbl_Info_IO WHERE id_account = :id_account AND date <= :date ORDER BY date DESC LIMIT 1")
    fun getLast(id_account:Int?, date: String?): LiveData<IOKRW>

    @Query("SELECT date FROM tbl_Info_IO WHERE id_account = :id_account AND date > :date")
    fun getAfter(id_account:Int?, date: String?): LiveData<List<String>>

    @Query("SELECT date FROM tbl_Info_IO WHERE id_account = :id_account AND date > :date ORDER BY date ASC LIMIT 1")
    fun getNext(id_account:Int?, date: String?): LiveData<String>

    @Query("SELECT SUM(input) FROM tbl_Info_IO WHERE id_account = :id_account AND date <= :date")
    fun sumOfInput(id_account: Int?, date: String?): LiveData<Int>

    @Query("SELECT SUM(output) FROM tbl_Info_IO WHERE id_account = :id_account AND date <= :date")
    fun sumOfOutput(id_account: Int?, date: String?): LiveData<Int>

    @Query("SELECT SUM(income) FROM tbl_Info_IO WHERE id_account = :id_account AND date <= :date")
    fun sumOfIncome(id_account: Int?, date: String?): LiveData<Int>

    @Query("SELECT SUM(income) FROM tbl_Info_IO WHERE date >= :startDate AND date <= :endDate")
    fun sumOfIncome(startDate: String?, endDate: String?): LiveData<Int>

    @Query("SELECT SUM(spend_cash) + SUM(spend_card) FROM tbl_Info_IO WHERE date >= :startDate AND date <= :endDate")
    fun sumOfSpend(startDate: String?, endDate: String?): LiveData<Int>

    @Query("SELECT SUM(spend_card) FROM tbl_Info_IO WHERE id_account = :id_account AND date <= :date")
    fun sumOfSpendCard(id_account: Int?, date: String?): LiveData<Int>

    @Query("SELECT SUM(spend_cash) FROM tbl_Info_IO WHERE id_account = :id_account AND date <= :date")
    fun sumOfSpendCash(id_account: Int?, date: String?): LiveData<Int>

    @Query("SELECT substr(date, 1, 7) AS date, sum(income) AS income, sum(spend_cash) + sum(spend_card) AS spend FROM tbl_Info_IO GROUP BY substr(date, 1, 7)")
    fun sumOfMonthly(): LiveData<List<StatisticsMonthly>>

    @Insert
    fun insert(io: IOKRW)

    @Update
    fun update(io: IOKRW)

    @Delete
    fun delete(io: IOKRW)
}