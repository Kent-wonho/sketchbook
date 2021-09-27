package com.kent.sketchbook

import android.content.Context
import android.util.TypedValue

public fun convertDpToPx(dp: Int, context: Context): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), context?.resources?.displayMetrics).toInt()
}