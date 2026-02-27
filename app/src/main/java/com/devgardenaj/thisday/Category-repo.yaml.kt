package com.devgardenaj.thisday

import com.devgardenaj.thisday.room.Category
import com.devgardenaj.thisday.room.CategoryDao
import com.devgardenaj.thisday.room.InfoAboutDayDao

class CategoryRepository(private val dao: CategoryDao, private val infoDao: InfoAboutDayDao) {
    suspend fun insert(category: Category) = dao.insertAll(category)
    suspend fun getAllNotDeleted() = dao.getAllNotDeleted()
    suspend fun update(category: Category) = dao.update(category)
    suspend fun deleteCategoryByID(id : Int) = dao.deleteCategoryByID(id)
    suspend fun deleteInfoByID(id : Int) = infoDao.deleteInfoByID(id)
}