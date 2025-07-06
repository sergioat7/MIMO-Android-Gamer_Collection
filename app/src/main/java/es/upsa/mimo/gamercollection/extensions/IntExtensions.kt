/*
 * Copyright (c) 2022 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 28/2/2022
 */

package es.upsa.mimo.gamercollection.extensions

import java.text.DecimalFormat

fun Int.getFormatted(): String {
    return DecimalFormat("#,###").format(this)
}