class MistakeType {
  final int id;
  final String name;

  MistakeType({required this.id, required this.name});

  factory MistakeType.fromJson(Map<String, dynamic> json) =>
      MistakeType(id: json['id'] as int, name: (json['name'] ?? '') as String);
}
