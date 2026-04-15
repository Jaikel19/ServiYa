package com.example.shared.presentation.workerZones

import com.example.shared.domain.entity.WorkZone

data class ZoneItem(
    val id: String,
    val canton: String,
    val province: String,
    val district: String = "",
    val locationCode: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
)

data class WorkerZonesUiState(
    val isLoading: Boolean = true,
    // Zonas que el trabajador tiene en Firestore (subcolección workZones)
    val workerZones: List<WorkZone> = emptyList(),
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null,
) {
    val servedZoneIds: Set<String> get() = workerZones.map { it.id }.toSet()
    val blockedZoneIds: Set<String> get() = workerZones.filter { it.blocked }.map { it.id }.toSet()
    val activeZoneIds: Set<String> get() = workerZones.filter { !it.blocked }.map { it.id }.toSet()
    val selectedCount: Int get() = workerZones.size

    fun selectedCountByProvince(province: String): Int =
        workerZones.count { wz -> ALL_ZONES.find { it.id == wz.id }?.province == province }

    companion object {
        val ALL_ZONES: List<ZoneItem> = listOf(
            // San José
            ZoneItem("sj-escazu", "Escazú", "San José"),
            ZoneItem("sj-santa-ana", "Santa Ana", "San José"),
            ZoneItem("sj-curridabat", "Curridabat", "San José"),
            ZoneItem("sj-san-pedro", "San Pedro", "San José"),
            ZoneItem("sj-desamparados", "Desamparados", "San José"),
            ZoneItem("sj-montes-de-oca", "Montes de Oca", "San José"),
            ZoneItem("sj-goicoechea", "Goicoechea", "San José"),
            ZoneItem("sj-moravia", "Moravia", "San José"),
            // Heredia
            ZoneItem("he-heredia", "Heredia Centro", "Heredia"),
            ZoneItem("he-belen", "Belén", "Heredia"),
            ZoneItem("he-san-francisco", "San Francisco", "Heredia"),
            ZoneItem("he-santo-domingo", "Santo Domingo", "Heredia"),
            ZoneItem("he-san-pablo", "San Pablo", "Heredia"),
            ZoneItem("he-flores", "Flores", "Heredia"),
            // Alajuela
            ZoneItem("al-alajuela", "Alajuela Centro", "Alajuela"),
            ZoneItem("al-coyol", "Alajuela Coyol", "Alajuela"),
            ZoneItem("al-san-rafael", "San Rafael", "Alajuela"),
            ZoneItem("al-poas", "Poás", "Alajuela"),
            // Cartago
            ZoneItem("ca-cartago", "Cartago Centro", "Cartago"),
            ZoneItem("ca-tres-rios", "Tres Ríos", "Cartago"),
            ZoneItem("ca-la-union", "La Unión", "Cartago"),
        )

        val PROVINCES = listOf("San José", "Heredia", "Alajuela", "Cartago")
    }
}
