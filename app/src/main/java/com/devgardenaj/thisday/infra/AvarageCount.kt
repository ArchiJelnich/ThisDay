package com.devgardenaj.thisday.infra

import android.util.Log
import com.devgardenaj.thisday.Category
import com.devgardenaj.thisday.GrathViewModel
import com.devgardenaj.thisday.InfoAboutDay
import java.time.YearMonth
import java.util.Collections.list
import kotlin.math.roundToInt


data class MonthAverage(
    val month: Int,
    val avgByCategory: Map<Int, Int>
)

data class MonthInfo(
    val month: Int,
    val infoByCategory: Map<Int, Int>
)

data class DayInfo(
    val day: Int,
    val sumsByCategory: Map<Int, Int>
)

fun AvarageCount(viewModel : GrathViewModel, chosenCat : Category) {


    val year = viewModel.newYear

    Log.d("MyDebugs", "Stage 5")
    viewModel.loadCategories()
    viewModel.loadInfo(chosenCat.categoryID)


    data class MonthInfo(
        val month: Int,
        val days: List<InfoAboutDay>
    )

    data class MonthSummary(
        val month: Int,
        val sumsByCategory: Map<Int, Int>
    )



    val groupedByMonth: List<MonthInfo> =
        (1..12).map { month ->
            MonthInfo(
                month = month,
                days = viewModel.info.value.filter { it.infoMonth == month }
            )
        }

    val monthSummaries: List<MonthSummary> =
        groupedByMonth.map { monthInfo ->
            MonthSummary(
                month = monthInfo.month,
                sumsByCategory = monthInfo.days
                    .groupBy { it.categoryID }
                    .mapValues { (_, items) ->
                        items.sumOf { it.infoSum }
                    }
            )
        }

    val monthAverages: List<MonthAverage> =
        monthSummaries.map { monthSummary ->

            val daysInMonth =
                YearMonth.of(year, monthSummary.month)
                    .lengthOfMonth()

            MonthAverage(
                month = monthSummary.month,
                avgByCategory = monthSummary.sumsByCategory.mapValues { (_, sum) ->
                    maxOf(1, (sum.toDouble() / daysInMonth).roundToInt())
                }
            )
        }


    viewModel.monthAverages.value = monthAverages

}

fun MonthCount(viewModel : GrathViewModel, chosenCat : Category)
{


    Log.d("MyDebugs", "Stage 4")
    viewModel.loadPerMInfo(chosenCat.categoryID)

    val year = viewModel.newMY.year
    val month = viewModel.newMY.monthValue


    val sourceList = viewModel.infoPerM.value
    val daysInMonth = YearMonth.of(year, month).lengthOfMonth()

    val presentCategories = sourceList.map { it.categoryID }.distinct()

    val sumMap = sourceList
        .groupBy { it.infoDay to it.categoryID }
        .mapValues { (_, items) -> items.sumOf { it.infoSum } }

    val dayInfos = (1..daysInMonth).map { day ->
        val sumsByCategory = presentCategories
            .associateWith { category -> sumMap[day to category] ?: 0 }
            .filterValues { it != 0 }

        DayInfo(day = day, sumsByCategory = sumsByCategory)
    }

    viewModel.monthInfo.value = dayInfos


}