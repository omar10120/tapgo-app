package app.taplinks.vendor.config

object AppConfig {

    const val IS_PROD = true

    val API_ENDPOINT = if (IS_PROD) "taplink-be.vercel.app" else "staging-taplink-be.vercel.app"
    
    val API_BASE_URL = if (IS_PROD) "https://taplink-be.vercel.app/api/v1/" else "https://staging-taplink-be.vercel.app/api/v1/"
    
    val FRONTEND_BASE_URL = if (IS_PROD) "https://taplinks.app" else "https://taplinks.vercel.app"

    object Endpoints {
        val PAYMENT_PAGE = "$FRONTEND_BASE_URL/payment"
    }
}
