package com.watering.moneyrecord.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.watering.moneyrecord.entities.Income

@Dao
interface DaoIncome {
    @Query("SELECT * FROM tbl_income")
    fun getAll(): LiveData<List<Income>>

    @Query("SELECT * FROM tbl_income WHERE date = :date")
    fun get(date:String?): LiveData<List<Income>>

    @Query("SELECT total(amount) FROM tbl_income WHERE id_account = :id_account AND date = :date")
    fun sum(id_account:Int?, date: String?): LiveData<Int>

    @Insert
    fun insert(income: Income)

    @Update
    fun update(income: Income)

    @Delete
    fun delete(income: Income)
}