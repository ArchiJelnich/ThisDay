package com.devgardenaj.thisday

import androidx.compose.ui.graphics.Color

fun ColorToHex(color : Color): String {


    val hex = String.format("#%02X%02X%02X",
        (color.red * 255).toInt(),
        (color.green * 255).toInt(),
        (color.blue * 255).toInt()
    )
    return(hex)

}