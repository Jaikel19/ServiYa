package com.example.seviya

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform