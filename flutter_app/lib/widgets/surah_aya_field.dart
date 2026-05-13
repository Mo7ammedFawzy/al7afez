import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import '../data/surahs.dart';
import '../l10n/tr.dart';

class SurahAyaField extends StatefulWidget {
  final String surahLabel;
  final String ayaLabel;
  final int? surahValue;
  final int? ayaValue;
  final ValueChanged<int?> onSurahChanged;
  final ValueChanged<int?> onAyaChanged;
  final String? Function(int?)? surahValidator;
  final String? Function(int?)? ayaValidator;

  const SurahAyaField({
    super.key,
    required this.surahLabel,
    required this.ayaLabel,
    required this.onSurahChanged,
    required this.onAyaChanged,
    this.surahValue,
    this.ayaValue,
    this.surahValidator,
    this.ayaValidator,
  });

  @override
  State<SurahAyaField> createState() => _SurahAyaFieldState();
}

class _SurahAyaFieldState extends State<SurahAyaField> {
  final _ayaCtrl = TextEditingController();
  SurahData? _selectedSurah;

  @override
  void initState() {
    super.initState();
    _sync();
  }

  @override
  void didUpdateWidget(SurahAyaField old) {
    super.didUpdateWidget(old);
    if (old.surahValue != widget.surahValue || old.ayaValue != widget.ayaValue) {
      _sync();
    }
  }

  void _sync() {
    _selectedSurah = widget.surahValue != null ? surahByNumber(widget.surahValue!) : null;
    final ayaText = widget.ayaValue?.toString() ?? '';
    if (_ayaCtrl.text != ayaText) _ayaCtrl.text = ayaText;
  }

  @override
  void dispose() {
    _ayaCtrl.dispose();
    super.dispose();
  }

  Future<void> _openPicker() async {
    final picked = await showModalBottomSheet<SurahData>(
      context: context,
      isScrollControlled: true,
      useSafeArea: true,
      builder: (_) => _SurahPickerSheet(selected: _selectedSurah),
    );
    if (picked == null) return;
    setState(() {
      _selectedSurah = picked;
      // Reset aya if it exceeds new surah's max
      final currentAya = int.tryParse(_ayaCtrl.text);
      if (currentAya != null && currentAya > picked.ayaCount) {
        _ayaCtrl.clear();
        widget.onAyaChanged(null);
      }
    });
    widget.onSurahChanged(picked.number);
  }

  @override
  Widget build(BuildContext context) {
    final cs = Theme.of(context).colorScheme;
    final maxAya = _selectedSurah?.ayaCount;

    return Row(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        // Surah picker
        Expanded(
          flex: 3,
          child: FormField<int>(
            initialValue: widget.surahValue,
            validator: (_) => widget.surahValidator?.call(_selectedSurah?.number),
            builder: (state) {
              return GestureDetector(
                onTap: _openPicker,
                child: AbsorbPointer(
                  child: TextFormField(
                    readOnly: true,
                    controller: TextEditingController(
                      text: _selectedSurah != null
                          ? '${_selectedSurah!.number}. ${_selectedSurah!.name}'
                          : '',
                    ),
                    decoration: InputDecoration(
                      labelText: widget.surahLabel,
                      border: const OutlineInputBorder(),
                      errorText: state.hasError ? state.errorText : null,
                      suffixIcon: const Icon(Icons.arrow_drop_down, size: 20),
                      contentPadding: const EdgeInsets.symmetric(horizontal: 12, vertical: 16),
                    ),
                  ),
                ),
              );
            },
          ),
        ),
        const SizedBox(width: 8),
        // Aya input
        Expanded(
          flex: 2,
          child: TextFormField(
            controller: _ayaCtrl,
            keyboardType: TextInputType.number,
            inputFormatters: [FilteringTextInputFormatter.digitsOnly],
            decoration: InputDecoration(
              labelText: widget.ayaLabel,
              border: const OutlineInputBorder(),
              helperText: maxAya != null ? '/ $maxAya' : null,
              helperStyle: TextStyle(color: cs.onSurfaceVariant, fontSize: 12),
            ),
            validator: (v) {
              final val = int.tryParse(v ?? '');
              if (widget.ayaValidator != null) return widget.ayaValidator!(val);
              if (val != null && maxAya != null && val > maxAya) {
                return '≤ $maxAya';
              }
              return null;
            },
            onChanged: (v) => widget.onAyaChanged(int.tryParse(v)),
          ),
        ),
      ],
    );
  }
}

