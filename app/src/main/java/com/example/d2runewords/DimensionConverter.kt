package com.example.d2runewords

import android.content.res.Resources
import android.util.TypedValue

class DimensionConverter {
    companion object instance {
        fun dp2px(dp: Int): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp.toFloat(),
                Resources.getSystem().getDisplayMetrics()
            ).toInt();
        }
    }
}