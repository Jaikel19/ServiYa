package com.example.shared.data.repository.workersList

import com.example.shared.data.remote.workersList.IRemoteWorkersListDataSource
import com.example.shared.data.remote.workersList.WorkerRemoteItem
import com.example.shared.data.repository.Service.IServiceRepository
import com.example.shared.domain.entity.WorkZone
import com.example.shared.domain.entity.WorkerListItemData
import com.example.shared.domain.entity.WorkerSchedule
import com.example.shared.presentation.workersList.WorkersListFilters
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class WorkersListRepository(
    private val remote: IRemoteWorkersListDataSource,
    private val servicesRepository: IServiceRepository,
) : IWorkersListRepository {

    private suspend fun getStartingPrice(workerId: String): Double {
        val services =
            try {
                servicesRepository.getServicesByWorker(workerId).first()
            } catch (e: Exception) {
                emptyList()
            }

        return services.minOfOrNull { it.cost } ?: 0.0
    }

    private suspend fun buildWorkerListItem(
        workerRemote: WorkerRemoteItem,
        categoryNameMap: Map<String, String>,
        includeSchedule: Boolean,
        includeWorkZones: Boolean,
    ): WorkerListItemData = coroutineScope {
        val workerId = workerRemote.workerId
        val profile = workerRemote.profile

        val addressDeferred = async { remote.getWorkerAddress(workerId) }
        val startingPriceDeferred = async { getStartingPrice(workerId) }
        val scheduleDeferred =
            if (includeSchedule) {
                async { remote.getWorkerSchedule(workerId) }
            } else {
                null
            }
        val workZonesDeferred =
            if (includeWorkZones) {
                async { remote.getWorkerWorkZones(workerId) }
            } else {
                null
            }

        val address = addressDeferred.await()
        val startingPrice = startingPriceDeferred.await()
        val schedule = scheduleDeferred?.await() ?: emptyList()
        val workZones = workZonesDeferred?.await() ?: emptyList()

        val categoryNames =
            profile.categories.mapNotNull { categoryId ->
                categoryNameMap[categoryId]
            }

        WorkerListItemData(
            workerId = workerId,
            uid = profile.uid,
            name = profile.name,
            profilePictureLink = profile.profilePicture,
            stars = profile.stars,
            status = profile.status,
            categoryIds = profile.categories,
            categoryNames = categoryNames,
            province = address?.province.orEmpty(),
            canton = address?.canton.orEmpty(),
            district = address?.district.orEmpty(),
            startingPrice = startingPrice,
            schedule = schedule,
            appointments = emptyList(),
            workZones = workZones,
        )
    }

    private suspend fun buildWorkerPreviewItem(
        workerRemote: WorkerRemoteItem,
        categoryNameMap: Map<String, String>,
    ): WorkerListItemData = coroutineScope {
        val workerId = workerRemote.workerId
        val profile = workerRemote.profile

        val addressDeferred = async { remote.getWorkerAddress(workerId) }
        val startingPriceDeferred = async { getStartingPrice(workerId) }

        val address = addressDeferred.await()
        val startingPrice = startingPriceDeferred.await()

        val categoryNames =
            profile.categories.mapNotNull { categoryId ->
                categoryNameMap[categoryId]
            }

        WorkerListItemData(
            workerId = workerId,
            uid = profile.uid,
            name = profile.name,
            profilePictureLink = profile.profilePicture,
            stars = profile.stars,
            status = profile.status,
            categoryIds = profile.categories,
            categoryNames = categoryNames,
            province = address?.province.orEmpty(),
            canton = address?.canton.orEmpty(),
            district = address?.district.orEmpty(),
            startingPrice = startingPrice,
        )
    }

    override suspend fun getWorkers(filters: WorkersListFilters): Flow<List<WorkerListItemData>> {
        return remote.getWorkers().map { workers ->
            coroutineScope {
                val candidateWorkers =
                    workers.filter { workerRemote ->
                        matchesFastProfileFilters(workerRemote, filters)
                    }

                val allCategoryIds =
                    candidateWorkers
                        .flatMap { it.profile.categories }
                        .toSet()

                val categoryNameMap = remote.getCategoryNamesMap(allCategoryIds)

                val includeSchedule = filters.hasDayFilter()
                val includeWorkZones = filters.hasLocationFilter()

                candidateWorkers
                    .map { workerRemote ->
                        async {
                            buildWorkerListItem(
                                workerRemote = workerRemote,
                                categoryNameMap = categoryNameMap,
                                includeSchedule = includeSchedule,
                                includeWorkZones = includeWorkZones,
                            )
                        }
                    }
                    .awaitAll()
                    .filter { worker ->
                        matchesFinalFilters(worker, filters)
                    }
                    .sortedByDescending { it.stars }
            }
        }
    }

    override suspend fun getWorkersByIds(workerIds: Set<String>): List<WorkerListItemData> {
        val workers = remote.getWorkersByIds(workerIds)

        val categoryNameMap =
            remote.getCategoryNamesMap(
                workers.flatMap { it.profile.categories }.toSet()
            )

        return coroutineScope {
            workers
                .map { workerRemote ->
                    async {
                        buildWorkerPreviewItem(
                            workerRemote = workerRemote,
                            categoryNameMap = categoryNameMap,
                        )
                    }
                }
                .awaitAll()
        }.sortedByDescending { it.stars }
    }

    override suspend fun getFavoriteWorkerIds(clientId: String): Flow<Set<String>> {
        return remote.getFavoriteWorkerIds(clientId)
    }

    override suspend fun addFavorite(clientId: String, workerId: String) {
        remote.addFavorite(clientId, workerId)
    }

    override suspend fun removeFavorite(clientId: String, workerId: String) {
        remote.removeFavorite(clientId, workerId)
    }

    private fun matchesFastProfileFilters(
        workerRemote: WorkerRemoteItem,
        filters: WorkersListFilters,
    ): Boolean {
        val selectedCategoryId = filters.selectedCategoryId.orEmpty()

        if (selectedCategoryId.isNotBlank() &&
            !workerRemote.profile.categories.contains(selectedCategoryId)
        ) {
            return false
        }

        return true
    }

    private fun matchesFinalFilters(
        worker: WorkerListItemData,
        filters: WorkersListFilters,
    ): Boolean {
        return matchesCategory(worker, filters) &&
                matchesSearch(worker, filters.searchQuery) &&
                matchesSelectedDay(worker.schedule, filters.selectedDayKey) &&
                matchesSelectedZone(worker.workZones, filters)
    }

    private fun matchesCategory(
        worker: WorkerListItemData,
        filters: WorkersListFilters,
    ): Boolean {
        val selectedCategoryId = filters.selectedCategoryId.orEmpty()
        val selectedCategoryName = normalizeText(filters.selectedCategoryName.orEmpty())

        val hasCategoryFilter =
            selectedCategoryId.isNotBlank() || selectedCategoryName.isNotBlank()

        if (!hasCategoryFilter) return true

        val idMatch =
            selectedCategoryId.isNotBlank() &&
                    worker.categoryIds.contains(selectedCategoryId)

        val nameMatch =
            selectedCategoryName.isNotBlank() &&
                    worker.categoryNames.any { categoryName ->
                        val normalizedCategory = normalizeText(categoryName)
                        normalizedCategory == selectedCategoryName ||
                                normalizedCategory.contains(selectedCategoryName)
                    }

        return idMatch || nameMatch
    }

    private fun matchesSearch(
        worker: WorkerListItemData,
        searchQuery: String,
    ): Boolean {
        val normalizedQuery = normalizeText(searchQuery)
        if (normalizedQuery.isBlank()) return true

        val searchIndex =
            buildString {
                append(normalizeText(worker.name))
                append(' ')
                append(normalizeText(worker.district))
                append(' ')
                append(normalizeText(worker.canton))
                append(' ')
                append(normalizeText(worker.province))

                worker.categoryNames.forEach { category ->
                    append(' ')
                    append(normalizeText(category))
                }
            }.trim()

        return searchIndex.contains(normalizedQuery)
    }

    private fun matchesSelectedDay(
        schedule: List<WorkerSchedule>,
        selectedDayKey: String,
    ): Boolean {
        val normalizedDayKey = normalizeText(selectedDayKey)
        if (normalizedDayKey.isBlank()) return true
        if (schedule.isEmpty()) return false

        val expectedDayNumber = dayNumberFromKey(normalizedDayKey)

        return schedule.any { day ->
            if (!day.enabled) return@any false

            val hasValidBlock =
                day.timeBlocks.any { block ->
                    val start = parseMinutes(block.start)
                    val end = parseMinutes(block.end)
                    start != null && end != null && end > start
                }

            if (!hasValidBlock) return@any false

            val dayKeyMatches = normalizeText(day.dayKey) == normalizedDayKey
            val dayNumberMatches =
                expectedDayNumber != null && day.dayNumber == expectedDayNumber

            dayKeyMatches || dayNumberMatches
        }
    }

    private fun matchesSelectedZone(
        workZones: List<WorkZone>,
        filters: WorkersListFilters,
    ): Boolean {
        if (!filters.hasLocationFilter()) return true
        if (workZones.isEmpty()) return false

        val selectedProvince = normalizeText(filters.selectedProvince)
        val selectedCanton = normalizeText(filters.selectedCanton)
        val selectedDistrict = normalizeText(filters.selectedDistrict)

        val provinceRules =
            workZones.filter { zone ->
                normalizeText(zone.province) == selectedProvince
            }

        if (provinceRules.isEmpty()) return false

        val districtRules =
            provinceRules.filter { zone ->
                normalizeText(zone.canton) == selectedCanton &&
                        normalizeText(zone.district) == selectedDistrict
            }

        val cantonRules =
            provinceRules.filter { zone ->
                normalizeText(zone.canton) == selectedCanton &&
                        normalizeText(zone.district).isBlank()
            }

        val provinceOnlyRules =
            provinceRules.filter { zone ->
                normalizeText(zone.canton).isBlank() &&
                        normalizeText(zone.district).isBlank()
            }

        if (districtRules.any { it.blocked }) return false
        if (cantonRules.any { it.blocked }) return false
        if (provinceOnlyRules.any { it.blocked }) return false

        if (districtRules.any { !it.blocked }) return true
        if (cantonRules.any { !it.blocked }) return true
        if (provinceOnlyRules.any { !it.blocked }) return true

        return false
    }

    private fun normalizeText(value: String): String {
        return value.trim().lowercase()
    }

    private fun parseMinutes(value: String): Int? {
        val clean = value.trim()
        if (clean.isBlank()) return null

        val timePart = clean.substringAfterLast('T')
        val parts = timePart.split(":")
        if (parts.size < 2) return null

        val hour = parts.getOrNull(0)?.toIntOrNull() ?: return null
        val minute = parts.getOrNull(1)?.toIntOrNull() ?: return null

        return (hour * 60) + minute
    }

    private fun dayNumberFromKey(dayKey: String): Int? {
        return when (normalizeText(dayKey)) {
            "monday" -> 1
            "tuesday" -> 2
            "wednesday" -> 3
            "thursday" -> 4
            "friday" -> 5
            "saturday" -> 6
            "sunday" -> 7
            else -> null
        }
    }
}