class _SurahPickerSheet extends StatefulWidget {
  final SurahData? selected;
  const _SurahPickerSheet({this.selected});

  @override
  State<_SurahPickerSheet> createState() => _SurahPickerSheetState();
}

class _SurahPickerSheetState extends State<_SurahPickerSheet> {
  final _searchCtrl = TextEditingController();
  List<SurahData> _filtered = kSurahs;

  @override
  void dispose() {
    _searchCtrl.dispose();
    super.dispose();
  }

  void _onSearch(String q) {
    final query = q.trim();
    setState(() {
      _filtered = query.isEmpty
          ? kSurahs
          : kSurahs.where((s) {
              return s.name.contains(query) ||
                  s.number.toString().contains(query);
            }).toList();
    });
  }

  @override
  Widget build(BuildContext context) {
    final cs = Theme.of(context).colorScheme;

    return DraggableScrollableSheet(
      initialChildSize: 0.85,
      minChildSize: 0.5,
      maxChildSize: 0.95,
      expand: false,
      builder: (_, scrollCtrl) => Column(
        children: [
          // Handle
          Container(
            margin: const EdgeInsets.symmetric(vertical: 8),
            width: 40,
            height: 4,
            decoration: BoxDecoration(
              color: cs.outlineVariant,
              borderRadius: BorderRadius.circular(2),
            ),
          ),
          // Title
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 4),
            child: Text(
              Tr.translate('chooseSurah'),
              style: Theme.of(context).textTheme.titleMedium?.copyWith(
                    fontWeight: FontWeight.bold,
                  ),
            ),
          ),
          // Search
          Padding(
            padding: const EdgeInsets.fromLTRB(16, 8, 16, 8),
            child: TextField(
              controller: _searchCtrl,
              autofocus: true,
              decoration: InputDecoration(
                hintText: Tr.translate('search'),
                prefixIcon: const Icon(Icons.search, size: 20),
                border: const OutlineInputBorder(),
                isDense: true,
                contentPadding: const EdgeInsets.symmetric(vertical: 10),
              ),
              onChanged: _onSearch,
            ),
          ),
          const Divider(height: 1),
          // List
          Expanded(
            child: ListView.builder(
              controller: scrollCtrl,
              itemCount: _filtered.length,
              itemBuilder: (_, i) {
                final s = _filtered[i];
                final isSelected = widget.selected?.number == s.number;
                return ListTile(
                  selected: isSelected,
                  selectedColor: cs.primary,
                  selectedTileColor: cs.primary.withOpacity(0.08),
                  leading: CircleAvatar(
                    radius: 16,
                    backgroundColor: isSelected
                        ? cs.primary
                        : cs.surfaceContainerHighest,
                    child: Text(
                      s.number.toString(),
                      style: TextStyle(
                        fontSize: 11,
                        fontWeight: FontWeight.bold,
                        color: isSelected ? cs.onPrimary : cs.onSurfaceVariant,
                      ),
                    ),
                  ),
                  title: Text(s.name),
                  trailing: Text(
                    '${s.ayaCount} ${Tr.translate('aya')}',
                    style: TextStyle(fontSize: 12, color: cs.onSurfaceVariant),
                  ),
                  onTap: () => Navigator.pop(context, s),
                );
              },
            ),
          ),
        ],
      ),
    );
  }
}
