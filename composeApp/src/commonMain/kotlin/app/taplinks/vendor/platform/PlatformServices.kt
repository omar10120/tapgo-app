package app.taplinks.vendor.platform

expect class PlatformServices {

    fun copyToClipboard(text: String)

    fun shareText(text: String, title: String = "Share")

    fun openWhatsApp(phoneNumber: String, message: String)
}