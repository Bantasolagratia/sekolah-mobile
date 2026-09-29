package com.sekolah.mobile

import com.sekolah.mobile.data.model.Guru
import com.sekolah.mobile.data.model.LoginRequest
import com.sekolah.mobile.data.model.SessionResponse
import com.sekolah.mobile.data.model.UserIdentity
import com.sekolah.mobile.data.model.UserProfileResponse
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ModelTest {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Test
    fun testLoginRequestSerialization() {
        val req = LoginRequest(email = "test@murid.sekolah.com", password = "SecretPassword123")
        val serialized = json.encodeToString(req)
        assertTrue(serialized.contains("test@murid.sekolah.com"))
        assertTrue(serialized.contains("SecretPassword123"))
    }

    @Test
    fun testSessionResponseDeserialization() {
        val sampleJson = """
            {
                "access_token": "mock-jwt-token-12345",
                "token_type": "bearer",
                "expires_in": 3600,
                "refresh_token": "mock-refresh-token",
                "user": {
                    "id": "usr-123",
                    "email": "wrenley@murid.sekolah.com"
                }
            }
        """.trimIndent()

        val session = json.decodeFromString<SessionResponse>(sampleJson)
        assertEquals("mock-jwt-token-12345", session.accessToken)
        assertEquals("bearer", session.tokenType)
        assertEquals("wrenley@murid.sekolah.com", session.user?.email)
    }

    @Test
    fun testUserProfileResponseMuridRoleDetection() {
        val muridProfile = UserProfileResponse(
            idUser = "usr-001",
            email = "wrenley@murid.sekolah.com",
            isStudent = true,
            roles = listOf("ROLE_MURID"),
            identities = listOf(
                UserIdentity(
                    id = "202610012",
                    name = "Wrenley",
                    detail = "Kelas 10-C",
                    role = "MURID",
                    isStudent = true
                )
            )
        )

        assertTrue(muridProfile.isRoleMurid)
        assertEquals("Wrenley", muridProfile.displayName)
        assertEquals("Kelas 10-C", muridProfile.displayDetail)
        assertEquals("202610012", muridProfile.nis)

        val nonMuridProfile = UserProfileResponse(
            idUser = "usr-002",
            email = "guru@sekolah.com",
            isStudent = false,
            isTeacher = true,
            roles = listOf("ROLE_GURU")
        )
        assertFalse(nonMuridProfile.isRoleMurid)
    }

    @Test
    fun testGuruInitialsAndDisplay() {
        val guru1 = Guru(nip = "19800101", nama = "Budi Raharjo", jabatan = "Guru Matematika")
        assertEquals("BR", guru1.initials)
        assertEquals("Guru Matematika", guru1.displayJabatan)

        val guru2 = Guru(nip = "19850202", nama = "Siti", jabatan = null)
        assertEquals("S", guru2.initials)
        assertEquals("Guru Pengajar", guru2.displayJabatan)

        val guru3 = Guru(nip = "19900303", nama = "Dr. H. Ahmad Dahlan, M.Pd", jabatan = "Guru Fisika")
        assertEquals("DH", guru3.initials)
    }
}

