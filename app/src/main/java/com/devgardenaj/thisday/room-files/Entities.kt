package com.devgardenaj.thisday

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Category")
data class Category(
    @PrimaryKey(autoGenerate = true) var categoryID: Int,
    @ColumnInfo(name = "categoryName") val categoryName: String,
    @ColumnInfo(name = "categoryColor") val categoryColor: String,
) : Serializable

@Entity(tableName = "InfoAboutDay")
data class InfoAboutDay(
    @PrimaryKey(autoGenerate = true) var iID: Int,
    @ColumnInfo(name = "categoryID") val categoryID: Int,
    @ColumnInfo(name = "infoDay") val infoDay: Int,
    @ColumnInfo(name = "infoMonth") val infoMonth: Int,
    @ColumnInfo(name = "infoYear") val infoYear: Int,
    @ColumnInfo(name = "infoSum") val infoSum: Int,
    @ColumnInfo(name = "infoDeletedFlag") val infoDeletedFlag: Int,
)

