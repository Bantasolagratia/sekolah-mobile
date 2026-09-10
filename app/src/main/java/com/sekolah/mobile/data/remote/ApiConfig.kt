package com.sekolah.mobile.data.remote

import android.content.Context
import android.content.SharedPreferences

object ApiConfig {
    const val DEFAULT_HOST = "10.0.2.2"
    const val AUTH_PORT = 8000
    const val API_PORT = 8080

    private const val PREFS_NAME = "sekolah_api_prefs"
    private const val KEY_CUSTOM_HOST = "custom_server_host"

    fun getHost(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_CUSTOM_HOST, null)?.trim()?.takeIf { it.isNotEmpty() } ?: DEFAULT_HOST
    }

    fun setHost(context: Context, newHost: String) {
        val clean = newHost.trim().ifEmpty { DEFAULT_HOST }
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_CUSTOM_HOST, clean)
            .apply()
    }

    fun getTokenUrl(context: Context): String =
        "http://${getHost(context)}:$AUTH_PORT/token?grant_type=password"

    fun getProfileUrl(context: Context): String =
        "http://${getHost(context)}:$API_PORT/auth-flow/profile"

    fun getGuruUrl(context: Context): String =
        "http://${getHost(context)}:$API_PORT/management/guru"
}

