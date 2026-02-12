package com.gencoder.vibecoding.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ChatMessageEntity::class], version = 1, exportSchema = false)
abstract class GencoderDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao

    companion object {
        @Volatile
        private var instance: GencoderDatabase? = null

        fun getInstance(context: Context): GencoderDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    GencoderDatabase::class.java,
                    "gencoder.db"
                ).build().also { instance = it }
            }
        }
    }
}
