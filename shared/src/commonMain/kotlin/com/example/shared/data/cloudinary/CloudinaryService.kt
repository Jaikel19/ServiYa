package com.example.shared.data.cloudinary

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import io.ktor.client.request.forms.submitFormWithBinaryData

class CloudinaryService(private val client: HttpClient) {

    private val cloudName = "dfk5xx88f"
    private val uploadPreset = "seviya_unsigned"

    suspend fun uploadImage(imageBytes: ByteArray, fileName: String): String {
        return try {
            val url = "https://api.cloudinary.com/v1_1/$cloudName/image/upload"

            val response = client.post(url) {
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append("upload_preset", uploadPreset)
                            append("file", imageBytes, Headers.build {
                                append(HttpHeaders.ContentType, "image/jpeg")
                                append(HttpHeaders.ContentDisposition, "form-data; name=\"file\"; filename=\"$fileName.jpg\"")
                            })
                        }
                    )
                )
            }

            val responseText = response.bodyAsText()
            println("DEBUG Cloudinary response: $responseText")

            val json = Json.parseToJsonElement(responseText).jsonObject
            json["secure_url"]?.jsonPrimitive?.content ?: ""
        } catch (e: Exception) {
            println("ERROR Cloudinary: ${e.message}")
            ""
        }
    }
}
