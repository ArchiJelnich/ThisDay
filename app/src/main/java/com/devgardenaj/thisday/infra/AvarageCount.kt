package com.devgardenaj.thisday.infra

data class MonthAverage(
    val month: Int,
    val avgByCategory: Map<Int, Int>
)

data class DayInfo(
    val day: Int,
    val sumsByCategory: Map<Int, Int>
)
