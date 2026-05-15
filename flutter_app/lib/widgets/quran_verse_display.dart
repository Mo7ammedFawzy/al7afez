import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import '../data/surahs.dart';
import '../l10n/tr.dart';
import '../services/quran_service.dart';

class QuranVerseDisplay extends StatelessWidget {
  final List<AyahData> ayahs;
  final bool loading;
  final String error;
  final Set<String> markedWordKeys;

  /// Called when a word is tapped. Null = display-only mode.
  final void Function(int surahNumber, int ayaNumber, int wordIndex)? onWordTap;

  const QuranVerseDisplay({
    super.key,
    required this.ayahs,
    this.loading = false,
    this.error = '',
    this.markedWordKeys = const {},
    this.onWordTap,
  });

  static String wordKey(int surah, int aya, int word) => '$surah-$aya-$word';

  // Bismillah is the first 4 words of aya 1 for every surah except At-Tawbah (9).
  static bool _isBismillahAya(AyahData ayah) =>
      ayah.ayahNumber == 1 &&
          ayah.surahNumber != 9 &&
          ayah.text.trim().startsWith('بِسْمِ');

  @override
  Widget build(BuildContext context) {
    final cs = Theme
        .of(context)
        .colorScheme;

    if (loading) return const Center(child: CircularProgressIndicator());
    if (error.isNotEmpty) {
      return Center(
        child: Padding(
          padding: const EdgeInsets.all(24),
          child: Text(error, style: TextStyle(color: cs.error)),
        ),
      );
    }
    if (ayahs.isEmpty) {
      return Center(
        child: Text(
            Tr.translate('noAyahs'), style: TextStyle(color: cs.onSurfaceVariant)),
      );
    }

    return SingleChildScrollView(
      padding: const EdgeInsets.fromLTRB(16, 16, 16, 16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          if (onWordTap != null)
            Padding(
              padding: const EdgeInsets.only(bottom: 12),
              child: Text(
                Tr.translate('tapWordHint'),
                textAlign: TextAlign.center,
                style: TextStyle(fontSize: 11, color: cs.onSurfaceVariant),
              ),
            ),
          ..._buildSegments(cs),
        ],
      ),
    );
  }

  List<Widget> _buildSegments(ColorScheme cs) {
    final segments = <Widget>[];
    final wrapChildren = <Widget>[];
    int currentSurah = -1;

    void flushWrap() {
      if (wrapChildren.isEmpty) return;
      segments.add(Padding(
        padding: const EdgeInsets.only(bottom: 4),
        child: Wrap(
          textDirection: TextDirection.rtl,
          alignment: WrapAlignment.spaceBetween,
          spacing: 0,
          runSpacing: 6,
          crossAxisAlignment: WrapCrossAlignment.center,
          children: List.from(wrapChildren),
        ),
      ));
      wrapChildren.clear();
    }

    for (final ayah in ayahs) {
      // ── Surah transition ───────────────────────────────────────────────────
      if (ayah.surahNumber != currentSurah) {
        flushWrap();
        segments.add(_SurahHeader(surahNumber: ayah.surahNumber, cs: cs));
        currentSurah = ayah.surahNumber;
      }

      final words = ayah.text.trim().split(' ');

      // ── Bismillah aya ──────────────────────────────────────────────────────
      if (_isBismillahAya(ayah)) {
        flushWrap();
        final bismillahCount = words.length >= 4 ? 4 : words.length;

        // Centered, tappable Bismillah line
        segments.add(Padding(
          padding: const EdgeInsets.symmetric(vertical: 8),
          child: Wrap(
            alignment: WrapAlignment.center,
            textDirection: TextDirection.rtl,
            spacing: 0,
            runSpacing: 4,
            crossAxisAlignment: WrapCrossAlignment.center,
            children: [
              for (int w = 0; w < bismillahCount; w++)
                _WordChip(
                  word: words[w],
                  isMarked: markedWordKeys.contains(
                      wordKey(ayah.surahNumber, ayah.ayahNumber, w)),
                  onTap: onWordTap == null
                      ? null
                      : () {
                    HapticFeedback.lightImpact();
                    onWordTap!(ayah.surahNumber, ayah.ayahNumber, w);
                  },
                  cs: cs,
                ),
              // Aya marker goes on the Bismillah line only when there are no
              // remaining words (e.g. Al-Fatiha aya 1 is purely the Bismillah).
              if (words.length <= bismillahCount)
                _AyahMarker(number: ayah.ayahNumber, cs: cs),
            ],
          ),
        ));

        // Remaining words after the Bismillah (e.g. ٱلٓمٓ in Al-Baqara)
        for (int w = bismillahCount; w < words.length; w++) {
          wrapChildren.add(_WordChip(
            word: words[w],
            isMarked: markedWordKeys.contains(
                wordKey(ayah.surahNumber, ayah.ayahNumber, w)),
            onTap: onWordTap == null
                ? null
                : () {
              HapticFeedback.lightImpact();
              onWordTap!(ayah.surahNumber, ayah.ayahNumber, w);
            },
            cs: cs,
          ));
        }
        if (words.length > bismillahCount) {
          wrapChildren.add(_AyahMarker(number: ayah.ayahNumber, cs: cs));
        }
        continue;
      }

      // ── Regular aya ───────────────────────────────────────────────────────
      for (int w = 0; w < words.length; w++) {
        wrapChildren.add(_WordChip(
          word: words[w],
          isMarked: markedWordKeys.contains(
              wordKey(ayah.surahNumber, ayah.ayahNumber, w)),
          onTap: onWordTap == null
              ? null
              : () {
            HapticFeedback.lightImpact();
            onWordTap!(ayah.surahNumber, ayah.ayahNumber, w);
          },
          cs: cs,
        ));
      }
      wrapChildren.add(_AyahMarker(number: ayah.ayahNumber, cs: cs));
    }

    flushWrap();
    return segments;
  }
}

