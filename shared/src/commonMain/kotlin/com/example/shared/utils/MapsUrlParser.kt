package com.example.shared.utils

import io.ktor.client.HttpClient
import io.ktor.client.request.get

private val COORD = """(-?\d{1,3}\.\d+)"""

/**
 * Extrae (latitud, longitud) de los formatos comunes de URL de Google Maps,
 * o de coordenadas pegadas directamente (ej: "9.9330, -84.0826").
 * Retorna null si no encuentra coordenadas.
 */
fun extractLatLngFromMapsUrl(url: String): Pair<Double, Double>? {
    val trimmed = url.trim()

    // Patrón 0: coordenadas directas "lat, lng" o "lat,lng" (sin URL)
    if (!trimmed.startsWith("http")) {
        val plainPattern = Regex("""^$COORD\s*,\s*$COORD$""")
        plainPattern.find(trimmed)?.let { match ->
            val lat = match.groupValues[1].toDoubleOrNull() ?: return@let
            val lng = match.groupValues[2].toDoubleOrNull() ?: return@let
            if (lat in -90.0..90.0 && lng in -180.0..180.0) return Pair(lat, lng)
        }
    }

    // Patrón 1: @lat,lng[,zoom] — URLs estándar de Maps y Place
    val atPattern = Regex("""@$COORD,$COORD""")
    atPattern.find(trimmed)?.let { match ->
        val lat = match.groupValues[1].toDoubleOrNull() ?: return@let
        val lng = match.groupValues[2].toDoubleOrNull() ?: return@let
        if (lat in -90.0..90.0 && lng in -180.0..180.0) return Pair(lat, lng)
    }

    // Patrón 2: ?q=lat,lng o &q=lat,lng
    val qPattern = Regex("""[?&]q=$COORD,$COORD""")
    qPattern.find(trimmed)?.let { match ->
        val lat = match.groupValues[1].toDoubleOrNull() ?: return@let
        val lng = match.groupValues[2].toDoubleOrNull() ?: return@let
        if (lat in -90.0..90.0 && lng in -180.0..180.0) return Pair(lat, lng)
    }

    // Patrón 3: !3dlat!4dlng — embebido en URLs de Place/Place detail
    val dataPattern = Regex("""!3d$COORD!4d$COORD""")
    dataPattern.find(trimmed)?.let { match ->
        val lat = match.groupValues[1].toDoubleOrNull() ?: return@let
        val lng = match.groupValues[2].toDoubleOrNull() ?: return@let
        if (lat in -90.0..90.0 && lng in -180.0..180.0) return Pair(lat, lng)
    }

    // Patrón 4: ll=lat,lng — formato antiguo de Maps
    val llPattern = Regex("""[?&]ll=$COORD,$COORD""")
    llPattern.find(trimmed)?.let { match ->
        val lat = match.groupValues[1].toDoubleOrNull() ?: return@let
        val lng = match.groupValues[2].toDoubleOrNull() ?: return@let
        if (lat in -90.0..90.0 && lng in -180.0..180.0) return Pair(lat, lng)
    }

    // Patrón 5: /place/lat,lng — URLs de pin directo
    val placePattern = Regex("""/place/$COORD,$COORD""")
    placePattern.find(trimmed)?.let { match ->
        val lat = match.groupValues[1].toDoubleOrNull() ?: return@let
        val lng = match.groupValues[2].toDoubleOrNull() ?: return@let
        if (lat in -90.0..90.0 && lng in -180.0..180.0) return Pair(lat, lng)
    }

    return null
}

/**
 * Resuelve la URL (siguiendo redirects si es necesario) y luego extrae las coordenadas.
 * Útil para links cortos como maps.app.goo.gl que redirigen a la URL completa de Maps.
 */
suspend fun resolveAndExtractLatLng(url: String, httpClient: HttpClient): Pair<Double, Double>? {
    // Primero intentar parsear directamente (para URLs largas ya conocidas)
    val direct = extractLatLngFromMapsUrl(url)
    if (direct != null) return direct

    // Seguir redirects para obtener la URL final
    return try {
        val response = httpClient.get(url)
        val finalUrl = response.call.request.url.toString()
        extractLatLngFromMapsUrl(finalUrl)
    } catch (e: Exception) {
        null
    }
}
