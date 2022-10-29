package com.hong.assignment.common

import android.content.Context
import androidx.room.Room
import com.hong.assignment.data.local.AppDatabase
import com.hong.assignment.data.local.dao.CentersDao
import java.util.concurrent.Executors

class LocalUtil private constructor() {

    companion object {
        @Volatile
        private var instance: LocalUtil? = null

        @JvmStatic
        fun getInstance(): LocalUtil =
            instance ?: synchronized(this) {
                instance ?: LocalUtil().also {
                    instance = it
                }
            }
    }

    private var dao: CentersDao? = null

    fun init(context: Context) {
        val dataBase = Room.databaseBuilder(context, AppDatabase::class.java, "room")
            .setQueryExecutor(Executors.newCachedThreadPool())
            .setTransactionExecutor(Executors.newCachedThreadPool())
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()

        dao = dataBase.centersDao()
    }

    fun getDao(): CentersDao {
        return dao!!
    }
}