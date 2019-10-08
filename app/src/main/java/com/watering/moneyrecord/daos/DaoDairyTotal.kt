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
    fun getOrderByDate(id_account: Int?): LiveData<List<DairyTotal>>

    @Query("SELECT date FROM tbl_Info_Dairy_Total WHERE id_account = :id_account AND date > :date")
    fun getAfter(id_account: Int?, date: String?): LiveData<List<String>>

    @Query("SELECT date FROM tbl_Info_Dairy_Total WHERE id_account = :id_account AND date > :date ORDER BY date ASC LIMIT 1")
    fun getNext(id_account: Int?, date: String?): LiveData<String>

    @Query("SELECT date FROM tbl_info_dairy_total ORDER BY date ASC LIMIT 1")
    fun getFirstDate(): LiveData<String>

    @Query("SELECT SUM(evaluation) FROM (SELECT * FROM (SELECT * FROM tbl_Info_Dairy_Total WHERE id_account IN (SELECT _id FROM tbl_account WHERE id_group = :group) AND date <= :date ORDER BY date ASC) GROUP BY id_account)")
    fun getSumOfEvaluation(group: Int?, date: String?): LiveData<Int>

    @Query("SELECT SUM(evaluation) FROM (SELECT * FROM (SELECT * FROM tbl_Info_Dairy_Total WHERE date <= :date ORDER BY date ASC) GROUP BY id_account)")
    fun getSumOfEvaluation(date: String?): LiveData<Int>

    @Query("SELECT SUM(principal) FROM (SELECT * FROM (SELECT * FROM tbl_Info_Dairy_Total WHERE id_account IN (SELECT _id FROM tbl_account WHERE id_group = :group) AND date <= :date ORDER BY date ASC) GROUP BY id_account)")
    fun getSumOfPrincipal(group: Int?, date: String?): LiveData<Int>

    @Query("SELECT SUM(principal) FROM (SELECT * FROM (SELECT * FROM tbl_Info_Dairy_Total WHERE date <= :date ORDER BY date ASC) GROUP BY id_account)")
    fun getSumOfPrincipal(date: String?): LiveData<Int>

    @Insert
    fun insert(dairy: DairyTotal)

    @Update
    fun update(dairy: DairyTotal)

    @Delete
    fun delete(dairy: DairyTotal)
}