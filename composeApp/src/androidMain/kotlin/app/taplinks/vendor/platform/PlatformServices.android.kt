package app.taplinks.vendor.platform

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri

actual class PlatformServices(private val context: Context) {
    
    actual fun copyToClipboard(text: String) {
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("Payment Link", text)
        clipboardManager.setPrimaryClip(clipData)
    }
    
    actual fun shareText(text: String, title: String) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            putExtra(Intent.EXTRA_SUBJECT, title)
        }
        
        val chooserIntent = Intent.createChooser(shareIntent, title)
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooserIntent)
    }
    
    actual fun openWhatsApp(phoneNumber: String, message: String) {
        try {
            
            val cleanNumber = phoneNumber.replace(Regex("[^\\d+]"), "")
            
            val whatsappIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://wa.me/$cleanNumber?text=${Uri.encode(message)}")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            
            context.startActivity(whatsappIntent)
        } catch (e: Exception) {
            
            val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("sms:$phoneNumber")
                putExtra("sms_body", message)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            
            try {
                context.startActivity(smsIntent)
            } catch (ex: Exception) {
                
            }
        }
    }
}