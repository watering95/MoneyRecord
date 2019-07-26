package com.watering.moneyrecord.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.watering.moneyrecord.entities.Home

@Dao
interface DaoHome {
    @Query("SELECT * FROM tbl_home")
    fun getAll(): LiveData<List<Home>>

    @Query("SELECT * FROM tbl_home WHERE id_account = :id_account")
    fun getByIdAccount(id_account: Int?): LiveData<Home>

    @Query("SELECT * FROM tbl_home WHERE `group` = :group")
    fun getByGroup(group: String?): LiveData<List<Home>>

    @Insert
    fun insert(home: Home)

    @Update
    fun update(home: Home)

    @Delete
    fun delete(home: Home)
}