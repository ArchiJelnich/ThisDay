package com.devgardenaj.thisday.repo

import com.devgardenaj.thisday.room.Category
import com.devgardenaj.thisday.room.CategoryDao

class CategoryRepository(private val dao: CategoryDao) {
    suspend fun insert(category: Category) = dao.insertAll(category)
    suspend fun getAllNotDeleted() = dao.getAllNotDeleted()
    suspend fun update(category: Category) = dao.update(category)
    suspend fun deleteCategoryWithAllData(id: Int) = dao.deleteCategoryWithAllData(id)
}