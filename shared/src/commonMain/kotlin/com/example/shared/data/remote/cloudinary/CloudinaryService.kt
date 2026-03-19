package com.example.shared.data.remote.cloudinary

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class CloudinaryService {

    private val cloudName = "dfk5xx88f"
    private val uploadPreset = "seviya_unsigned"

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    suspend fun uploadImage(imageBytes: ByteArray, fileName: String): String {
        val url = "https://api.cloudinary.com/v1_1/$cloudName/image/upload"

        val response = client.post(url) {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("upload_preset", uploadPreset)
                        append("file", imageBytes, Headers.build {
                            append(HttpHeaders.ContentType, "image/jpeg")
                            append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                        })
                    }
                )
            )
        }

        val responseText = response.bodyAsText()
        val json = Json.parseToJsonElement(responseText).jsonObject
        return json["secure_url"]?.jsonPrimitive?.content ?: ""
    }
}