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
import java.time.YearMonth
import kotlin.math.roundToInt

class GraphViewModel(
    private val catRepo: CategoryRepository,
    private val infoRepository: InfoRepository,
    val newYear: Int,
    val newMY: LocalDate
) : CategoryViewModel(catRepo) {

    val categoryColors: Map<Int, Color>
        get() = categories.value.associate {
            it.categoryID to parseColor(it.categoryColor)
        }

    var info = mutableStateOf<List<InfoAboutDay>>(emptyList())
    var infoPerM = mutableStateOf<List<InfoAboutDay>>(emptyList())
    var monthAverages = mutableStateOf<List<MonthAverage>>(emptyList())
    var monthInfo = mutableStateOf<List<DayInfo>>(emptyList())

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadInfo(ID: Int) {
        viewModelScope.launch {
            info.value = if (ID == -1)
                infoRepository.getInfoByYear(newYear)
            else
                infoRepository.getInfoByYearByID(newYear, ID)
            computeMonthAverages()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadPerMInfo(ID: Int) {
        viewModelScope.launch {
            infoPerM.value = if (ID == -1)
                infoRepository.getInfoByYearMonth(newMY.monthValue, newMY.year)
            else
                infoRepository.getInfoByYearMonthByID(newMY.monthValue, newMY.year, ID)
            computeMonthInfo()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun computeMonthAverages() {
        val currentInfo = info.value

        val groupedByMonth = (1..12).map { month ->
            month to currentInfo.filter { it.infoMonth == month }
        }

        val monthSummaries = groupedByMonth.map { (month, days) ->
            month to days
                .groupBy { it.categoryID }
                .mapValues { (_, items) -> items.sumOf { it.infoSum } }
        }

        monthAverages.value = monthSummaries.map { (month, sumsByCategory) ->
            val daysInMonth = YearMonth.of(newYear, month).lengthOfMonth()
            MonthAverage(
                month = month,
                avgByCategory = sumsByCategory.mapValues { (_, sum) ->
                    (sum.toDouble() / daysInMonth).roundToInt()
                }
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun computeMonthInfo() {
        val year = newMY.year
        val month = newMY.monthValue
        val sourceList = infoPerM.value
        val daysInMonth = YearMonth.of(year, month).lengthOfMonth()

        val presentCategories = sourceList.map { it.categoryID }.distinct()
        val sumMap = sourceList
            .groupBy { it.infoDay to it.categoryID }
            .mapValues { (_, items) -> items.sumOf { it.infoSum } }

        monthInfo.value = (1..daysInMonth).map { day ->
            DayInfo(
                day = day,
                sumsByCategory = presentCategories
                    .associateWith { category -> sumMap[day to category] ?: 0 }
                    .filterValues { it != 0 }
            )
        }
    }
}
