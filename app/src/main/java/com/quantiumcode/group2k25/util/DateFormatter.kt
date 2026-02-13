package com.quantiumcode.group2k25.util

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

object DateFormatter {

    private val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    private val isoFormatMs = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    private val dateOnlyFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    private val brDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))

    private val brDateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR"))

    fun formatDate(isoDate: String?): String {
        if (isoDate == null) return "-"
        return try {
            val date = parseIso(isoDate)
            if (date != null) brDateFormat.format(date) else isoDate.take(10)
        } catch (e: Exception) {
            isoDate.take(10)
        }
    }

    fun formatDateTime(isoDate: String?): String {
        if (isoDate == null) return "-"
        return try {
            val date = parseIso(isoDate)
            if (date != null) brDateTimeFormat.format(date) else isoDate
        } catch (e: Exception) {
            isoDate
        }
    }

    fun parseIso(isoDate: String): java.util.Date? {
        return try {
            isoFormatMs.parse(isoDate)
        } catch (e: Exception) {
            try {
                isoFormat.parse(isoDate)
            } catch (e2: Exception) {
                try {
                    dateOnlyFormat.parse(isoDate)
                } catch (e3: Exception) {
                    null
                }
            }
        }
    }
}
