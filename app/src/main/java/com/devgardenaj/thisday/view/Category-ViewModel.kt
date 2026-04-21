package com.devgardenaj.thisday.view

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devgardenaj.thisday.repo.CategoryRepository
import com.devgardenaj.thisday.infra.inputChecker
import com.devgardenaj.thisday.room.Category
import kotlinx.coroutines.launch




open class CategoryViewModel(private val repository: CategoryRepository) : ViewModel() {

    var categories = mutableStateOf<List<Category>>(emptyList())
        private set

    fun loadCategories() {
        viewModelScope.launch {
            categories.value = repository.getAllNotDeleted()

        }
    }

    fun insertCategory(name: String, color: String) {
        viewModelScope.launch {
            repository.insert(Category(0, inputChecker(name), color, 0))
            loadCategories()
        }
    }

    fun deleteCategory(id : Int)
    {
        viewModelScope.launch {
            repository.deleteCategoryByID(id)
            repository.deleteInfoByID(id)
            loadCategories()
        }


    }

    fun getCategoryById(id: Int): Category? {
        return categories.value.find { it.categoryID == id }
    }

    fun updateCategory(id: Int, name: String, color: String) {
        viewModelScope.launch {
            val updated = Category(id, inputChecker(name), color, 0)
            repository.update(updated)
            loadCategories()
        }
    }
}