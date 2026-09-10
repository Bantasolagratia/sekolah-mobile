package com.sekolah.mobile.data.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val email: String,
    val password: String
)

data class UserDto(
    val id: String?,
    val email: String?
)

data class SessionResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String? = null,
    @SerializedName("expires_in") val expiresIn: Long? = null,
    @SerializedName("refresh_token") val refreshToken: String? = null,
    val user: UserDto? = null
)

data class UserIdentity(
    val id: String,
    val name: String? = null,
    val detail: String? = null,
    val role: String? = null,
    val isStudent: Boolean = false,
    val isTeacher: Boolean = false,
    val isAdmin: Boolean = false,
    val isGuardian: Boolean = false
)

data class UserProfileResponse(
    val idUser: String? = null,
    val email: String? = null,
    val isAdmin: Boolean = false,
    val isTeacher: Boolean = false,
    val isStudent: Boolean = false,
    val isGuardian: Boolean = false,
    val roles: List<String> = emptyList(),
    val identities: List<UserIdentity> = emptyList()
) {
    val isRoleMurid: Boolean
        get() = isStudent || roles.contains("MURID")

    val studentIdentity: UserIdentity?
        get() = identities.firstOrNull { it.isStudent || it.role == "MURID" } ?: identities.firstOrNull()

    val displayName: String
        get() = studentIdentity?.name?.trim()?.takeIf { it.isNotEmpty() }
            ?: email?.substringBefore('@')?.takeIf { it.isNotEmpty() }
            ?: "Siswa"

    val displayDetail: String
        get() = studentIdentity?.detail?.trim()?.takeIf { it.isNotEmpty() }
            ?: "Kelas 10-C"

    val nis: String
        get() = studentIdentity?.id ?: "-"
}

data class Guru(
    val nip: String,
    val nama: String,
    val jabatan: String? = null,
    val telp: String? = null,
    val wa: String? = null
) {
    val initials: String
        get() {
            val parts = nama.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
            return when {
                parts.isEmpty() -> "?"
                parts.size >= 2 -> "${parts[0][0]}${parts[1][0]}".uppercase()
                else -> "${parts[0][0]}".uppercase()
            }
        }

    val displayJabatan: String
        get() = jabatan?.trim()?.takeIf { it.isNotEmpty() } ?: "Guru Pengajar"
}

