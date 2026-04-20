package com.example.shared.domain.entity

data class CostaRicaDistrict(val code: String, val name: String)

data class CostaRicaCanton(
    val code: String,
    val name: String,
    val districts: List<CostaRicaDistrict>,
)

data class CostaRicaProvince(
    val code: String,
    val name: String,
    val cantons: List<CostaRicaCanton>,
)

data class LocationSelection(
    val provinceName: String,
    val provinceCode: String,
    val cantonName: String,
    val cantonCode: String,
    val districtName: String,
    val districtCode: String,
)
