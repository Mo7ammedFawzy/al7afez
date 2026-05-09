import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import '../l10n/tr.dart';
import '../models/mistake_line.dart';
import '../models/mistake_type.dart';
import '../models/recitation_form_data.dart';
import '../services/api_service.dart';
import '../widgets/active_chip.dart';
import '../widgets/inactive_chip.dart';
import 'login_screen.dart';

class RecitationMistakesScreen extends StatefulWidget {
  final RecitationFormData formData;
  final int? editId;

  const RecitationMistakesScreen({
    super.key,
    required this.formData,
    this.editId,
  });

  @override
  State<RecitationMistakesScreen> createState() => _RecitationMistakesScreenState();
}

class _RecitationMistakesScreenState extends State<RecitationMistakesScreen> {
  List<MistakeType> _mistakeTypes = [];
  final List<MistakeLine> _mistakes = [];

  bool _loadingData = true;
  bool _submitting = false;
  String _error = '';

  @override
  void initState() {
    super.initState();
    _loadMistakeTypes();
  }

  @override
  void dispose() {
    super.dispose();
  }

  // ── Data ─────────────────────────────────────────────────────────────────

  Future<void> _loadMistakeTypes() async {
    try {
      final data = await ApiService.get('/mistake-types', params: {'page': '0', 'size': '100'});
      setState(() {
        _mistakeTypes = ((data['content'] ?? data) as List)
            .map((e) => MistakeType.fromJson(e as Map<String, dynamic>))
            .toList();
        _loadingData = false;
      });
    } on ApiException catch (e) {
      if (e.isUnauthorized) { _forceLogout(); return; }
      if (mounted) {
        setState(() {
          _error = e.message.isNotEmpty
              ? e.message
              : Tr.translate('requestError', {'statusCode': e.statusCode.toString()});
          _loadingData = false;
        });
      }
    }
  }

  void _forceLogout() async {
    await ApiService.clearToken();
    if (mounted) {
      Navigator.pushReplacement(context, MaterialPageRoute(builder: (_) => const LoginScreen()));
    }
  }

  // ── Mutations ─────────────────────────────────────────────────────────────

  void _tap(int typeId) {
    HapticFeedback.lightImpact();
    setState(() {
      final idx = _indexOf(typeId);
      if (idx >= 0) {
        _mistakes[idx].count++;
      } else {
        _mistakes.add(MistakeLine()..mistakeTypeId = typeId);
      }
    });
  }

  void _increment(int typeId) {
    HapticFeedback.lightImpact();
    setState(() {
      final idx = _indexOf(typeId);
      if (idx >= 0) _mistakes[idx].count++;
    });
  }

  void _decrement(int typeId) {
    HapticFeedback.lightImpact();
    final idx = _indexOf(typeId);
    if (idx < 0) return;
    if (_mistakes[idx].count > 1) {
      setState(() => _mistakes[idx].count--);
      return;
    }
    final removed = _mistakes[idx];
    setState(() {
      _mistakes.removeAt(idx);
    });
    final name = _typeById(typeId)?.name ?? '';
    ScaffoldMessenger.of(context).clearSnackBars();
    ScaffoldMessenger.of(context).showSnackBar(SnackBar(
      content: Text(Tr.translate('deletedMistake', {'name': name})),
      action: SnackBarAction(
        label: Tr.translate('undo'),
        onPressed: () => setState(() => _mistakes.add(removed)),
      ),
      duration: const Duration(seconds: 3),
    ));
  }

  // ── Submit ────────────────────────────────────────────────────────────────

