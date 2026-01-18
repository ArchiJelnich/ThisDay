package com.devgardenaj.thisday.infra

import android.util.Log
import com.devgardenaj.thisday.GrathViewModel
import com.devgardenaj.thisday.InfoAboutDay
import kotlin.math.roundToInt


fun AvarageCount(viewModel : GrathViewModel) {


    val year = viewModel.myDate.year

    viewModel.loadCategories()
    viewModel.loadInfo()





    data class MonthInfo(
        val month: Int,
        val days: List<InfoAboutDay>
    )

    data class MonthSummary(
        val month: Int,
        val sumsByCategory: Map<Int, Int>
    )

    data class MonthAverage(
        val month: Int,
        val avgByCategory: Map<Int, Int>
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
                java.time.YearMonth.of(year, monthSummary.month)
                    .lengthOfMonth()

            MonthAverage(
                month = monthSummary.month,
                avgByCategory = monthSummary.sumsByCategory.mapValues { (_, sum) ->
                    maxOf(1, (sum.toDouble() / daysInMonth).roundToInt())
                }
            )
        }

    Log.d("AvarageCountDebug", "AvarageCount monthAverages = " + monthAverages)

}