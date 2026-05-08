import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import '../l10n/tr.dart';
import '../models/student.dart';
import '../models/mistake_type.dart';
import '../models/mistake_line.dart';
import '../services/api_service.dart';
import 'login_screen.dart';

class RecitationFormScreen extends StatefulWidget {
  final int? editId;
  const RecitationFormScreen({super.key, this.editId});

  @override
  State<RecitationFormScreen> createState() => _RecitationFormScreenState();
}

class _RecitationFormScreenState extends State<RecitationFormScreen> {
  final _formKey = GlobalKey<FormState>();

  final _codeCtrl = TextEditingController();
  final _notesCtrl = TextEditingController();
  final _fromSurahCtrl = TextEditingController();
  final _toSurahCtrl = TextEditingController();
  final _fromAyaCtrl = TextEditingController();
  final _toAyaCtrl = TextEditingController();
  final _numberOfAyatCtrl = TextEditingController();
  final _gradeCtrl = TextEditingController();

  DateTime? _recitationDate;
  int? _selectedStudentId;

  List<Student> _students = [];
  List<MistakeType> _mistakeTypes = [];
  List<MistakeLine> _mistakes = [];

  bool _loadingData = true;
  bool _submitting = false;
  String _error = '';

  @override
  void initState() {
    super.initState();
    _loadData();
  }

  Future<void> _loadData() async {
    try {
      final results = await Future.wait([
        ApiService.get('/students', params: {'page': '0', 'size': '100'}),
        ApiService.get('/mistake-types', params: {'page': '0', 'size': '100'}),
      ]);

      setState(() {
        _students = ((results[0]['content'] ?? results[0]) as List)
            .map((e) => Student.fromJson(e as Map<String, dynamic>))
            .toList();
        _mistakeTypes = ((results[1]['content'] ?? results[1]) as List)
            .map((e) => MistakeType.fromJson(e as Map<String, dynamic>))
            .toList();
        _loadingData = false;
      });
    } on ApiException catch (e) {
      if (e.isUnauthorized) { _forceLogout(); return; }
      if (mounted) {
        final msg = e.message.isNotEmpty
            ? e.message
            : Tr.translate('requestError', {'statusCode': e.statusCode.toString()});
        setState(() { _error = msg; _loadingData = false; });
      }
    }
  }

  void _forceLogout() async {
    await ApiService.clearToken();
    if (mounted) {
      Navigator.pushReplacement(context, MaterialPageRoute(builder: (_) => const LoginScreen()));
    }
  }

  Future<void> _pickDate() async {
    final picked = await showDatePicker(
      context: context,
      initialDate: _recitationDate ?? DateTime.now(),
      firstDate: DateTime(2000),
      lastDate: DateTime(2100),
      locale: const Locale('ar'),
    );
    if (picked != null) setState(() => _recitationDate = picked);
  }

  void _addMistake() => setState(() => _mistakes.add(MistakeLine()));

  void _removeMistake(int index) => setState(() => _mistakes.removeAt(index));

