/*
 * Copyright (c) 2022 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 9/3/2022
 */

package es.upsa.mimo.gamercollection.extensions

import android.content.SharedPreferences

fun SharedPreferences.Editor.setString(key: String, value: String?) {
    putString(key, value)
    commit()
}

fun SharedPreferences.Editor.setInt(key: String, value: Int) {
    putInt(key, value)
    commit()
}

fun SharedPreferences.Editor.setBoolean(key: String, value: Boolean) {
    putBoolean(key, value)
    commit()
}