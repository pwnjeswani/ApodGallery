package com.pawanjeswani.apodgallery.util

import java.text.SimpleDateFormat
import java.util.*

object GeneralUtils {

    var dateFormatter = SimpleDateFormat("YYYY-MM-dd", Locale.ENGLISH)
        internal set

    var todayDate = System.currentTimeMillis()
    internal set

    fun getDaysAgo(daysBefore:Int): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -daysBefore)
        return calendar.time
    }

    fun getEndDate(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.DAY_OF_YEAR, Constans.PageSize -1)
        return calendar.time
    }
}
