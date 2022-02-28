/*
 * Copyright (c) 2022 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 28/2/2022
 */

package es.upsa.mimo.gamercollection.extensions

import android.util.Log
import es.upsa.mimo.gamercollection.utils.Constants
import java.text.SimpleDateFormat
import java.util.*

fun String?.toDate(
    format: String? = null,
    language: String? = null,
    timeZone: TimeZone? = null
): Date? {

    val dateFormat = format ?: Constants.DATE_FORMAT
    val locale = language?.let {
        Locale.forLanguageTag(it)
    } ?: run {
        Locale.getDefault()
    }
    val simpleDateFormat = SimpleDateFormat(dateFormat, locale)
    simpleDateFormat.timeZone = timeZone ?: TimeZone.getDefault()

    this?.let {

        return try {
            simpleDateFormat.parse(it)
        } catch (e: Exception) {

            Log.e("StringExtensions", e.message ?: "")
            null
        }
    } ?: run {
        Log.e("StringExtensions", "dateString null")
        return null
    }
}