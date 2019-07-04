package com.watering.moneyrecord.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.watering.moneyrecord.entities.DairyForeign

@Dao
interface DaoDairyForeign {
    @Query("SELECT * from tbl_Info_Dairy_Foreign")
    fun getAll(): LiveData<List<DairyForeign>>

    @Query("SELECT * FROM tbl_Info_Dairy_Foreign WHERE id_account = :id_account AND date = :date AND id_currency = :currency")
    fun get(id_account: Int?, date: String?, currency: Int?): LiveData<DairyForeign>

    @Query("SELECT * FROM tbl_Info_Dairy_Foreign WHERE id_account = :id_account AND date <= :date GROUP BY id_currency")
    fun getLast(id_account: Int?, date: String?): LiveData<List<DairyForeign>>

    @Query("SELECT date FROM tbl_Info_Dairy_Foreign WHERE id_account = :id_account AND date > :date AND id_currency = :currency")
    fun getAfter(id_account: Int?, date: String?, currency: Int?): LiveData<List<String>>

    @Insert
    fun insert(dairy: DairyForeign)

    @Update
    fun update(dairy: DairyForeign)

    @Delete
    fun delete(dairy: DairyForeign)
}