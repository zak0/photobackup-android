package com.jamitek.photosapp.util

import java.text.SimpleDateFormat
import java.util.*

/**
 * Collection of methods for dealing with date and time.
 */
object DateUtil {

    private const val TAG = "DateUtil"
    const val EPOCH_EXIF = "1970:01:01 00:00:00"
    private const val EXIF_DATEFORMAT = "yyyy:MM:dd" // 2020:07:29
    private const val NICE_DATEFORMAT = "EEE, d MMM yyyy" // Wed, 6 Dec 2020
    private const val NICE_MONTH_AND_YEAR = "MMMM yyyy" /// December 2020

    /**
     * Converts a date [String] from how datetime is represented in EXIF tags into a nicer date
     * to display on the UI.
     *
     * Example of an "EXIF" formatted date: "2019:06:28 19:55:31"
     *
     * @return A nice date string, if parsing the input EXIF format date succeeded,
     *  "unknown" otherwise.
     */
    fun exifDateToNiceDate(exifDateString: String): String {
        val dateString = exifDateString.split(" ")[0]
        val date = try {
            SimpleDateFormat(EXIF_DATEFORMAT, Locale.US).parse(dateString)
        } catch(e: Exception) {
            null
        }

        // Date is null if parsing failed
        return date?.let {
            SimpleDateFormat(NICE_DATEFORMAT, Locale.US).format(it)
        } ?: "Unknown"
    }

    fun exifDateToNiceMonthAndYear(exifDateString: String): String {
        val dateString = exifDateString.split(" ")[0]
        val date = try {
            SimpleDateFormat(EXIF_DATEFORMAT, Locale.US).parse(dateString)
        } catch(e: Exception) {
            null
        }

        // Date is null if parsing failed
        return date?.let {
            SimpleDateFormat(NICE_MONTH_AND_YEAR, Locale.US).format(it)
        } ?: "Unknown"
    }

    fun dateToExifDate(date: Date): String {
        return SimpleDateFormat(NICE_DATEFORMAT, Locale.US).format(date)
    }
}