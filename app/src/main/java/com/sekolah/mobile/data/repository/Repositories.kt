package com.sekolah.mobile.data.repository

import android.content.Context
import com.google.gson.Gson
import com.sekolah.mobile.data.model.Guru
import com.sekolah.mobile.data.model.SessionResponse
import com.sekolah.mobile.data.model.UserProfileResponse
import com.sekolah.mobile.data.remote.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepository(private val context: Context) {
    private val apiClient = ApiClient(context)
    private val gson = Gson()

    private val prefs = context.getSharedPreferences("sekolah_auth_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_TOKEN = "auth_access_token"
        private const val KEY_REFRESH_TOKEN = "auth_refresh_token"
        private const val KEY_PROFILE_JSON = "auth_profile_json"
    }

    private val _currentUserProfile = MutableStateFlow<UserProfileResponse?>(null)
    val currentUserProfile: StateFlow<UserProfileResponse?> = _currentUserProfile.asStateFlow()

    init {
        loadCachedProfile()
    }

    private fun loadCachedProfile() {
        val cached = prefs.getString(KEY_PROFILE_JSON, null)
        if (cached != null) {
            try {
                _currentUserProfile.value = gson.fromJson(cached, UserProfileResponse::class.java)
            } catch (_: Exception) {}
        }
    }

    fun getSavedToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun hasActiveSession(): Boolean = !getSavedToken().isNullOrBlank()

    suspend fun login(email: String, password: String): UserProfileResponse {
        val session = apiClient.login(email, password)
        prefs.edit()
            .putString(KEY_TOKEN, session.accessToken)
            .putString(KEY_REFRESH_TOKEN, session.refreshToken)
            .apply()

        // Fetch fresh profile
        val profile = apiClient.fetchProfile(session.accessToken)
        _currentUserProfile.value = profile
        prefs.edit()
            .putString(KEY_PROFILE_JSON, gson.toJson(profile))
            .apply()

        return profile
    }

    suspend fun refreshProfile(): UserProfileResponse? {
        val token = getSavedToken() ?: return null
        return try {
            val fresh = apiClient.fetchProfile(token)
            _currentUserProfile.value = fresh
            prefs.edit().putString(KEY_PROFILE_JSON, gson.toJson(fresh)).apply()
            fresh
        } catch (_: Exception) {
            _currentUserProfile.value
        }
    }

    fun logout() {
        prefs.edit().clear().apply()
        _currentUserProfile.value = null
    }
}

class GuruRepository(private val context: Context) {
    private val apiClient = ApiClient(context)
    private val authRepository = AuthRepository(context)

    suspend fun getDaftarGuru(): List<Guru> {
        val token = authRepository.getSavedToken()
            ?: throw IllegalStateException("Sesi telah berakhir. Silakan login kembali.")
        return apiClient.getDaftarGuru(token)
    }
}

