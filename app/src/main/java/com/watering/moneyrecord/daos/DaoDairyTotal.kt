package com.watering.moneyrecord.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.watering.moneyrecord.entities.DairyTotal

@Dao
interface DaoDairyTotal {
    @Query("SELECT * from tbl_Info_Dairy_Total")
    fun getAll(): LiveData<List<DairyTotal>>

    @Query("SELECT * FROM tbl_Info_Dairy_Total WHERE id_account = :id_account AND date = :date")
    fun get(id_account:Int?, date:String?): LiveData<DairyTotal>

    @Query("SELECT * FROM tbl_Info_Dairy_Total WHERE id_account = :id_account ORDER BY date DESC")
    fun getLogs(id_account: Int?): LiveData<List<DairyTotal>>

    @Query("SELECT date FROM tbl_Info_Dairy_Total WHERE id_account = :id_account AND date > :date")
    fun getAfter(id_account: Int?, date: String?): LiveData<List<String>>

    @Query("SELECT (A.evaluation + B.evaluation) AS evaluation, (C.principal + D.principal) AS principal, ((A.evaluation + B.evaluation) / (C.principal + D.principal) * 100) AS rate, :date AS date, :id_account AS id_account FROM " +
            "(SELECT evaluation FROM tbl_Info_IO WHERE id_account = :id_account AND date <= :date ORDER BY date DESC LIMIT 1) AS A," +
            "(SELECT SUM(evaluation) AS evaluation FROM tbl_Info_IO_Foreign WHERE id_account = :id_account AND date <= :date GROUP BY id_currency) AS B," +
            "(SELECT principal FROM tbl_Info_Dairy WHERE id_account = :id_account AND date <= :date ORDER BY date DESC LIMIT 1) AS C," +
            "(SELECT SUM(principal_krw) AS principal FROM tbl_Info_Dairy_Foreign WHERE id_account = :id_account AND date <= :date GROUP BY id_currency) AS D")
    fun getLast(id_account:Int?, date:String?): LiveData<DairyTotal>


    @Insert
    fun insert(dairy: DairyTotal)

    @Update
    fun update(dairy: DairyTotal)

    @Delete
    fun delete(dairy: DairyTotal)
}