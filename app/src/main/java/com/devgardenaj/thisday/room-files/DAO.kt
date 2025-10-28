package com.devgardenaj.thisday.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.devgardenaj.thisday.Category

@Dao
interface CategoryDao {
    @Query("SELECT * FROM Category")
    fun getAll(): List<Category>
    @Insert
    fun insertAll(vararg category: Category)
    @Query("SELECT * FROM Category WHERE categoryID = :categoryID")
    suspend fun getCategoryByID(categoryID: Int): List<Category>
    @Query("DELETE FROM Category")
    fun deleteAll()
}

@Dao
interface InfoAboutDayDao {

}