// ── Sub-widgets ───────────────────────────────────────────────────────────────

class _SurahHeader extends StatelessWidget {
  final int surahNumber;
  final ColorScheme cs;

  const _SurahHeader({required this.surahNumber, required this.cs});

  @override
  Widget build(BuildContext context) {
    final name = surahByNumber(surahNumber)?.name ?? '$surahNumber';
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 12),
      child: Container(
        decoration: BoxDecoration(
          color: cs.primaryContainer.withOpacity(0.25),
          borderRadius: BorderRadius.circular(8),
          border: Border.all(color: cs.primary.withOpacity(0.35)),
        ),
        padding: const EdgeInsets.symmetric(vertical: 10, horizontal: 16),
        child: Text(
          'سورة $name',
          textAlign: TextAlign.center,
          style: TextStyle(
            fontFamily: 'AmiriQuranColored',
            fontSize: 20,
            color: cs.primary,
            fontWeight: FontWeight.bold,
          ),
        ),
      ),
    );
  }
}

class _WordChip extends StatelessWidget {
  final String word;
  final bool isMarked;
  final VoidCallback? onTap;
  final ColorScheme cs;

  const _WordChip({
    required this.word,
    required this.isMarked,
    required this.onTap,
    required this.cs,
  });

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: AnimatedContainer(
        duration: const Duration(milliseconds: 150),
        padding: const EdgeInsets.symmetric(horizontal: 1, vertical: 1),
        decoration: BoxDecoration(
          color: isMarked ? cs.errorContainer : Colors.transparent,
          borderRadius: BorderRadius.circular(4),
          border: isMarked
              ? Border.all(color: cs.error.withOpacity(0.4))
              : null,
        ),
        child: Text(
          '$word ',
          style: TextStyle(
            fontFamily: 'AmiriQuranColored',
            fontSize: 26,
            height: 2.2,
            color: isMarked ? cs.onErrorContainer : cs.onSurface,
          ),
          textDirection: TextDirection.rtl,
        ),
      ),
    );
  }
}

class _AyahMarker extends StatelessWidget {
  final int number;
  final ColorScheme cs;

  const _AyahMarker({required this.number, required this.cs});

  @override
  Widget build(BuildContext context) {
    return Container(
      width: 28,
      height: 28,
      margin: const EdgeInsets.symmetric(horizontal: 4),
      decoration: BoxDecoration(
        shape: BoxShape.circle,
        border: Border.all(color: cs.primary.withOpacity(0.6)),
      ),
      child: Center(
        child: Text(
          '$number',
          style: TextStyle(
            fontSize: 9,
            color: cs.primary,
            fontWeight: FontWeight.w700,
          ),
        ),
      ),
    );
  }
}
