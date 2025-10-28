package com.devgardenaj.thisday

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.devgardenaj.thisday.room.CategoryDao
import com.devgardenaj.thisday.room.InfoAboutDayDao

@Database(entities = [Category::class, InfoAboutDay::class],version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun CategoryDao(): CategoryDao
    abstract fun InfoAboutDayDao(): InfoAboutDayDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null


        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "thisday_app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }


    }


}
