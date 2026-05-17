package com.devgardenaj.thisday.infra

import android.app.Application
import com.devgardenaj.thisday.repo.CategoryRepository
import com.devgardenaj.thisday.repo.InfoRepository
import com.devgardenaj.thisday.room.AppDatabase

class ThisDayApp : Application() {

    private val db by lazy { AppDatabase.getInstance(this) }

    val categoryRepo by lazy { CategoryRepository(db.CategoryDao()) }
    val infoRepo by lazy { InfoRepository(db.InfoAboutDayDao()) }
}
