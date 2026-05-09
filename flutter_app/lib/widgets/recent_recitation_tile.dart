import 'package:flutter/material.dart';
import '../screens/recitation_form_screen.dart';

class RecentRecitationTile extends StatelessWidget {
  final Map<String, dynamic> recitation;
  final Color primaryColor;

  const RecentRecitationTile({super.key, required this.recitation, required this.primaryColor});

  @override
  Widget build(BuildContext context) {
    final student = recitation['student'] as Map<String, dynamic>?;
    final studentName = student?['name'] as String? ?? '—';
    final date = recitation['recitationDate'] as String? ?? '';
    final grade = recitation['grade'];
    final id = recitation['id'] as int?;

    return Card(
      elevation: 0,
      margin: const EdgeInsets.only(bottom: 8),
      child: ListTile(
        leading: CircleAvatar(
          backgroundColor: primaryColor.withValues(alpha: 0.12),
          child: Text(
            studentName.isNotEmpty ? studentName[0] : '؟',
            style: TextStyle(color: primaryColor, fontWeight: FontWeight.bold),
          ),
        ),
        title: Text(studentName),
        subtitle: Text(date),
        trailing: grade != null
            ? Chip(
                label: Text(grade.toString()),
                backgroundColor: primaryColor.withValues(alpha: 0.1),
                labelStyle: TextStyle(color: primaryColor, fontWeight: FontWeight.bold),
                side: BorderSide.none,
                padding: EdgeInsets.zero,
              )
            : null,
        onTap: id == null
            ? null
            : () {
                Navigator.push(
                  context,
                  MaterialPageRoute(
                    builder: (_) => RecitationFormScreen(editId: id),
                  ),
                );
              },
      ),
    );
  }
}
