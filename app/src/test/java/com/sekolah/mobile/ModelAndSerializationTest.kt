package com.sekolah.mobile

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sekolah.mobile.data.model.Guru
import com.sekolah.mobile.data.model.SessionResponse
import com.sekolah.mobile.data.model.UserIdentity
import com.sekolah.mobile.data.model.UserProfileResponse
import org.junit.Assert.*
import org.junit.Test

class ModelAndSerializationTest {
    private val gson = Gson()

    @Test
    fun testSessionResponseDeserialization() {
        val json = """
            {
                "access_token": "mock_jwt_access_token_xyz",
                "token_type": "bearer",
                "expires_in": 86400,
                "refresh_token": "mock_refresh_token_123",
                "user": {
                    "id": "f2c4215d-bc90-49a8-9636-64205628d534",
                    "email": "wrenley@murid.sekolah.com"
                }
            }
        """.trimIndent()

        val session = gson.fromJson(json, SessionResponse::class.java)
        assertEquals("mock_jwt_access_token_xyz", session.accessToken)
        assertEquals("bearer", session.tokenType)
        assertEquals(86400L, session.expiresIn)
        assertEquals("mock_refresh_token_123", session.refreshToken)
        assertNotNull(session.user)
        assertEquals("wrenley@murid.sekolah.com", session.user?.email)
    }

    @Test
    fun testUserProfileResponseMuridRole() {
        val json = """
            {
                "idUser": "f2c4215d-bc90-49a8-9636-64205628d534",
                "email": "wrenley@murid.sekolah.com",
                "isAdmin": false,
                "isTeacher": false,
                "isStudent": true,
                "isGuardian": false,
                "roles": ["MURID"],
                "identities": [
                    {
                        "id": "202610012",
                        "name": "Wrenley Roth",
                        "detail": "Kelas 10-C",
                        "role": "MURID",
                        "isStudent": true
                    }
                ]
            }
        """.trimIndent()

        val profile = gson.fromJson(json, UserProfileResponse::class.java)
        assertTrue(profile.isRoleMurid)
        assertEquals("Wrenley Roth", profile.displayName)
        assertEquals("Kelas 10-C", profile.displayDetail)
        assertEquals("202610012", profile.nis)
        assertEquals(1, profile.identities.size)
        assertTrue(profile.identities[0].isStudent)
    }

    @Test
    fun testGuruModelInitialsAndParsing() {
        val json = """
            [
                {
                    "nip": "198501012010011004",
                    "nama": "Jasper Novak",
                    "jabatan": "Guru Fisika",
                    "telp": "081210000004",
                    "wa": "081210000004"
                },
                {
                    "nip": "198501012010011005",
                    "nama": "Madelynn Cannon",
                    "jabatan": "Guru Kimia",
                    "telp": "081210000005",
                    "wa": "081210000005"
                }
            ]
        """.trimIndent()

        val listType = object : TypeToken<List<Guru>>() {}.type
        val guruList: List<Guru> = gson.fromJson(json, listType)

        assertEquals(2, guruList.size)
        assertEquals("Jasper Novak", guruList[0].nama)
        assertEquals("JN", guruList[0].initials)
        assertEquals("Guru Fisika", guruList[0].displayJabatan)

        assertEquals("Madelynn Cannon", guruList[1].nama)
        assertEquals("MC", guruList[1].initials)
        assertEquals("Guru Kimia", guruList[1].displayJabatan)
    }
}