  Future<void> _submit() async {
    if (!_formKey.currentState!.validate()) return;
    setState(() { _submitting = true; _error = ''; });

    try {
      String? dateStr;
      if (_recitationDate != null) {
        final d = _recitationDate!;
        dateStr = '${d.year}-${d.month.toString().padLeft(2, '0')}-${d.day.toString().padLeft(2, '0')}';
      }

      final payload = {
        'code': _codeCtrl.text.isEmpty ? null : _codeCtrl.text,
        'recitationDate': dateStr,
        'studentId': _selectedStudentId,
        'fromSurah': int.tryParse(_fromSurahCtrl.text),
        'toSurah': int.tryParse(_toSurahCtrl.text),
        'fromAya': int.tryParse(_fromAyaCtrl.text),
        'toAya': int.tryParse(_toAyaCtrl.text),
        'numberOfAyat': int.tryParse(_numberOfAyatCtrl.text),
        'grade': int.tryParse(_gradeCtrl.text),
        'notes': _notesCtrl.text.isEmpty ? null : _notesCtrl.text,
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

      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text(Tr.translate('savedSuccessfully')), backgroundColor: Colors.green),
        );
        _resetForm();
      }
    } on ApiException catch (e) {
      if (e.isUnauthorized) { _forceLogout(); return; }
      if (mounted) {
        final msg = e.message.isNotEmpty
            ? e.message
            : Tr.translate('requestError', {'statusCode': e.statusCode.toString()});
        setState(() => _error = msg);
      }
    } finally {
      if (mounted) setState(() => _submitting = false);
    }
  }

  void _resetForm() {
    _formKey.currentState?.reset();
    _codeCtrl.clear();
    _notesCtrl.clear();
    _fromSurahCtrl.clear();
    _toSurahCtrl.clear();
    _fromAyaCtrl.clear();
    _toAyaCtrl.clear();
    _numberOfAyatCtrl.clear();
    _gradeCtrl.clear();
    setState(() {
      _recitationDate = null;
      _selectedStudentId = null;
      _mistakes = [];
      _error = '';
    });
  }

  @override
  void dispose() {
    _codeCtrl.dispose();
    _notesCtrl.dispose();
    _fromSurahCtrl.dispose();
    _toSurahCtrl.dispose();
    _fromAyaCtrl.dispose();
    _toAyaCtrl.dispose();
    _numberOfAyatCtrl.dispose();
    _gradeCtrl.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.editId != null ? Tr.translate('editRecitation') : Tr.translate('addRecitation')),
        actions: [
          IconButton(
            icon: const Icon(Icons.logout),
            tooltip: Tr.translate('logout'),
            onPressed: () async {
              await ApiService.clearToken();
              if (context.mounted) {
                Navigator.pushReplacement(context, MaterialPageRoute(builder: (_) => const LoginScreen()));
              }
            },
          ),
        ],
      ),
      body: _loadingData
          ? const Center(child: CircularProgressIndicator())
          : SingleChildScrollView(
              padding: const EdgeInsets.all(16),
              child: Form(
                key: _formKey,
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.stretch,
                  children: [
                    if (_error.isNotEmpty)
                      Container(
                        margin: const EdgeInsets.only(bottom: 16),
                        padding: const EdgeInsets.all(12),
                        decoration: BoxDecoration(
                          color: Colors.red.shade50,
                          border: Border.all(color: Colors.red.shade200),
                          borderRadius: BorderRadius.circular(8),
                        ),
                        child: Text(_error, style: TextStyle(color: Colors.red.shade800)),
                      ),

                    _row([
                      _field(
                        label: Tr.translate('code'),
                        controller: _codeCtrl,
                        validator: (v) => (v == null || v.isEmpty) ? Tr.translate('required') : null,
                      ),
                      _datePicker(),
                    ]),
                    const SizedBox(height: 12),

                    _row([
                      _studentDropdown(),
                      _field(label: Tr.translate('numberOfAyat'), controller: _numberOfAyatCtrl, numeric: true),
                    ]),
                    const SizedBox(height: 12),

                    _row([
                      _field(label: Tr.translate('fromSurah'), controller: _fromSurahCtrl, numeric: true),
                      _field(label: Tr.translate('toSurah'), controller: _toSurahCtrl, numeric: true),
                    ]),
                    const SizedBox(height: 12),

                    _row([
                      _field(label: Tr.translate('fromAya'), controller: _fromAyaCtrl, numeric: true),
                      _field(label: Tr.translate('toAya'), controller: _toAyaCtrl, numeric: true),
                    ]),
                    const SizedBox(height: 12),

                    _field(
                      label: Tr.translate('grade'),
                      controller: _gradeCtrl,
                      numeric: true,
                      validator: (v) {
                        if (v == null || v.isEmpty) return null;
                        final n = int.tryParse(v);
                        if (n == null || n < 0 || n > 10) return Tr.translate('gradeMustBeBetween');
                        return null;
                      },
                    ),
                    const SizedBox(height: 12),

                    TextFormField(
                      controller: _notesCtrl,
                      maxLines: 3,
                      decoration: InputDecoration(
                        labelText: Tr.translate('sessionNotes'),
                        border: const OutlineInputBorder(),
                        alignLabelWithHint: true,
                      ),
                    ),
                    const SizedBox(height: 24),

                    _mistakesSection(),
                    const SizedBox(height: 24),

                    Row(
                      children: [
                        Expanded(
                          child: FilledButton(
                            onPressed: _submitting ? null : _submit,
                            style: FilledButton.styleFrom(padding: const EdgeInsets.symmetric(vertical: 14)),
                            child: _submitting
                                ? const SizedBox(height: 20, width: 20, child: CircularProgressIndicator(strokeWidth: 2, color: Colors.white))
                                : Text(
                                    widget.editId != null ? Tr.translate('saveButton') : Tr.translate('createButton'),
                                    style: const TextStyle(fontSize: 16),
                                  ),
                          ),
                        ),
                        const SizedBox(width: 12),
                        Expanded(
                          child: OutlinedButton(
                            onPressed: _submitting ? null : _resetForm,
                            style: OutlinedButton.styleFrom(padding: const EdgeInsets.symmetric(vertical: 14)),
                            child: Text(Tr.translate('cancelButton'), style: const TextStyle(fontSize: 16)),
                          ),
                        ),
                      ],
                    ),
                  ],
                ),
              ),
            ),
    );
  }

  Widget _row(List<Widget> children) {
    return Row(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: children.expand((w) => [Expanded(child: w), const SizedBox(width: 12)]).toList()..removeLast(),
    );
  }

  Widget _field({
    required String label,
    required TextEditingController controller,
    bool numeric = false,
    String? Function(String?)? validator,
  }) {
    return TextFormField(
      controller: controller,
      keyboardType: numeric ? TextInputType.number : TextInputType.text,
      inputFormatters: numeric ? [FilteringTextInputFormatter.digitsOnly] : null,
      decoration: InputDecoration(labelText: label, border: const OutlineInputBorder()),
      validator: validator,
    );
  }

  Widget _datePicker() {
    return GestureDetector(
      onTap: _pickDate,
      child: AbsorbPointer(
        child: TextFormField(
          readOnly: true,
          decoration: InputDecoration(
            labelText: Tr.translate('sessionDate'),
            border: const OutlineInputBorder(),
            suffixIcon: const Icon(Icons.calendar_today, size: 18),
          ),
          controller: TextEditingController(
            text: _recitationDate != null
                ? '${_recitationDate!.year}/${_recitationDate!.month.toString().padLeft(2, '0')}/${_recitationDate!.day.toString().padLeft(2, '0')}'
                : '',
          ),
        ),
      ),
    );
  }

  Widget _studentDropdown() {
    return DropdownButtonFormField<int>(
      value: _selectedStudentId,
      decoration: InputDecoration(labelText: Tr.translate('student'), border: const OutlineInputBorder()),
      isExpanded: true,
      items: [
        DropdownMenuItem<int>(value: null, child: Text(Tr.translate('chooseStudent'))),
        ..._students.map((s) => DropdownMenuItem<int>(value: s.id, child: Text(s.name))),
      ],
      onChanged: (v) => setState(() => _selectedStudentId = v),
    );
  }

  Widget _mistakesSection() {
    return Card(
      elevation: 0,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(8),
        side: BorderSide(color: Colors.grey.shade300),
      ),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(Tr.translate('mistakesLog'), style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
                    const SizedBox(height: 2),
                    Text(Tr.translate('mistakesHint'), style: TextStyle(fontSize: 12, color: Colors.grey[600])),
                  ],
                ),
                TextButton.icon(
                  onPressed: _addMistake,
                  icon: const Icon(Icons.add, size: 18),
                  label: Text(Tr.translate('addMistake')),
                ),
              ],
            ),
            if (_mistakes.isEmpty) ...[
              const SizedBox(height: 16),
              Container(
                padding: const EdgeInsets.all(16),
                decoration: BoxDecoration(color: Colors.grey.shade50, borderRadius: BorderRadius.circular(6)),
                child: Text(Tr.translate('noMistakes'), style: TextStyle(color: Colors.grey[500]), textAlign: TextAlign.center),
              ),
            ] else ...[
              const SizedBox(height: 12),
              ...List.generate(_mistakes.length, (i) => _mistakeRow(i)),
            ],
          ],
        ),
      ),
    );
  }

  Widget _mistakeRow(int index) {
    final mistake = _mistakes[index];
    return Padding(
      padding: const EdgeInsets.only(bottom: 8),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.center,
        children: [
          Expanded(
            flex: 3,
            child: DropdownButtonFormField<int>(
              value: mistake.mistakeTypeId,
              decoration: InputDecoration(
                labelText: Tr.translate('mistakeType'),
                border: const OutlineInputBorder(),
                isDense: true,
                contentPadding: const EdgeInsets.symmetric(horizontal: 12, vertical: 10),
              ),
              isExpanded: true,
              items: [
                DropdownMenuItem<int>(value: null, child: Text(Tr.translate('choose'))),
                ..._mistakeTypes.map((t) => DropdownMenuItem<int>(value: t.id, child: Text(t.name))),
              ],
              onChanged: (v) => setState(() => mistake.mistakeTypeId = v),
            ),
          ),
          const SizedBox(width: 8),
          SizedBox(
            width: 72,
            child: TextFormField(
              initialValue: mistake.count.toString(),
              keyboardType: TextInputType.number,
              inputFormatters: [FilteringTextInputFormatter.digitsOnly],
              decoration: InputDecoration(
                labelText: Tr.translate('count'),
                border: const OutlineInputBorder(),
                isDense: true,
                contentPadding: const EdgeInsets.symmetric(horizontal: 8, vertical: 10),
              ),
              onChanged: (v) => mistake.count = int.tryParse(v) ?? 1,
            ),
          ),
          const SizedBox(width: 8),
          Expanded(
            flex: 2,
            child: TextFormField(
              initialValue: mistake.note,
              decoration: InputDecoration(
                labelText: Tr.translate('note'),
                border: const OutlineInputBorder(),
                isDense: true,
                contentPadding: const EdgeInsets.symmetric(horizontal: 8, vertical: 10),
              ),
              onChanged: (v) => mistake.note = v,
            ),
          ),
          const SizedBox(width: 8),
          IconButton(
            onPressed: () => _removeMistake(index),
            icon: const Icon(Icons.delete_outline, color: Colors.red),
            tooltip: Tr.translate('delete'),
          ),
        ],
      ),
    );
  }
}
