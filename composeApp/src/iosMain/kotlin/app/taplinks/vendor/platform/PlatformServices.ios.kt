package app.taplinks.vendor.platform

import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenURLOptionsAnnotation
import platform.UIKit.UIPasteboard
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIViewController
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
actual class PlatformServices {
    
    actual fun copyToClipboard(text: String) {
        UIPasteboard.generalPasteboard.string = text
    }
    
    actual fun shareText(text: String, title: String) {
        val activityViewController = UIActivityViewController(
            activityItems = listOf(text),
            applicationActivities = null
        )

        val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
        rootViewController?.presentViewController(
            activityViewController, 
            animated = true, 
            completion = null
        )
    }
    
    actual fun openWhatsApp(phoneNumber: String, message: String) {
        
        val cleanNumber = phoneNumber.replace(Regex("[^\\d+]"), "")

        val whatsappUrlString = "https://wa.me/$cleanNumber?text=${message.replace(" ", "%20")}"
        val whatsappUrl = NSURL.URLWithString(whatsappUrlString)
        
        if (whatsappUrl != null && UIApplication.sharedApplication.canOpenURL(whatsappUrl)) {
            UIApplication.sharedApplication.openURL(
                whatsappUrl,
                options = emptyMap<UIApplicationOpenURLOptionsAnnotation, Any>(),
                completionHandler = null
            )
        } else {
            
            val smsUrlString = "sms:$phoneNumber&body=${message.replace(" ", "%20")}"
            val smsUrl = NSURL.URLWithString(smsUrlString)
            
            if (smsUrl != null && UIApplication.sharedApplication.canOpenURL(smsUrl)) {
                UIApplication.sharedApplication.openURL(
                    smsUrl,
                    options = emptyMap<UIApplicationOpenURLOptionsAnnotation, Any>(),
                    completionHandler = null
                )
            }
        }
    }
}