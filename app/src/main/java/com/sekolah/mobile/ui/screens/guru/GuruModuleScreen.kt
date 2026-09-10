package com.sekolah.mobile.ui.screens.guru

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sekolah.mobile.data.model.Guru
import com.sekolah.mobile.data.repository.GuruRepository
import com.sekolah.mobile.ui.theme.PrimaryTeal
import com.sekolah.mobile.ui.theme.PrimaryTealContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class GuruUiState(
    val allGuru: List<Guru> = emptyList(),
    val filteredGuru: List<Guru> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val selectedGuru: Guru? = null
)

class GuruViewModel(application: Application) : AndroidViewModel(application) {
    private val guruRepository = GuruRepository(application)

    private val _uiState = MutableStateFlow(GuruUiState())
    val uiState: StateFlow<GuruUiState> = _uiState.asStateFlow()

    init {
        loadGuruList()
    }

    fun loadGuruList() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val list = guruRepository.getDaftarGuru()
                _uiState.value = _uiState.value.copy(
                    allGuru = list,
                    filteredGuru = filterGuru(list, _uiState.value.searchQuery),
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Gagal memuat data guru."
                )
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(
            searchQuery = query,
            filteredGuru = filterGuru(_uiState.value.allGuru, query)
        )
    }

    fun selectGuru(guru: Guru?) {
        _uiState.value = _uiState.value.copy(selectedGuru = guru)
    }

    private fun filterGuru(list: List<Guru>, query: String): List<Guru> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return list
        return list.filter {
            it.nama.lowercase().contains(q) ||
            it.nip.lowercase().contains(q) ||
            (it.jabatan?.lowercase()?.contains(q) == true)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuruModuleScreen(
    viewModel: GuruViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Modal BottomSheet for Detail
    if (state.selectedGuru != null) {
        val guru = state.selectedGuru!!
        ModalBottomSheet(
            onDismissRequest = { viewModel.selectGuru(null) },
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(PrimaryTealContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = guru.initials,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = PrimaryTeal
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = guru.nama,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = guru.displayJabatan,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = PrimaryTeal,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                DetailItem(icon = Icons.Default.Badge, label = "NIP", value = guru.nip)
                Spacer(modifier = Modifier.height(12.dp))
                DetailItem(
                    icon = Icons.Default.Phone,
                    label = "Nomor Telepon",
                    value = guru.telp?.ifEmpty { "Tidak tersedia" } ?: "Tidak tersedia",
                    canCopy = !guru.telp.isNullOrEmpty(),
                    onCopy = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Telepon", guru.telp))
                        Toast.makeText(context, "Nomor telepon disalin", Toast.LENGTH_SHORT).show()
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
                DetailItem(
                    icon = Icons.Default.Chat,
                    label = "WhatsApp",
                    value = guru.wa?.ifEmpty { "Tidak tersedia" } ?: "Tidak tersedia",
                    canCopy = !guru.wa.isNullOrEmpty(),
                    onCopy = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("WhatsApp", guru.wa))
                        Toast.makeText(context, "Nomor WhatsApp disalin", Toast.LENGTH_SHORT).show()
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Modul Guru") },
                actions = {
                    IconButton(onClick = viewModel::loadGuruList) {
                        Icon(Icons.Default.Refresh, contentDescription = "Muat Ulang")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Bar
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = viewModel::onSearchQueryChanged,
                placeholder = { Text("Cari nama guru, NIP, atau mata pelajaran...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (state.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp)
            )

            // Header Count
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daftar Pendidik & Pengajar",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Surface(
                    color = PrimaryTealContainer,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "${state.filteredGuru.size} Guru",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = PrimaryTeal,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Body Content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                when {
                    state.isLoading -> {
                        CircularProgressIndicator(color = PrimaryTeal)
                    }
                    state.errorMessage != null -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudOff,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(54.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Gagal Memuat Data",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = state.errorMessage!!,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = viewModel::loadGuruList) {
                                Icon(Icons.Default.Refresh, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Coba Lagi")
                            }
                        }
                    }
                    state.filteredGuru.isEmpty() -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.PersonSearch,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (state.searchQuery.isEmpty()) "Belum ada data guru" else "Guru tidak ditemukan",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    else -> {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(state.filteredGuru, key = { it.nip }) { guru ->
                                GuruCard(guru = guru, onClick = { viewModel.selectGuru(guru) })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GuruCard(guru: Guru, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(PrimaryTealContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = guru.initials,
                    color = PrimaryTeal,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = guru.nama,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = guru.displayJabatan,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = PrimaryTeal,
                        fontWeight = FontWeight.Medium
                    )
                )
                Text(
                    text = "NIP: ${guru.nip}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun DetailItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    canCopy: Boolean = false,
    onCopy: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
        if (canCopy) {
            IconButton(onClick = onCopy) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Salin $label",
                    tint = PrimaryTeal,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

