class UserIdentityModel {
  final String id;
  final String? name;
  final String? detail;
  final String? role;
  final bool isStudent;
  final bool isTeacher;
  final bool isAdmin;
  final bool isGuardian;

  const UserIdentityModel({
    required this.id,
    this.name,
    this.detail,
    this.role,
    this.isStudent = false,
    this.isTeacher = false,
    this.isAdmin = false,
    this.isGuardian = false,
  });

  factory UserIdentityModel.fromJson(Map<String, dynamic> json) {
    return UserIdentityModel(
      id: json['id']?.toString() ?? '',
      name: json['name']?.toString(),
      detail: json['detail']?.toString(),
      role: json['role']?.toString(),
      isStudent: json['isStudent'] == true,
      isTeacher: json['isTeacher'] == true,
      isAdmin: json['isAdmin'] == true,
      isGuardian: json['isGuardian'] == true,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      'detail': detail,
      'role': role,
      'isStudent': isStudent,
      'isTeacher': isTeacher,
      'isAdmin': isAdmin,
      'isGuardian': isGuardian,
    };
  }
}

class UserProfileModel {
  final String? idUser;
  final String? email;
  final bool isAdmin;
  final bool isTeacher;
  final bool isStudent;
  final bool isGuardian;
  final List<String> roles;
  final List<UserIdentityModel> identities;

  const UserProfileModel({
    this.idUser,
    this.email,
    this.isAdmin = false,
    this.isTeacher = false,
    this.isStudent = false,
    this.isGuardian = false,
    this.roles = const [],
    this.identities = const [],
  });

  bool get isRoleMurid => isStudent || roles.contains('MURID');

  UserIdentityModel? get studentIdentity {
    for (final id in identities) {
      if (id.isStudent || id.role == 'MURID') return id;
    }
    return identities.isNotEmpty ? identities.first : null;
  }

  String get displayName {
    final s = studentIdentity;
    if (s?.name != null && s!.name!.trim().isNotEmpty) {
      return s.name!.trim();
    }
    if (email != null && email!.contains('@')) {
      return email!.split('@').first;
    }
    return 'Siswa';
  }

  String get displayDetail {
    final s = studentIdentity;
    if (s?.detail != null && s!.detail!.trim().isNotEmpty) {
      return s.detail!.trim();
    }
    return 'Murid Sekolah';
  }

  String? get nis => studentIdentity?.id;

  factory UserProfileModel.fromJson(Map<String, dynamic> json) {
    final rawRoles = json['roles'];
    final List<String> rolesList = [];
    if (rawRoles is List) {
      for (final r in rawRoles) {
        if (r != null) rolesList.add(r.toString());
      }
    }

    final rawIdentities = json['identities'];
    final List<UserIdentityModel> identityList = [];
    if (rawIdentities is List) {
      for (final item in rawIdentities) {
        if (item is Map<String, dynamic>) {
          identityList.add(UserIdentityModel.fromJson(item));
        }
      }
    }

    return UserProfileModel(
      idUser: json['idUser']?.toString(),
      email: json['email']?.toString(),
      isAdmin: json['isAdmin'] == true,
      isTeacher: json['isTeacher'] == true,
      isStudent: json['isStudent'] == true,
      isGuardian: json['isGuardian'] == true,
      roles: rolesList,
      identities: identityList,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'idUser': idUser,
      'email': email,
      'isAdmin': isAdmin,
      'isTeacher': isTeacher,
      'isStudent': isStudent,
      'isGuardian': isGuardian,
      'roles': roles,
      'identities': identities.map((e) => e.toJson()).toList(),
    };
  }
}

