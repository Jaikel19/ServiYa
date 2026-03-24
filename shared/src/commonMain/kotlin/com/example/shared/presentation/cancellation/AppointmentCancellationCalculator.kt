package com.example.shared.presentation.cancellation

import com.example.shared.domain.entity.Appointment
import com.example.shared.domain.entity.CancellationPolicy

object AppointmentCancellationCalculator {

    fun buildClientPreview(
        appointment: Appointment,
        cancellationPolicy: CancellationPolicy?,
        currentDateTime: String
    ): AppointmentCancellationPreview {
        val minutesUntilAppointment = minutesBetween(
            start = currentDateTime,
            end = appointment.serviceStartAt
        )

        val resolved = resolveClientRefundRule(
            minutesUntilAppointment = minutesUntilAppointment,
            cancellationPolicy = cancellationPolicy
        )

        val refundAmount = ((appointment.totalCost * resolved.percentage) / 100.0).toInt()
        val nonRefundableAmount = appointment.totalCost - refundAmount

        return AppointmentCancellationPreview(
            cancelledBy = "cliente",
            policyLabel = resolved.label,
            refundPercentage = resolved.percentage,
            refundAmount = refundAmount.coerceAtLeast(0),
            nonRefundableAmount = nonRefundableAmount.coerceAtLeast(0),
            appointmentTotal = appointment.totalCost,
            warningMessage = "El reembolso, si corresponde, se realizará mediante comprobante."
        )
    }

    fun buildWorkerPreview(
        appointment: Appointment
    ): AppointmentCancellationPreview {
        return AppointmentCancellationPreview(
            cancelledBy = "trabajador",
            policyLabel = "Cancelación realizada por el trabajador",
            refundPercentage = 100,
            refundAmount = appointment.totalCost,
            nonRefundableAmount = 0,
            appointmentTotal = appointment.totalCost,
            warningMessage = "El reembolso se realizará al cliente mediante comprobante."
        )
    }

    private data class RefundRule(
        val label: String,
        val percentage: Int
    )

    private fun resolveClientRefundRule(
        minutesUntilAppointment: Long,
        cancellationPolicy: CancellationPolicy?
    ): RefundRule {
        val safePolicy = cancellationPolicy ?: CancellationPolicy()

        return when {
            minutesUntilAppointment >= MINUTES_7_DAYS -> {
                RefundRule(
                    label = "7 días o más antes",
                    percentage = safePolicy.before7DaysOrMore
                )
            }

            minutesUntilAppointment >= MINUTES_3_DAYS -> {
                RefundRule(
                    label = "Entre 3 y 6 días",
                    percentage = safePolicy.between3And6Days
                )
            }

            minutesUntilAppointment >= MINUTES_48_HOURS -> {
                RefundRule(
                    label = "48 horas antes",
                    percentage = safePolicy.before48h
                )
            }

            minutesUntilAppointment >= MINUTES_24_HOURS -> {
                RefundRule(
                    label = "24 horas antes",
                    percentage = safePolicy.before24h
                )
            }

            else -> {
                RefundRule(
                    label = "Mismo día o menos de 24h",
                    percentage = safePolicy.sameDayOrLess24h
                )
            }
        }
    }

    private fun minutesBetween(
        start: String,
        end: String
    ): Long {
        val startParsed = parseDateTime(start) ?: return 0L
        val endParsed = parseDateTime(end) ?: return 0L

        val startMinutes = toAbsoluteMinutes(startParsed)
        val endMinutes = toAbsoluteMinutes(endParsed)

        return endMinutes - startMinutes
    }

    private data class SimpleDateTime(
        val year: Int,
        val month: Int,
        val day: Int,
        val hour: Int,
        val minute: Int
    )

    private fun parseDateTime(raw: String): SimpleDateTime? {
        if (raw.isBlank()) return null

        return try {
            val normalized = raw.trim().replace(" ", "T")
            val datePart = normalized.substringBefore("T")
            val timePart = normalized.substringAfter("T", "00:00")

            val datePieces = datePart.split("-")
            if (datePieces.size != 3) return null

            val timePieces = timePart.split(":")
            val hour = timePieces.getOrNull(0)?.toIntOrNull() ?: 0
            val minute = timePieces.getOrNull(1)?.take(2)?.toIntOrNull() ?: 0

            SimpleDateTime(
                year = datePieces[0].toIntOrNull() ?: return null,
                month = datePieces[1].toIntOrNull() ?: return null,
                day = datePieces[2].toIntOrNull() ?: return null,
                hour = hour,
                minute = minute
            )
        } catch (_: Exception) {
            null
        }
    }

    private fun toAbsoluteMinutes(dateTime: SimpleDateTime): Long {
        val days = daysFromCivil(
            year = dateTime.year,
            month = dateTime.month,
            day = dateTime.day
        )

        return (days * 24L * 60L) + (dateTime.hour * 60L) + dateTime.minute
    }

    private fun daysFromCivil(
        year: Int,
        month: Int,
        day: Int
    ): Long {
        var y = year
        var m = month.toLong()
        val d = day.toLong()

        y -= if (m <= 2L) 1 else 0
        val era = if (y >= 0) y / 400 else (y - 399) / 400
        val yoe = (y - era * 400).toLong()
        m = m + if (m > 2L) -3L else 9L
        val doy = (153L * m + 2L) / 5L + d - 1L
        val doe = yoe * 365L + yoe / 4L - yoe / 100L + doy
        return era.toLong() * 146097L + doe - 719468L
    }

    private const val MINUTES_24_HOURS = 24L * 60L
    private const val MINUTES_48_HOURS = 48L * 60L
    private const val MINUTES_3_DAYS = 3L * 24L * 60L
    private const val MINUTES_7_DAYS = 7L * 24L * 60L
}