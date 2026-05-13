import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import '../l10n/tr.dart';
import '../models/recitation_form_data.dart';
import '../models/student.dart';
import '../services/api_service.dart';
import '../widgets/surah_aya_field.dart';
import 'login_screen.dart';
import 'recitation_mistakes_screen.dart';

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
  final _numberOfAyatCtrl = TextEditingController();
  final _gradeCtrl = TextEditingController();

  DateTime? _recitationDate;
  int? _selectedStudentId;
  int? _fromSurah;
  int? _fromAya;
  int? _toSurah;
  int? _toAya;

  List<Student> _students = [];
  bool _loadingData = true;
  String _error = '';

  @override
  void initState() {
    super.initState();
    _loadData();
  }

  Future<void> _loadData() async {
    try {
      final data = await ApiService.get('/students', params: {'page': '0', 'size': '100'});
      setState(() {
        _students = ((data['content'] ?? data) as List)
            .map((e) => Student.fromJson(e as Map<String, dynamic>))
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

  Future<void> _startRecitation() async {
    if (!_formKey.currentState!.validate()) return;

    final formData = RecitationFormData(
      code: _codeCtrl.text.isEmpty ? null : _codeCtrl.text,
      recitationDate: _recitationDate,
      studentId: _selectedStudentId,
      fromSurah: _fromSurah,
      toSurah: _toSurah,
      fromAya: _fromAya,
      toAya: _toAya,
      numberOfAyat: int.tryParse(_numberOfAyatCtrl.text),
      grade: int.tryParse(_gradeCtrl.text),
      notes: _notesCtrl.text.isEmpty ? null : _notesCtrl.text,
    );

    final saved = await Navigator.push<bool>(
      context,
      MaterialPageRoute(
        builder: (_) => RecitationMistakesScreen(
          formData: formData,
          editId: widget.editId,
        ),
      ),
    );

    if (saved == true && mounted) {
      _resetForm();
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(Tr.translate('savedSuccessfully')),
          backgroundColor: Colors.green,
        ),
      );
    }
  }

  void _resetForm() {
    _formKey.currentState?.reset();
    _codeCtrl.clear();
    _notesCtrl.clear();
    _numberOfAyatCtrl.clear();
    _gradeCtrl.clear();
    setState(() {
      _recitationDate = null;
      _selectedStudentId = null;
      _fromSurah = null;
      _fromAya = null;
      _toSurah = null;
      _toAya = null;
      _error = '';
    });
  }

  @override
  void dispose() {
    _codeCtrl.dispose();
    _notesCtrl.dispose();
    _numberOfAyatCtrl.dispose();
    _gradeCtrl.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.editId != null
            ? Tr.translate('editRecitation')
            : Tr.translate('addRecitation')),
        centerTitle: true,
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

                    SurahAyaField(
                      surahLabel: Tr.translate('fromSurah'),
                      ayaLabel: Tr.translate('fromAya'),
                      surahValue: _fromSurah,
                      ayaValue: _fromAya,
                      onSurahChanged: (v) => setState(() => _fromSurah = v),
                      onAyaChanged: (v) => setState(() => _fromAya = v),
                    ),
                    const SizedBox(height: 12),

                    SurahAyaField(
                      surahLabel: Tr.translate('toSurah'),
                      ayaLabel: Tr.translate('toAya'),
                      surahValue: _toSurah,
                      ayaValue: _toAya,
                      onSurahChanged: (v) => setState(() => _toSurah = v),
                      onAyaChanged: (v) => setState(() => _toAya = v),
                    ),
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

                    Row(
                      children: [
                        Expanded(
                          child: FilledButton.icon(
                            onPressed: _startRecitation,
                            icon: const Icon(Icons.arrow_back),
                            label: Text(Tr.translate('startRecitation'), style: const TextStyle(fontSize: 16)),
                            style: FilledButton.styleFrom(padding: const EdgeInsets.symmetric(vertical: 14)),
                          ),
                        ),
                        const SizedBox(width: 12),
                        Expanded(
                          child: OutlinedButton(
                            onPressed: _resetForm,
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
      children: children
          .expand((w) => [Expanded(child: w), const SizedBox(width: 12)])
          .toList()
        ..removeLast(),
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
      initialValue: _selectedStudentId,
      decoration: InputDecoration(labelText: Tr.translate('student'), border: const OutlineInputBorder()),
      isExpanded: true,
      items: [
        DropdownMenuItem<int>(value: null, child: Text(Tr.translate('chooseStudent'))),
        ..._students.map((s) => DropdownMenuItem<int>(value: s.id, child: Text(s.name))),
      ],
      onChanged: (v) => setState(() => _selectedStudentId = v),
    );
  }
}
