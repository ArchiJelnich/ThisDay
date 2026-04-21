package com.devgardenaj.thisday.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import com.devgardenaj.thisday.repo.CategoryRepository
import com.devgardenaj.thisday.repo.InfoRepository
import com.devgardenaj.thisday.infra.DayInfo
import com.devgardenaj.thisday.infra.MonthAverage
import com.devgardenaj.thisday.infra.parseColor
import com.devgardenaj.thisday.room.InfoAboutDay
import kotlinx.coroutines.launch
import java.time.LocalDate

class GraphViewModel(private val catRepo: CategoryRepository, private val infoRepository: InfoRepository, val newYear : Int, val newMY : LocalDate) : CategoryViewModel(catRepo) {

    val categoryColors: Map<Int, Color>
        get() = categories.value.associate {
            it.categoryID to parseColor(it.categoryColor)
        }

    var info = mutableStateOf<List<InfoAboutDay>>(emptyList())
    var infoPerM = mutableStateOf<List<InfoAboutDay>>(emptyList())
    var monthAverages = mutableStateOf<List<MonthAverage>>(emptyList())
    var monthInfo = mutableStateOf<List<DayInfo>>(emptyList())

    fun loadInfo(ID : Int) {
        viewModelScope.launch {


            if (ID == -1)
                info.value = infoRepository.getInfoByYear(newYear)
            else
                info.value = infoRepository.getInfoByYearByID(newYear, ID)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadPerMInfo(ID : Int) {
        viewModelScope.launch {


            if (ID == -1)
                infoPerM.value = infoRepository.getInfoByYearMonth(newMY.monthValue, newMY.year)
            else
                infoPerM.value = infoRepository.getInfoByYearMonthByID(newMY.monthValue, newMY.year, ID)

        }
    }

}