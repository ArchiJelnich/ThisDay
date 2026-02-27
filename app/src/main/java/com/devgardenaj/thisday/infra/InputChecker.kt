package com.devgardenaj.thisday.infra

fun inputChecker(input: String): String {



    var newString = input

    if (newString.isEmpty())
    {
        newString = ""
    }

    newString = newString.replace("*", "")

    if (newString.length>15)
    {
        newString = newString.take(15)
    }

    return newString
}