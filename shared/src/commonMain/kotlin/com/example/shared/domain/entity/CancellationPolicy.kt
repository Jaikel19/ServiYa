package com.example.shared.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class CancellationPolicy(
    val before24h: Int = 0,
    val before48h: Int = 0,
    val before7DaysOrMore: Int = 0,
    val between3And6Days: Int = 0,
    val sameDayOrLess24h: Int = 0,
)
