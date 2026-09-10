# Sekolah Mobile App (Native Android)

Aplikasi mobile resmi portal sekolah berbasis **Native Android murni** dengan **Kotlin & Jetpack Compose (Material 3)**, dirancang untuk mendukung ekosistem pendidikan terintegrasi dengan Auth Service (GoTrue) dan Backend Spring Boot (`sekolah-app`).

---

## 🛠️ Stack Teknologi

- **Bahasa Pemrograman**: Kotlin `2.0.21`
- **UI Toolkit**: Jetpack Compose (Material 3)
- **Runtime & Kompilasi Java**: **Java 17 LTS**
- **Sistem Build**: **Gradle 8.10** & Android Gradle Plugin (AGP) `8.7.0`
- **Android Target**: `compileSdk = 35`, `minSdk = 24`, `targetSdk = 35`
- **Networking**: OkHttp `4.12.0` & Gson `2.11.0`
- **Arsitektur**: MVVM (Model-View-ViewModel) dengan StateFlow & Coroutines

---

## 🚀 Fitur & Alur Bisnis

1. **Integrasi Auth Service (GoTrue port 8000)**:
   - Endpoint: `POST /token?grant_type=password`
   - Manajemen sesi aman dan auto-login tersimpan di SharedPreferences.
2. **Integrasi Profil Backend (Spring Boot port 8080)**:
   - Endpoint: `GET /auth-flow/profile`
   - Mendeteksi peran siswa (`isStudent` / `ROLE_MURID`), nama lengkap, Nomor Induk Siswa (NIS), dan Kelas (`Kelas 10-C`).
3. **Modul Guru Khusus Siswa (Murid)**:
   - Endpoint: `GET /management/guru`
   - Direktori pendidik & pengajar sekolah.
   - Pencarian real-time berdasarkan Nama, NIP, atau Mata Pelajaran / Jabatan.
   - Avatar inisial otomatis untuk setiap pengajar.
   - Modal detail kontak dengan tombol salin nomor Telepon dan WhatsApp langsung ke papan klip.
4. **Konfigurasi Host Server Dinamis**:
   - Default: `10.0.2.2` untuk Android Emulator (atau `localhost` / IP LAN Wi-Fi untuk pengujian di perangkat fisik).
   - Dialog pengaturan host server langsung diakses dari layar login.
5. **Dashboard & Navigasi**:
   - Header kartu identitas siswa dengan gradien elegan dan badge `PERAN: MURID`.
   - Kartu pintasan cepat ke Modul Guru.
   - Scaffold Bottom Navigation (`Beranda`, `Modul Guru`, `Profil`).
   - Halaman profil dengan konfirmasi dialog keluar (*logout*).

---

## 🧪 Akun Uji Coba Pengembang

Tersedia akun siswa terverifikasi di backend:
- **Email**: `wrenley@murid.sekolah.com`
- **Kata Sandi**: `Password123!`
- **Nama Siswa**: Wrenley Roth
- **Kelas**: Kelas 10-C (NIS: 202610012)

---

## 📱 Cara Menjalankan Project

Buka project ini di **Android Studio** atau compile via terminal:

```bash
# Clone repositori
git clone https://github.com/Bantasolagratia/sekolah-mobile.git
cd sekolah-mobile

# Menjalankan unit test
./gradlew test

# Build APK Debug
./gradlew assembleDebug
```
