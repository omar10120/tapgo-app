package app.taplinks.vendor

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform