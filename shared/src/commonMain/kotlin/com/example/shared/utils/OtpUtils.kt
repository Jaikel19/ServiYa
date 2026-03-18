package com.example.shared.utils

expect object OtpUtils {
    fun generateOtpCode(): String
    fun sha256(input: String): String
}