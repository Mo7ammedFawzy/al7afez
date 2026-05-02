class Student {
  final int id;
  final String name;

  Student({required this.id, required this.name});

  factory Student.fromJson(Map<String, dynamic> json) =>
      Student(id: json['id'] as int, name: (json['name'] ?? '') as String);
}
