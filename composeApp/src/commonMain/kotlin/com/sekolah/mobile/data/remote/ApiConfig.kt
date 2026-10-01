package com.sekolah.mobile.data.remote

import com.sekolah.mobile.data.storage.getDefaultServerHost
import com.sekolah.mobile.data.storage.getPlatformStorage

object ApiConfig {
    const val AUTH_PORT = 8000
    const val API_PORT = 8080

    private const val KEY_CUSTOM_HOST = "custom_server_host"

    fun getHost(): String {
        val storage = getPlatformStorage()
        return storage.getString(KEY_CUSTOM_HOST, null)?.trim()?.takeIf { it.isNotEmpty() }
            ?: getDefaultServerHost()
    }

    fun setHost(newHost: String) {
        val storage = getPlatformStorage()
        val clean = newHost.trim().ifEmpty { getDefaultServerHost() }
        storage.setString(KEY_CUSTOM_HOST, clean)
    }

    fun getTokenUrl(): String =
        "http://${getHost()}:$AUTH_PORT/token?grant_type=password"

    fun getProfileUrl(): String =
        "http://${getHost()}:$API_PORT/auth-flow/profile"

    fun getGuruUrl(): String =
        "http://${getHost()}:$API_PORT/management/guru"

    fun getVerifyUrl(): String =
        "http://${getHost()}:$API_PORT/auth-flow/verify"

    fun getRegisterUrl(): String =
        "http://${getHost()}:$API_PORT/auth-flow/register"
}

