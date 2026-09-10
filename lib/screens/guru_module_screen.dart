import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import '../models/guru_model.dart';
import '../services/auth_service.dart';
import '../services/guru_service.dart';

class GuruModuleScreen extends StatefulWidget {
  const GuruModuleScreen({super.key});

  @override
  State<GuruModuleScreen> createState() => _GuruModuleScreenState();
}

class _GuruModuleScreenState extends State<GuruModuleScreen> {
  final _searchController = TextEditingController();
  List<GuruModel> _allGuru = [];
  List<GuruModel> _filteredGuru = [];
  bool _isLoading = true;
  String? _errorMessage;

  @override
  void initState() {
    super.initState();
    _loadGuruData();
  }

  @override
  void dispose() {
    _searchController.dispose();
    super.dispose();
  }

  Future<void> _loadGuruData() async {
    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });

    try {
      final token = AuthService().session?.accessToken ?? '';
      final list = await GuruService().getDaftarGuru(token);
      if (!mounted) return;

      setState(() {
        _allGuru = list;
        _applyFilter(_searchController.text);
        _isLoading = false;
      });
    } catch (e) {
      if (!mounted) return;
      setState(() {
        _errorMessage = e.toString().replaceFirst('Exception: ', '').replaceFirst('HttpException: ', '');
        _isLoading = false;
      });
    }
  }

  void _applyFilter(String query) {
    final q = query.trim().toLowerCase();
    if (q.isEmpty) {
      _filteredGuru = List.from(_allGuru);
    } else {
      _filteredGuru = _allGuru.where((g) {
        final namaMatch = g.nama.toLowerCase().contains(q);
        final nipMatch = g.nip.toLowerCase().contains(q);
        final jabatanMatch = (g.jabatan ?? '').toLowerCase().contains(q);
        return namaMatch || nipMatch || jabatanMatch;
      }).toList();
    }
  }

  void _showGuruDetail(GuruModel guru) {
    showModalBottomSheet(
      context: context,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
      ),
      builder: (ctx) => Padding(
        padding: const EdgeInsets.symmetric(horizontal: 24.0, vertical: 20.0),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Center(
              child: Container(
                width: 40,
                height: 4,
                decoration: BoxDecoration(
                  color: Colors.grey.shade300,
                  borderRadius: BorderRadius.circular(2),
                ),
              ),
            ),
            const SizedBox(height: 16),
            Row(
              children: [
                CircleAvatar(
                  radius: 28,
                  backgroundColor: Theme.of(context).colorScheme.primaryContainer,
                  child: Text(
                    guru.initials,
                    style: TextStyle(
                      fontSize: 18,
                      fontWeight: FontWeight.bold,
                      color: Theme.of(context).colorScheme.primary,
                    ),
                  ),
                ),
                const SizedBox(width: 16),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        guru.nama,
                        style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
                      ),
                      const SizedBox(height: 4),
                      Text(
                        guru.displayJabatan,
                        style: TextStyle(
                          fontSize: 14,
                          color: Theme.of(context).colorScheme.primary,
                          fontWeight: FontWeight.w500,
                        ),
                      ),
                    ],
                  ),
                ),
              ],
            ),
            const Divider(height: 32),
            _buildDetailRow(Icons.badge_outlined, 'NIP', guru.nip),
            const SizedBox(height: 12),
            _buildDetailRow(
              Icons.phone_outlined,
              'Telepon',
              guru.telp != null && guru.telp!.isNotEmpty ? guru.telp! : 'Tidak tersedia',
              canCopy: guru.telp != null && guru.telp!.isNotEmpty,
            ),
            const SizedBox(height: 12),
            _buildDetailRow(
              Icons.chat_bubble_outline_rounded,
              'WhatsApp',
              guru.wa != null && guru.wa!.isNotEmpty ? guru.wa! : 'Tidak tersedia',
              canCopy: guru.wa != null && guru.wa!.isNotEmpty,
            ),
            const SizedBox(height: 20),
          ],
        ),
      ),
    );
  }

  Widget _buildDetailRow(IconData icon, String label, String value, {bool canCopy = false}) {
    return Row(
      children: [
        Icon(icon, size: 20, color: Colors.grey.shade600),
        const SizedBox(width: 12),
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                label,
                style: TextStyle(fontSize: 12, color: Colors.grey.shade600),
              ),
              Text(
                value,
                style: const TextStyle(fontSize: 14, fontWeight: FontWeight.w500),
              ),
            ],
          ),
        ),
        if (canCopy)
          IconButton(
            icon: const Icon(Icons.copy_rounded, size: 18),
            tooltip: 'Salin $label',
            onPressed: () {
              Clipboard.setData(ClipboardData(text: value));
              ScaffoldMessenger.of(context).showSnackBar(
                SnackBar(content: Text('$label disalin ke papan klip')),
              );
            },
          ),
      ],
    );
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return Scaffold(
      appBar: AppBar(
        title: const Text('Modul Guru'),
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh_rounded),
            tooltip: 'Muat Ulang',
            onPressed: _loadGuruData,
          ),
        ],
      ),
      body: Column(
        children: [
          // Search Input Bar
          Padding(
            padding: const EdgeInsets.all(16.0),
            child: TextField(
              controller: _searchController,
              onChanged: (val) {
                setState(() {
                  _applyFilter(val);
                });
              },
              decoration: InputDecoration(
                hintText: 'Cari nama guru, NIP, atau mata pelajaran...',
                prefixIcon: const Icon(Icons.search_rounded),
                suffixIcon: _searchController.text.isNotEmpty
                    ? IconButton(
                        icon: const Icon(Icons.clear_rounded),
                        onPressed: () {
                          _searchController.clear();
                          setState(() {
                            _applyFilter('');
                          });
                        },
                      )
                    : null,
                border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
                filled: true,
                fillColor: theme.colorScheme.surfaceContainerHighest.withOpacity(0.3),
                contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
              ),
            ),
          ),

          // Total Count Info
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 16.0),
            child: Row(
              children: [
                Text(
                  'Daftar Pendidik & Pengajar',
                  style: theme.textTheme.titleSmall?.copyWith(fontWeight: FontWeight.bold),
                ),
                const Spacer(),
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
                  decoration: BoxDecoration(
                    color: theme.colorScheme.primaryContainer,
                    borderRadius: BorderRadius.circular(12),
                  ),
                  child: Text(
                    '${_filteredGuru.length} Guru',
                    style: TextStyle(
                      fontSize: 12,
                      fontWeight: FontWeight.bold,
                      color: theme.colorScheme.primary,
                    ),
                  ),
                ),
              ],
            ),
          ),
          const SizedBox(height: 8),

          // Content List / Loading / Error
          Expanded(
            child: _isLoading
                ? const Center(child: CircularProgressIndicator())
                : _errorMessage != null
                    ? Center(
                        child: Padding(
                          padding: const EdgeInsets.all(24.0),
                          child: Column(
                            mainAxisSize: MainAxisSize.min,
                            children: [
                              Icon(Icons.cloud_off_rounded, size: 54, color: theme.colorScheme.error),
                              const SizedBox(height: 12),
                              Text(
                                'Gagal Memuat Data',
                                style: theme.textTheme.titleMedium?.copyWith(fontWeight: FontWeight.bold),
                              ),
                              const SizedBox(height: 6),
                              Text(
                                _errorMessage!,
                                textAlign: TextAlign.center,
                                style: theme.textTheme.bodySmall?.copyWith(color: Colors.grey),
                              ),
                              const SizedBox(height: 16),
                              FilledButton.icon(
                                onPressed: _loadGuruData,
                                icon: const Icon(Icons.refresh_rounded),
                                label: const Text('Coba Lagi'),
                              ),
                            ],
                          ),
                        ),
                      )
                    : _filteredGuru.isEmpty
                        ? Center(
                            child: Column(
                              mainAxisSize: MainAxisSize.min,
                              children: [
                                Icon(Icons.person_search_rounded, size: 54, color: Colors.grey.shade400),
                                const SizedBox(height: 12),
                                Text(
                                  _searchController.text.isEmpty
                                      ? 'Belum ada data guru'
                                      : 'Guru tidak ditemukan',
                                  style: TextStyle(fontSize: 16, color: Colors.grey.shade600),
                                ),
                              ],
                            ),
                          )
                        : RefreshIndicator(
                            onRefresh: _loadGuruData,
                            child: ListView.separated(
                              padding: const EdgeInsets.all(16),
                              itemCount: _filteredGuru.length,
                              separatorBuilder: (_, __) => const SizedBox(height: 10),
                              itemBuilder: (ctx, index) {
                                final guru = _filteredGuru[index];
                                return Card(
                                  elevation: 0,
                                  shape: RoundedRectangleBorder(
                                    borderRadius: BorderRadius.circular(12),
                                    side: BorderSide(color: Colors.grey.shade200),
                                  ),
                                  child: ListTile(
                                    contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                                    leading: CircleAvatar(
                                      backgroundColor: theme.colorScheme.primaryContainer,
                                      child: Text(
                                        guru.initials,
                                        style: TextStyle(
                                          fontWeight: FontWeight.bold,
                                          color: theme.colorScheme.primary,
                                        ),
                                      ),
                                    ),
                                    title: Text(
                                      guru.nama,
                                      style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 15),
                                    ),
                                    subtitle: Column(
                                      crossAxisAlignment: CrossAxisAlignment.start,
                                      children: [
                                        const SizedBox(height: 4),
                                        Text(
                                          guru.displayJabatan,
                                          style: TextStyle(
                                            color: theme.colorScheme.primary,
                                            fontWeight: FontWeight.w500,
                                            fontSize: 13,
                                          ),
                                        ),
                                        const SizedBox(height: 2),
                                        Text(
                                          'NIP: ${guru.nip}',
                                          style: TextStyle(fontSize: 12, color: Colors.grey.shade600),
                                        ),
                                      ],
                                    ),
                                    trailing: const Icon(Icons.chevron_right_rounded),
                                    onTap: () => _showGuruDetail(guru),
                                  ),
                                );
                              },
                            ),
                          ),
          ),
        ],
      ),
    );
  }
}

