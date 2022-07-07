package space.frankuzi.cinemacollection.watchlater.datetime

import java.text.SimpleDateFormat
import java.util.*

data class DateTime(
    val dayOfMonth: Int,
    val month: Int,
    val year: Int,
    val hour: Int,
    val minute: Int,
) {

    fun getDate(): Date {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        return calendar.time
    }
}