  Future<void> _submit() async {
    setState(() { _submitting = true; _error = ''; });
    try {
      final payload = {
        ...widget.formData.toJson(),
        'mistakes': _mistakes
            .where((m) => m.mistakeTypeId != null && m.count > 0)
            .map((m) => m.toJson())
            .toList(),
      };
      if (widget.editId != null) {
        await ApiService.put('/recitations/${widget.editId}', payload);
      } else {
        await ApiService.post('/recitations', payload);
      }
      if (mounted) Navigator.pop(context, true);
    } on ApiException catch (e) {
      if (e.isUnauthorized) { _forceLogout(); return; }
      if (mounted) {
        setState(() => _error = e.message.isNotEmpty
            ? e.message
            : Tr.translate('requestError', {'statusCode': e.statusCode.toString()}));
      }
    } finally {
      if (mounted) setState(() => _submitting = false);
    }
  }

  // ── Helpers ───────────────────────────────────────────────────────────────

  int _indexOf(int typeId) => _mistakes.indexWhere((m) => m.mistakeTypeId == typeId);

  MistakeType? _typeById(int id) {
    try { return _mistakeTypes.firstWhere((t) => t.id == id); }
    catch (_) { return null; }
  }

  int get _totalCount => _mistakes.fold(0, (s, m) => s + m.count);

  List<MapEntry<MistakeType?, List<MistakeType>>> _groupedLeaves() {
    final parentIds = _mistakeTypes
        .where((t) => t.parentId != null)
        .map((t) => t.parentId!)
        .toSet();
    final parentById = {
      for (final t in _mistakeTypes.where((t) => parentIds.contains(t.id))) t.id: t,
    };
    final grouped = <MistakeType?, List<MistakeType>>{};
    for (final t in _mistakeTypes.where((t) => !parentIds.contains(t.id))) {
      grouped
          .putIfAbsent(t.parentId != null ? parentById[t.parentId] : null, () => [])
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
    final cs = Theme.of(context).colorScheme;
    final total = _totalCount;

    return Scaffold(
      appBar: AppBar(
        title: Text(Tr.translate('mistakesLog')),
        centerTitle: true,
        actions: [
          if (total > 0)
            Padding(
              padding: const EdgeInsetsDirectional.only(end: 12),
              child: Badge.count(
                count: total,
                backgroundColor: cs.primary,
                textColor: cs.onPrimary,
                textStyle: const TextStyle(fontWeight: FontWeight.bold, fontSize: 12),
                child: const SizedBox.shrink(),
              ),
            ),
        ],
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
                  child: SingleChildScrollView(
                    padding: const EdgeInsets.fromLTRB(20, 24, 20, 16),
                    child: _buildPalette(cs),
                  ),
                ),
                _submitBar(cs),
              ],
            ),
    );
  }

  Widget _buildPalette(ColorScheme cs) {
    final groups = _groupedLeaves();
    final activeById = {for (final m in _mistakes) m.mistakeTypeId: m};

    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: [
        for (final group in groups) ...[
          if (group.key != null) _sectionDivider(group.key!.name, cs),
          const SizedBox(height: 14),
          Wrap(
            spacing: 10,
            runSpacing: 10,
            children: [
              for (final type in group.value)
                if (activeById[type.id] case final mistake?)
                  ActiveChip(
                    type: type,
                    mistake: mistake,
                    onIncrement: () => _increment(type.id),
                    onDecrement: () => _decrement(type.id),
                  )
                else
                  InactiveChip(
                    type: type,
                    onTap: () => _tap(type.id),
                  ),
            ],
          ),
          const SizedBox(height: 28),
        ],
      ],
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
    return SafeArea(
      child: Padding(
        padding: const EdgeInsets.fromLTRB(20, 8, 20, 20),
        child: FilledButton(
          onPressed: _submitting ? null : _submit,
          style: FilledButton.styleFrom(minimumSize: const Size.fromHeight(52)),
          child: _submitting
              ? const SizedBox(
                  height: 20, width: 20,
                  child: CircularProgressIndicator(strokeWidth: 2, color: Colors.white),
                )
              : Text(
                  widget.editId != null ? Tr.translate('saveButton') : Tr.translate('createButton'),
                  style: const TextStyle(fontSize: 16),
                ),
        ),
      ),
    );
  }
}

