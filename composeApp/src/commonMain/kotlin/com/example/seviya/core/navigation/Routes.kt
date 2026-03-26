package com.example.seviya.core.navigation

import kotlinx.serialization.Serializable

// Invitado
@Serializable object Landing

@Serializable object RoleCatalog

@Serializable object RoleAdmissionCatalog

@Serializable object TravelTimeConfig

@Serializable object Services

// Cliente
@Serializable object CategoriesCatalog

@Serializable object ClientMap

@Serializable object ClientSearch

@Serializable object ClientAlerts

@Serializable object ClientRequests

@Serializable object ClientHome

@Serializable object ClientDashboard

@Serializable object ClientAgenda

@Serializable object ClientProfile

@Serializable object ClientMessages

@Serializable object ClientConfiguration

@Serializable object ClientSettings

@Serializable data class ClientDailyAppointments(val clientId: String)

@Serializable data class ClientWeeklyAppointments(val clientId: String)

@Serializable data class ClientAppointmentDetail(val bookingId: String)

@Serializable data class ClientToWorkerReview(val appointmentId: String)

@Serializable data class WorkerDailyAppointments(val workerId: String)

@Serializable object RequestAppointment

// Trabajador
@Serializable object WorkerDashboard

@Serializable object WorkerAgenda

@Serializable object WorkerRequests

@Serializable data class WorkerRequestDetail(val bookingId: String)

@Serializable data class WorkerPaymentDetail(val bookingId: String)

@Serializable object WorkerAlerts

@Serializable object WorkerProfile

@Serializable object WorkerMessages

@Serializable object WorkerConfiguration

@Serializable object WorkerSettings

@Serializable object WorkerReports

@Serializable object WorkerPortfolio

@Serializable object WorkerServices

@Serializable object WorkerSchedule

@Serializable object WorkerCategories

@Serializable object WorkerAppointmentDetail

@Serializable data class WorkerWeeklyAppointments(val workerId: String)

@Serializable data class WorkerDailyAgenda(val workerId: String)

@Serializable data class WorkerStartAppointmentOtp(val appointmentId: String)

@Serializable data class WorkerToClientReview(val appointmentId: String)

@Serializable object ClientFavorites

@Serializable object ClientLocationCatalog

@Serializable data class ClientPaymentUpload(val appointmentId: String)

@Serializable data class WorkersList(val categoryId: String?, val categoryName: String?)

@Serializable data class ProfessionalProfile(val workerId: String)
