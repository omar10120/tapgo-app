package app.taplinks.vendor.utils

import kotlin.math.roundToInt

object MoneyFormatter {
    fun format(amount: Double): String {
        return "%.2f".format(amount)
    }
}