import 'dart:convert';
import 'package:http/http.dart' as http;

class AyahData {
  final int surahNumber;
  final int ayahNumber;
  final String text;

  const AyahData({
    required this.surahNumber,
    required this.ayahNumber,
    required this.text,
  });
}

class QuranService {
  static const _base = 'https://api.alquran.cloud/v1';

  static Future<List<AyahData>> fetchRange({
    required int fromSurah,
    required int fromAya,
    required int toSurah,
    required int toAya,
  }) async {
    final result = <AyahData>[];
    for (int s = fromSurah; s <= toSurah; s++) {
      final ayahs = await _fetchSurah(s);
      final start = s == fromSurah ? fromAya : 1;
      final end = s == toSurah ? toAya : (ayahs.isEmpty ? 0 : ayahs.last
          .ayahNumber);
      result.addAll(
          ayahs.where((a) => a.ayahNumber >= start && a.ayahNumber <= end));
    }
    return result;
  }

  static Future<List<AyahData>> _fetchSurah(int surahNumber) async {
    final res = await http.get(
        Uri.parse('$_base/surah/$surahNumber/quran-uthmani'));
    if (res.statusCode != 200) throw Exception(
        'Quran API error ${res.statusCode}');
    final json = jsonDecode(utf8.decode(res.bodyBytes)) as Map<String, dynamic>;
    final ayahs = (json['data']['ayahs'] as List).cast<Map<String, dynamic>>();
    return ayahs
        .map((a) =>
        AyahData(
          surahNumber: surahNumber,
          ayahNumber: a['numberInSurah'] as int,
          text: a['text'] as String,
        ))
        .toList();
  }
}
