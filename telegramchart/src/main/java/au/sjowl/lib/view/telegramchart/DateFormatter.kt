package au.sjowl.lib.view.telegramchart

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.GregorianCalendar
import java.util.Locale

object DateFormatter {
    private val calendar = GregorianCalendar()
    @SuppressLint("ConstantLocale")
    private val locale = Locale.getDefault()
    private val dateFormat = SimpleDateFormat("MMM d", locale)
    private val dateFormatLong = SimpleDateFormat("EEE, MMM d", locale)

    fun format(timeInMillisec: Long): String {
        calendar.timeInMillis = timeInMillisec
        return dateFormat.format(calendar.time)
    }

    fun formatLong(timeInMillisec: Long): String {
        calendar.timeInMillis = timeInMillisec
        return dateFormatLong.format(calendar.time)
    }
}