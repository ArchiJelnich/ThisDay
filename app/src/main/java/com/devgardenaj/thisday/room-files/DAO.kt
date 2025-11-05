package com.devgardenaj.thisday.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.devgardenaj.thisday.Category

@Dao
interface CategoryDao {
    @Query("SELECT * FROM Category")
    suspend  fun getAll(): List<Category>
    @Insert
    suspend  fun insertAll(vararg category: Category)
    @Query("SELECT * FROM Category WHERE categoryID = :categoryID")
     suspend fun getCategoryByID(categoryID: Int): List<Category>
    @Query("DELETE FROM Category")
    suspend  fun deleteAll()
}

@Dao
interface InfoAboutDayDao {

}