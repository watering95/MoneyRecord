package com.watering.moneyrecord.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.watering.moneyrecord.daos.*
import com.watering.moneyrecord.entities.*
import kotlinx.coroutines.CoroutineScope

@Database(entities = [Account::class, Card::class, CategoryMain::class, CategorySub::class, DairyForeign::class, DairyKRW::class, DairyTotal::class, Group::class, Income::class, IOForeign::class, IOKRW::class, Spend::class, SpendCard::class, SpendCash::class], version = 8)
abstract class AppDatabase: RoomDatabase() {
    abstract fun daoAccount(): DaoAccount
    abstract fun daoCard(): DaoCard
    abstract fun daoCategoryMain(): DaoCategoryMain
    abstract fun daoCategorySub(): DaoCategorySub
    abstract fun daoDairyForeign(): DaoDairyForeign
    abstract fun daoDairyKRW(): DaoDairyKRW
    abstract fun daoDairyTotal(): DaoDairyTotal
    abstract fun daoGroup(): DaoGroup
    abstract fun daoIncome(): DaoIncome
    abstract fun daoIOForeign(): DaoIOForeign
    abstract fun daoIOKRW(): DaoIOKRW
    abstract fun daoSpend(): DaoSpend
    abstract fun daoSpendCard(): DaoSpendCard
    abstract fun daoSpendCash(): DaoSpendCash

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java,"AssetLog.db")
                                .addMigrations(MIGRATION_7_8)
                                .build()
                INSTANCE = instance
                instance
            }
        }

        private val MIGRATION_7_8 = object: Migration(7,8) {
            override fun migrate(database: SupportSQLiteDatabase) {

            }
        }
    }
}