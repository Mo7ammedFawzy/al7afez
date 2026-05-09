class RecitationFormData {
  final String? code;
  final DateTime? recitationDate;
  final int? studentId;
  final int? fromSurah;
  final int? toSurah;
  final int? fromAya;
  final int? toAya;
  final int? numberOfAyat;
  final int? grade;
  final String? notes;

  const RecitationFormData({
    this.code,
    this.recitationDate,
    this.studentId,
    this.fromSurah,
    this.toSurah,
    this.fromAya,
    this.toAya,
    this.numberOfAyat,
    this.grade,
    this.notes,
  });

  Map<String, dynamic> toJson() {
    final d = recitationDate;
    return {
      'code': code,
      'recitationDate': d != null
          ? '${d.year}-${d.month.toString().padLeft(2, '0')}-${d.day.toString().padLeft(2, '0')}'
          : null,
      'studentId': studentId,
      'fromSurah': fromSurah,
      'toSurah': toSurah,
      'fromAya': fromAya,
      'toAya': toAya,
      'numberOfAyat': numberOfAyat,
      'grade': grade,
      'notes': notes,
    };
  }
}
