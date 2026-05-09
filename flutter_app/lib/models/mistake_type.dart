class MistakeType {
  final int id;
  final String name;
  final int? parentId;
  final String? parentName;

  const MistakeType({
    required this.id,
    required this.name,
    this.parentId,
    this.parentName,
  });

  factory MistakeType.fromJson(Map<String, dynamic> json) {
    final parent = json['parent'] as Map<String, dynamic>?;
    return MistakeType(
      id: json['id'] as int,
      name: (json['name'] as String?) ?? '',
      parentId: parent?['id'] as int?,
      parentName: parent?['name'] as String?,
    );
  }
}
