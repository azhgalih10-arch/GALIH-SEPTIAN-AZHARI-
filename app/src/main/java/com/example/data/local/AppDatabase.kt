package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.AppNotification
import com.example.data.model.ClusterLedgerEntry
import com.example.data.model.DuesBill
import com.example.data.model.Resident

@Database(
    entities = [
        Resident::class,
        DuesBill::class,
        ClusterLedgerEntry::class,
        AppNotification::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun duesDao(): DuesDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cluster_gandana_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
