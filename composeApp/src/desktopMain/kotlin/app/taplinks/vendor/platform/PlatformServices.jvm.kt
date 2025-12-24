package app.taplinks.vendor.platform

actual class PlatformServices {
    
    actual fun copyToClipboard(text: String) {
        try {
            val clipboard = java.awt.Toolkit.getDefaultToolkit().systemClipboard
            val stringSelection = java.awt.datatransfer.StringSelection(text)
            clipboard.setContents(stringSelection, null)
            println("Content copied to clipboard: $text")
        } catch (e: Exception) {
            println("Failed to copy to clipboard")
            e.printStackTrace()
        }
    }
    
    actual fun shareText(text: String, title: String) {
        
        copyToClipboard(text)
        println("Shared content copied to clipboard: $text")
    }
    
    actual fun openWhatsApp(phoneNumber: String, message: String) {
        
        try {
            val encodedMessage = java.net.URLEncoder.encode(message, "UTF-8")
            val url = "https://wa.me/$phoneNumber?text=$encodedMessage"
            val desktop = java.awt.Desktop.getDesktop()
            if (desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
                desktop.browse(java.net.URI(url))
            }
        } catch (e: Exception) {
            println("Failed to open WhatsApp: $e")
            e.printStackTrace()
        }
    }
}