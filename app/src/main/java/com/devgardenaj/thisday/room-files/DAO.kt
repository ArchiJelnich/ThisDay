package com.devgardenaj.thisday.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.devgardenaj.thisday.Category
import com.devgardenaj.thisday.InfoAboutDay

@Dao
interface CategoryDao {
    @Query("SELECT * FROM Category")
    suspend  fun getAll(): List<Category>
    @Query("SELECT * FROM Category WHERE categoryDeletedFlag = 0")
    suspend  fun getAllNotDeleted(): List<Category>
    @Insert
    suspend  fun insertAll(vararg category: Category)
    @Query("SELECT * FROM Category WHERE categoryID = :categoryID")
     suspend fun getCategoryByID(categoryID: Int): List<Category>
    @Query("DELETE FROM Category")
    fun deleteAll()
    @Update
    suspend fun update(category: Category)
}

@Dao
interface InfoAboutDayDao {
    @Query("SELECT iID, categoryID, infoSum FROM InfoAboutDay WHERE infoDay = :infoDay AND infoMonth = :infoMonth AND infoYear = :infoYear")
    suspend fun getInfoByDay(infoDay: Int, infoMonth: Int, infoYear: Int): List<InfoSummary>
    @Update
    suspend fun updateInfo(infoDay: InfoAboutDay)
    @Insert
    suspend  fun insertInfo(vararg infoDay: InfoAboutDay)
    @Query("SELECT * FROM InfoAboutDay WHERE infoDay = :infoDay AND infoMonth = :infoMonth AND infoYear = :infoYear")
    suspend fun getAll(infoDay: Int, infoMonth: Int, infoYear: Int): List<InfoAboutDay>
    @Query("DELETE FROM InfoAboutDay")
    fun deleteAll()
}

data class InfoSummary(
    val iID: Int,
    val categoryID : Int,
    val infoSum: Int
)