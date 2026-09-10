package com.sekolah.mobile.data.remote

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.sekolah.mobile.data.model.Guru
import com.sekolah.mobile.data.model.LoginRequest
import com.sekolah.mobile.data.model.SessionResponse
import com.sekolah.mobile.data.model.UserProfileResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

class ApiClient(private val context: Context) {
    private val gson = Gson()
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    suspend fun login(email: String, password: String): SessionResponse = withContext(Dispatchers.IO) {
        val url = ApiConfig.getTokenUrl(context)
        val bodyJson = gson.toJson(LoginRequest(email = email.trim(), password = password))
        val requestBody = bodyJson.toRequestBody(jsonMediaType)

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")
            .build()

        client.newCall(request).execute().use { response ->
            val responseBody = response.body?.string() ?: ""
            if (!response.isSuccessful) {
                var message = "Email atau password tidak valid."
                try {
                    val errorObj = gson.fromJson(responseBody, JsonObject::class.java)
                    if (errorObj.has("error_description")) {
                        message = errorObj.get("error_description").asString
                    } else if (errorObj.has("msg")) {
                        message = errorObj.get("msg").asString
                    } else if (errorObj.has("message")) {
                        message = errorObj.get("message").asString
                    }
                } catch (_: Exception) {}
                throw IOException(message)
            }

            gson.fromJson(responseBody, SessionResponse::class.java)
        }
    }

    suspend fun fetchProfile(token: String): UserProfileResponse = withContext(Dispatchers.IO) {
        val url = ApiConfig.getProfileUrl(context)
        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Authorization", "Bearer $token")
            .addHeader("Accept", "application/json")
            .build()

        client.newCall(request).execute().use { response ->
            val responseBody = response.body?.string() ?: ""
            if (!response.isSuccessful) {
                var message = "Gagal mengambil profil (${response.code})"
                try {
                    val errorObj = gson.fromJson(responseBody, JsonObject::class.java)
                    if (errorObj.has("message")) {
                        message = errorObj.get("message").asString
                    }
                } catch (_: Exception) {}
                throw IOException(message)
            }

            gson.fromJson(responseBody, UserProfileResponse::class.java)
        }
    }

    suspend fun getDaftarGuru(token: String): List<Guru> = withContext(Dispatchers.IO) {
        val url = ApiConfig.getGuruUrl(context)
        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Authorization", "Bearer $token")
            .addHeader("Accept", "application/json")
            .build()

        client.newCall(request).execute().use { response ->
            val responseBody = response.body?.string() ?: ""
            if (!response.isSuccessful) {
                var message = "Gagal mengambil data guru (${response.code})"
                try {
                    val errorObj = gson.fromJson(responseBody, JsonObject::class.java)
                    if (errorObj.has("message")) {
                        message = errorObj.get("message").asString
                    }
                } catch (_: Exception {} )
                throw IOException(message)
            }

            val listType = object : TypeToken<List<Guru>>() {}.type
            gson.fromJson<List<Guru>>(responseBody, listType) ?: emptyList()
        }
    }
}

