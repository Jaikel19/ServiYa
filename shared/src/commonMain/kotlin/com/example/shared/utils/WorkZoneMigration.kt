package com.example.shared.utils

import com.example.shared.data.repository.User.IUserRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first

/**
 * Migración one-time: agrega explícitamente el campo `isDefault = false` a todas las
 * workZones existentes en Firestore que no lo tenían.
 *
 * Llama a esta función UNA SOLA VEZ desde el ViewModel de administración o desde
 * un botón de debug en la app. Después de ejecutarla todos los documentos tendrán
 * el campo `isDefault` en Firestore.
 *
 * Returns: par (zonasActualizadas, errores)
 */
suspend fun migrateWorkZonesAddIsDefault(
    repository: IUserRepository,
): Pair<Int, Int> {
    var updated = 0
    var errors = 0

    val workers = try {
        repository.getAllWorkers().catch { emit(emptyList()) }.first()
    } catch (e: Exception) {
        return Pair(0, 1)
    }

    for (worker in workers) {
        try {
            val zones = repository.getWorkZonesByUser(worker.uid)
                .catch { emit(emptyList()) }
                .first()

            // Elegir la zona principal: primera no bloqueada con coordenadas, o la primera en general
            val principalId = zones
                .firstOrNull { !it.blocked && (it.latitude != 0.0 || it.longitude != 0.0) }?.id
                ?: zones.firstOrNull()?.id

            for (zone in zones) {
                try {
                    val shouldBeDefault = zone.id == principalId
                    repository.updateWorkZone(worker.uid, zone.copy(isDefault = shouldBeDefault))
                    updated++
                } catch (e: Exception) {
                    errors++
                }
            }
        } catch (e: Exception) {
            errors++
        }
    }

    return Pair(updated, errors)
}
