class UserInfo {
  final int id;
  final String name;
  final String username;

  const UserInfo({
    required this.id,
    required this.name,
    required this.username,
  });

  factory UserInfo.fromJson(Map<String, dynamic> json) => UserInfo(
        id: json['userId'] as int,
        name: (json['name'] as String?) ?? '',
        username: (json['username'] as String?) ?? '',
      );

  Map<String, dynamic> toJson() => {
        'userId': id,
        'name': name,
        'username': username,
      };
}
