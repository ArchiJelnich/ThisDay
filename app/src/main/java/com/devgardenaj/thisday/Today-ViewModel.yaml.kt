package com.devgardenaj.thisday

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.devgardenaj.thisday.infra.CustomDate
import com.devgardenaj.thisday.room.InfoAboutDay
import com.devgardenaj.thisday.room.InfoSummary
import kotlinx.coroutines.launch

class TodayViewModel(private val repository: CategoryRepository, private val infoRepository: InfoRepository, private val date : CustomDate) : CategoryViewModel(repository) {

    var info = mutableStateOf<List<InfoSummary>>(emptyList())

    fun loadInfo() {
        viewModelScope.launch {
            info.value = infoRepository.getInfoByDay(date.day, date.month, date.year)
        }
    }

    fun updateCount(categoryId: Int, newCount: Int) {
        viewModelScope.launch {
            val existing = infoRepository.getAll(date.day, date.month, date.year)
                .find { it.categoryID == categoryId }

            if (existing != null) {
                infoRepository.updateInfo(
                    existing.copy(infoSum = newCount)
                )
            } else {
                infoRepository.insertInfo(
                    InfoAboutDay(
                        iID = 0,
                        categoryID = categoryId,
                        infoSum = newCount,
                        infoDay = date.day,
                        infoMonth = date.month,
                        infoYear = date.year
                    )
                )
            }

            loadInfo()
        }
    }

}