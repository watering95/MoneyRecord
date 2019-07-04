package com.watering.moneyrecord.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.watering.moneyrecord.entities.IOForeign

@Dao
interface DaoIOForeign {
    @Query("SELECT * from tbl_Info_IO_Foreign")
    fun getAll(): LiveData<List<IOForeign>>

    @Query("SELECT * FROM tbl_Info_IO_Foreign WHERE id_account = :id_account AND date = :date AND id_currency = :currency")
    fun get(id_account: Int?, date: String?, currency: Int?): LiveData<IOForeign>

    @Query("SELECT * FROM tbl_Info_IO_Foreign WHERE id_account = :id_account AND date <= :date GROUP BY id_currency")
    fun getLast(id_account: Int?, date: String?): LiveData<List<IOForeign>>

    @Query("SELECT * FROM tbl_Info_IO_Foreign WHERE id_account = :id_account AND date <= :date AND id_currency = :currency ORDER BY date DESC LIMIT 1")
    fun getLast(id_account:Int?, date: String?, currency: Int?): LiveData<IOForeign>

    @Query("SELECT SUM(input) FROM tbl_Info_IO_Foreign WHERE id_account = :id_account AND date <= :date AND id_currency = :currency")
    fun sumOfInput(id_account: Int?, date: String?, currency: Int?): LiveData<Double>

    @Query("SELECT SUM(output) FROM tbl_Info_IO_Foreign WHERE id_account = :id_account AND date <= :date AND id_currency = :currency")
    fun sumOfOutput(id_account: Int?, date: String?, currency: Int?): LiveData<Double>

    @Query("SELECT SUM(input_krw) FROM tbl_Info_IO_Foreign WHERE id_account = :id_account AND date <= :date AND id_currency = :currency")
    fun sumOfInputKRW(id_account: Int?, date: String?, currency: Int?): LiveData<Int>

    @Query("SELECT SUM(output_krw) FROM tbl_Info_IO_Foreign WHERE id_account = :id_account AND date <= :date AND id_currency = :currency")
    fun sumOfOutputKRW(id_account: Int?, date: String?, currency: Int?): LiveData<Int>

    @Insert
    fun insert(dairy: IOForeign)

    @Update
    fun update(dairy: IOForeign)

    @Delete
    fun delete(dairy: IOForeign)
}