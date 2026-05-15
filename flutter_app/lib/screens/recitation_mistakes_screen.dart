import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import '../l10n/tr.dart';
import '../models/mistake_line.dart';
import '../models/mistake_type.dart';
import '../models/recitation_form_data.dart';
import '../services/api_service.dart';
import '../services/quran_service.dart';
import '../widgets/inactive_chip.dart';
import '../widgets/quran_verse_display.dart';
import 'login_screen.dart';

class _MarkedWord {
  final String key;
  int? mistakeTypeId;
  _MarkedWord(this.key, [this.mistakeTypeId]);
}

class RecitationMistakesScreen extends StatefulWidget {
  final RecitationFormData formData;
  final int? editId;

  const RecitationMistakesScreen({
    super.key,
    required this.formData,
    this.editId,
  });

  @override
  State<RecitationMistakesScreen> createState() =>
      _RecitationMistakesScreenState();
}

class _RecitationMistakesScreenState extends State<RecitationMistakesScreen> {
  List<MistakeType> _mistakeTypes = [];
  final List<MistakeLine> _mistakes = [];

  List<AyahData> _ayahs = [];
  bool _quranLoading = false;
  String _quranError = '';

  final List<_MarkedWord> _markedWords = [];
  late final TextEditingController _gradeCtrl;

  bool _loadingData = true;
  bool _submitting = false;
  String _error = '';

  @override
  void initState() {
    super.initState();
    _gradeCtrl = TextEditingController(
      text: widget.initialGrade?.toString() ?? '',
    );
    _loadMistakeTypes();
    _loadQuranText();
    _applyInitialMistakes();
  }

  // ── Data ─────────────────────────────────────────────────────────────────
  Future<void> _loadMistakeTypes() async {
    try {
      final data = await ApiService.get(
          '/mistake-types', params: {'page': '0', 'size': '100'});
      setState(() {
        _mistakeTypes = ((data['content'] ?? data) as List)
            .map((e) => MistakeType.fromJson(e as Map<String, dynamic>))
            .toList();
        _loadingData = false;
      });
    } on ApiException catch (e) {
      if (e.isUnauthorized) {
        _forceLogout();
        return;
      }
      if (mounted) {
        setState(() {
          _error = e.message.isNotEmpty
              ? e.message
              : Tr.translate(
              'requestError', {'statusCode': e.statusCode.toString()});
          _loadingData = false;
        });
      }
    }
  }

  Future<void> _loadQuranText() async {
    final fd = widget.formData;
    if (fd.fromSurah == null || fd.fromAya == null ||
        fd.toSurah == null || fd.toAya == null) return;
    setState(() {
      _quranLoading = true;
      _quranError = '';
    });
    try {
      final ayahs = await QuranService.fetchRange(
        fromSurah: fd.fromSurah!,
        fromAya: fd.fromAya!,
        toSurah: fd.toSurah!,
        toAya: fd.toAya!,
      );
      if (mounted) setState(() {
        _ayahs = ayahs;
        _quranLoading = false;
      });
    } catch (_) {
      if (mounted) setState(() {
        _quranError = 'تعذّر تحميل الآيات';
        _quranLoading = false;
      });
    }
  }

  void _applyInitialMistakes() {
    for (final m in widget.initialMistakes) {
      final surah = m['surahNumber'] as int?;
      final aya = m['ayaNumber'] as int?;
      final word = m['wordIndex'] as int?;
      final typeId = (m['mistakeType'] as Map<String, dynamic>?)?['id'] as int?;
      _mistakes.add(MistakeLine(
        mistakeTypeId: typeId,
        surahNumber: surah,
        ayaNumber: aya,
        wordIndex: word,
      ));
      if (surah != null && aya != null && word != null) {
        final key = QuranVerseDisplay.wordKey(surah, aya, word);
        _markedWords.add(_MarkedWord(key, typeId));
      }
    }
  }

  @override
  void dispose() {
    _gradeCtrl.dispose();
    super.dispose();
  }

  void _forceLogout() async {
    await ApiService.clearToken();
    if (mounted) {
      Navigator.pushReplacement(
          context, MaterialPageRoute(builder: (_) => const LoginScreen()));
    }
  }

  // ── Word tap (positional mistakes) ───────────────────────────────────────

