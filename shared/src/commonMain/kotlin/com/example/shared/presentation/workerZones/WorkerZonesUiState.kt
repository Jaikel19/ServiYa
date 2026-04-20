package com.example.shared.presentation.workerZones

import com.example.shared.data.local.costaRicaProvinces
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
        // 82 cantones oficiales INEC — código de cantón como ID estable en Firestore
        val ALL_ZONES: List<ZoneItem> = costaRicaProvinces.flatMap { province ->
            province.cantons.map { canton ->
                ZoneItem(
                    id = canton.code,
                    canton = canton.name,
                    province = province.name,
                    locationCode = canton.code,
                )
            }
        }

        val PROVINCES: List<String> = costaRicaProvinces.map { it.name }
    }
}
