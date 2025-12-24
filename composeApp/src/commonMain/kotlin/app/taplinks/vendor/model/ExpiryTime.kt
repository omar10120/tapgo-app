package app.taplinks.vendor.model

enum class ExpiryTime(val displayName: String, val hours: Int) {
    ONE_HOUR("1 Hour", 1),
    THREE_HOURS("3 Hours", 3),
    TWELVE_HOURS("12 Hours", 12),
    TWENTY_FOUR_HOURS("24 Hours", 24)
}