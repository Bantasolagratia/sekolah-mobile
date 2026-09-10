class SessionModel {
  final String accessToken;
  final String? tokenType;
  final int? expiresIn;
  final String? refreshToken;
  final String? userId;
  final String? email;

  const SessionModel({
    required this.accessToken,
    this.tokenType,
    this.expiresIn,
    this.refreshToken,
    this.userId,
    this.email,
  });

  factory SessionModel.fromJson(Map<String, dynamic> json) {
    String? uid;
    String? mail;
    if (json['user'] is Map<String, dynamic>) {
      final userMap = json['user'] as Map<String, dynamic>;
      uid = userMap['id']?.toString();
      mail = userMap['email']?.toString();
    }

    return SessionModel(
      accessToken: json['access_token']?.toString() ?? '',
      tokenType: json['token_type']?.toString(),
      expiresIn: json['expires_in'] is int ? json['expires_in'] as int : null,
      refreshToken: json['refresh_token']?.toString(),
      userId: uid,
      email: mail,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'access_token': accessToken,
      'token_type': tokenType,
      'expires_in': expiresIn,
      'refresh_token': refreshToken,
      'user': {
        'id': userId,
        'email': email,
      },
    };
  }
}