  void _onWordTap(int surahNumber, int ayaNumber, int wordIndex) {
    HapticFeedback.lightImpact();
    final key = QuranVerseDisplay.wordKey(surahNumber, ayaNumber, wordIndex);

    // Toggle off if already marked
    if (_markedWords.any((w) => w.key == key)) {
      setState(() {
        _markedWords.removeWhere((w) => w.key == key);
        _mistakes.removeWhere((m) =>
        m.surahNumber == surahNumber &&
            m.ayaNumber == ayaNumber &&
            m.wordIndex == wordIndex);
      });
      return;
    }

    // Mark immediately — mistake type is optional
    setState(() {
      _markedWords.add(_MarkedWord(key));
      _mistakes.add(MistakeLine(
        surahNumber: surahNumber,
        ayaNumber: ayaNumber,
        wordIndex: wordIndex,
      ));
    });

    // Show type picker only when types are available
    if (_mistakeTypes.isEmpty) return;

    final cs = Theme
        .of(context)
        .colorScheme;
    final groups = _groupedLeaves();
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      builder: (_) =>
          SafeArea(
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                Padding(
                  padding: const EdgeInsets.fromLTRB(16, 16, 16, 12),
                  child: Text(
                    'اختر نوع الخطأ',
                    style: TextStyle(fontSize: 16,
                        fontWeight: FontWeight.w700,
                        color: cs.onSurface),
                  ),
                ),
                Divider(height: 1, color: cs.outlineVariant),
                ConstrainedBox(
                  constraints: BoxConstraints(
                    maxHeight: MediaQuery
                        .of(context)
                        .size
                        .height * 0.5,
                  ),
                  child: SingleChildScrollView(
                    padding: const EdgeInsets.fromLTRB(20, 16, 20, 24),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.stretch,
                      children: [
                        for (final group in groups) ...[
                          if (group.key != null) _sectionDivider(
                              group.key!.name, cs),
                          const SizedBox(height: 10),
                          Wrap(
                            spacing: 10,
                            runSpacing: 10,
                            children: [
                              for (final type in group.value)
                                InactiveChip(
                                  type: type,
                                  onTap: () =>
                                      _selectMistakeForWord(
                                          surahNumber, ayaNumber, wordIndex,
                                          type.id),
                                ),
                            ],
                          ),
                          const SizedBox(height: 16),
                        ],
                      ],
                    ),
                  ),
                ),
              ],
            ),
          ),
    );
  }

  void _selectMistakeForWord(int surahNumber, int ayaNumber, int wordIndex,
      int typeId) {
    Navigator.pop(context);
    HapticFeedback.mediumImpact();
    final key = QuranVerseDisplay.wordKey(surahNumber, ayaNumber, wordIndex);
    setState(() {
      _markedWords.firstWhere((w) => w.key == key).mistakeTypeId = typeId;
      // Update the mistake that was already added on tap
      final idx = _mistakes.indexWhere((m) =>
      m.surahNumber == surahNumber &&
          m.ayaNumber == ayaNumber &&
          m.wordIndex == wordIndex);
      if (idx >= 0) _mistakes[idx].mistakeTypeId = typeId;
    });
  }

  // ── Submit ────────────────────────────────────────────────────────────────

  Future<void> _submit() async {
    setState(() {
      _submitting = true;
      _error = '';
    });
    try {
      final payload = {
        ...widget.formData.toJson(),
        'grade': _gradeCtrl.text.isNotEmpty
            ? int.tryParse(_gradeCtrl.text)
            : null,
        'mistakes': _mistakes.map((m) => m.toJson()).toList(),
      };
      if (widget.editId != null) {
        await ApiService.put('/recitations/${widget.editId}', payload);
      } else {
        await ApiService.post('/recitations', payload);
      }
      if (mounted) Navigator.pop(context, true);
    } on ApiException catch (e) {
      if (e.isUnauthorized) {
        _forceLogout();
        return;
      }
      if (mounted) {
        setState(() =>
        _error = e.message.isNotEmpty
            ? e.message
            : Tr.translate(
            'requestError', {'statusCode': e.statusCode.toString()}));
      }
    } finally {
      if (mounted) setState(() => _submitting = false);
    }
  }

  // ── Helpers ───────────────────────────────────────────────────────────────

  int get _totalCount => _mistakes.length;

  List<MapEntry<MistakeType?, List<MistakeType>>> _groupedLeaves() {
    final parentIds = _mistakeTypes
        .where((t) => t.parentId != null)
        .map((t) => t.parentId!)
        .toSet();
    final parentById = {
      for (final t in _mistakeTypes.where((t) => parentIds.contains(t.id))) t
          .id: t,
    };
    final grouped = <MistakeType?, List<MistakeType>>{};
    for (final t in _mistakeTypes.where((t) => !parentIds.contains(t.id))) {
      grouped
          .putIfAbsent(
          t.parentId != null ? parentById[t.parentId] : null, () => [])
          .add(t);
    }
    return grouped.entries.toList()
      ..sort((a, b) {
        if (a.key == null) return 1;
        if (b.key == null) return -1;
        return 0;
      });
  }

  // ── Build ─────────────────────────────────────────────────────────────────

  @override
  Widget build(BuildContext context) {
    final cs = Theme
        .of(context)
        .colorScheme;

    return Scaffold(
      appBar: AppBar(
        title: Text(Tr.translate('mistakesLog')),
        centerTitle: true,
      ),
      body: _loadingData
          ? const Center(child: CircularProgressIndicator())
          : Column(
        children: [
          if (_error.isNotEmpty)
            Container(
              width: double.infinity,
              padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 10),
              color: Colors.red.shade50,
              child: Text(_error, style: TextStyle(color: Colors.red.shade800)),
            ),
          Expanded(
            child: QuranVerseDisplay(
              ayahs: _ayahs,
              loading: _quranLoading,
              error: _quranError,
              markedWordKeys: _markedWords.map((w) => w.key).toSet(),
              onWordTap: _ayahs.isNotEmpty ? _onWordTap : null,
            ),
          ),
          _submitBar(cs),
        ],
      ),
    );
  }

  Widget _sectionDivider(String label, ColorScheme cs) {
    return Row(
      children: [
        Expanded(child: Divider(color: cs.outlineVariant)),
        Padding(
          padding: const EdgeInsets.symmetric(horizontal: 12),
          child: Text(
            label,
            style: TextStyle(
              fontSize: 11,
              fontWeight: FontWeight.w700,
              color: cs.onSurfaceVariant,
              letterSpacing: 0.8,
            ),
          ),
        ),
        Expanded(child: Divider(color: cs.outlineVariant)),
      ],
    );
  }

  Widget _submitBar(ColorScheme cs) {
    final total = _totalCount;
    return SafeArea(
      child: Padding(
        padding: const EdgeInsets.fromLTRB(16, 8, 16, 16),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
              decoration: BoxDecoration(
                color: cs.surfaceContainerLow,
                borderRadius: BorderRadius.circular(12),
              ),
              child: Row(
                children: [
                  // Mistakes badge
                  Container(
                    padding: const EdgeInsets.symmetric(
                        horizontal: 14, vertical: 8),
                    decoration: BoxDecoration(
                      color: total > 0 ? cs.errorContainer : cs
                          .surfaceContainerHigh,
                      borderRadius: BorderRadius.circular(20),
                    ),
                    child: Row(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        Icon(Icons.error_outline_rounded, size: 18,
                            color: total > 0 ? cs.onErrorContainer : cs
                                .onSurfaceVariant),
                        const SizedBox(width: 6),
                        Text(
                          '$total',
                          style: TextStyle(
                            fontSize: 18,
                            fontWeight: FontWeight.w800,
                            color: total > 0 ? cs.onErrorContainer : cs
                                .onSurfaceVariant,
                          ),
                        ),
                        const SizedBox(width: 4),
                        Text(
                          Tr.translate('mistakes'),
                          style: TextStyle(
                            fontSize: 13,
                            color: total > 0 ? cs.onErrorContainer : cs
                                .onSurfaceVariant,
                          ),
                        ),
                      ],
                    ),
                  ),
                  const Spacer(),
                  // Grade input
                  SizedBox(
                    width: 100,
                    child: TextField(
                      controller: _gradeCtrl,
                      keyboardType: TextInputType.number,
                      inputFormatters: [FilteringTextInputFormatter.digitsOnly],
                      textAlign: TextAlign.center,
                      style: TextStyle(
                        fontSize: 24,
                        fontWeight: FontWeight.w800,
                        color: cs.primary,
                      ),
                      decoration: InputDecoration(
                        labelText: Tr.translate('grade'),
                        labelStyle: TextStyle(
                            fontSize: 12, color: cs.onSurfaceVariant),
                        suffix: Text(
                          '/10',
                          style: TextStyle(
                            fontSize: 12,
                            color: cs.onSurfaceVariant,
                            fontWeight: FontWeight.w500,
                          ),
                        ),
                        enabledBorder: OutlineInputBorder(
                          borderRadius: BorderRadius.circular(12),
                          borderSide: BorderSide(color: cs.outlineVariant),
                        ),
                        focusedBorder: OutlineInputBorder(
                          borderRadius: BorderRadius.circular(12),
                          borderSide: BorderSide(color: cs.primary, width: 2),
                        ),
                        contentPadding: const EdgeInsets.symmetric(
                            horizontal: 12, vertical: 12),
                      ),
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 10),
            FilledButton(
              onPressed: _submitting ? null : _submit,
              style: FilledButton.styleFrom(
                  minimumSize: const Size.fromHeight(52)),
              child: _submitting
                  ? const SizedBox(height: 20, width: 20,
                  child: CircularProgressIndicator(
                      strokeWidth: 2, color: Colors.white))
                  : Text(
                widget.editId != null ? Tr.translate('saveButton') : Tr
                    .translate('createButton'),
                style: const TextStyle(fontSize: 16),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

