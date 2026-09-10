class GuruModel {
  final String nip;
  final String nama;
  final String? jabatan;
  final String? telp;
  final String? wa;

  const GuruModel({
    required this.nip,
    required this.nama,
    this.jabatan,
    this.telp,
    this.wa,
  });

  String get initials {
    if (nama.trim().isEmpty) return '?';
    final parts = nama.trim().split(RegExp(r'\s+'));
    if (parts.length >= 2) {
      return '${parts[0][0]}${parts[1][0]}'.toUpperCase();
    }
    return parts[0][0].toUpperCase();
  }

  String get displayJabatan {
    if (jabatan != null && jabatan!.trim().isNotEmpty) {
      return jabatan!.trim();
    }
    return 'Guru Pengajar';
  }

  factory GuruModel.fromJson(Map<String, dynamic> json) {
    return GuruModel(
      nip: json['nip']?.toString() ?? '',
      nama: json['nama']?.toString() ?? '',
      jabatan: json['jabatan']?.toString(),
      telp: json['telp']?.toString(),
      wa: json['wa']?.toString(),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'nip': nip,
      'nama': nama,
      'jabatan': jabatan,
      'telp': telp,
      'wa': wa,
    };
  }
}

