package com.devgardenaj.thisday.repo

import com.devgardenaj.thisday.room.InfoAboutDay
import com.devgardenaj.thisday.room.InfoAboutDayDao

class InfoRepository(private val dao: InfoAboutDayDao) {
    suspend fun getInfoByDay(infoDay: Int, infoMonth: Int, infoYear: Int) = dao.getInfoByDay(infoDay, infoMonth, infoYear)
    suspend fun getInfoByYear(infoYear: Int) = dao.getInfoByYear( infoYear)
    suspend fun getInfoByYearByID(infoYear: Int, ID : Int) = dao.getInfoByYearByID( infoYear, ID)
    suspend fun getInfoByYearMonth(infoM : Int, infoYear: Int) = dao.getInfoByYearM(infoM, infoYear)
    suspend fun getInfoByYearMonthByID(infoM : Int, infoYear: Int, ID : Int) = dao.getInfoByYearMByID(infoM, infoYear, ID)
    suspend fun updateInfo(infoSummary: InfoAboutDay) = dao.updateInfo(infoSummary)
    suspend fun insertInfo(infoSummary: InfoAboutDay) = dao.insertInfo(infoSummary)
    suspend fun getAll(infoDay: Int, infoMonth: Int, infoYear: Int) = dao.getAll(infoDay, infoMonth, infoYear)


}