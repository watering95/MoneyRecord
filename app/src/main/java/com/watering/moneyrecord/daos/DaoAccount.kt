package com.watering.moneyrecord.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.watering.moneyrecord.entities.Account

@Dao
interface DaoAccount {
    @Query("SELECT * from tbl_Account")
    fun getAll(): LiveData<List<Account>>

    @Query("SELECT * FROM tbl_Account WHERE _id = :id LIMIT 1")
    fun get(id: Int?): LiveData<Account>

    @Query("SELECT * FROM tbl_Account WHERE id_group = :id")
    fun getByGroup(id: Int?): LiveData<List<Account>>

    @Query("SELECT * FROM tbl_Account WHERE _id = (SELECT id_account FROM tbl_spend_cash WHERE spend_code = :code)")
    fun getByCode(code: String?): LiveData<Account>

    @Query("SELECT * FROM tbl_Account WHERE number = :number")
    fun get(number: String?): LiveData<Account>

    @Insert
    fun insert(account: Account)

    @Update
    fun update(account: Account)

    @Delete
    fun delete(account: Account)
}