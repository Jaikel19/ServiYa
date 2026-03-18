package com.example.shared.utils

import java.security.MessageDigest
import kotlin.random.Random

actual object OtpUtils {

    actual fun generateOtpCode(): String {
        return Random.nextInt(100000, 1000000).toString()
    }

    actual fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { byte ->
            "%02x".format(byte)
        }
    }
}