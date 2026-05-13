import 'package:flutter/material.dart';
import '../l10n/tr.dart';
import '../services/api_service.dart';
import '../widgets/recent_recitation_tile.dart';
import '../widgets/stat_card.dart';
import 'recitation_form_screen.dart';

class DashboardTab extends StatefulWidget {
  final VoidCallback onNewRecitation;

  const DashboardTab({super.key, required this.onNewRecitation});

  @override
  State<DashboardTab> createState() => _DashboardTabState();
}

class _DashboardTabState extends State<DashboardTab> {
  bool _loading = true;
  String _error = '';
  Map<String, dynamic>? _summary;
  List<dynamic> _recent = [];

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    setState(() { _loading = true; _error = ''; });
    try {
      final data = await ApiService.get('/reports/overview') as Map<String, dynamic>;
      setState(() {
        _summary = data['summary'] as Map<String, dynamic>?;
        _recent = (data['recentRecitations'] as List<dynamic>?) ?? [];
        _loading = false;
      });
    } on ApiException catch (e) {
      if (e.isUnauthorized) {
        await ApiService.clearToken();
        if (mounted) {
          Navigator.pushReplacementNamed(context, '/login');
        }
        return;
      }
      setState(() { _error = e.message; _loading = false; });
    } catch (e) {
      setState(() { _error = e.toString(); _loading = false; });
    }
  }

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;
    final name = ApiService.currentUser?.name ?? '';

    return Scaffold(
      appBar: AppBar(
        title: Text(Tr.translate('appTitle')),
        centerTitle: true,
      ),
      body: _loading
          ? const Center(child: CircularProgressIndicator())
          : _error.isNotEmpty
              ? Center(
                  child: Column(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      Text(_error, textAlign: TextAlign.center),
                      const SizedBox(height: 16),
                      OutlinedButton(
                        onPressed: _load,
                        child: Text(Tr.translate('retry')),
                      ),
                    ],
                  ),
                )
              : RefreshIndicator(
                  onRefresh: _load,
                  child: ListView(
                    padding: const EdgeInsets.all(16),
                    children: [
                      // Greeting
                      Text(
                        Tr.translate('welcome', {'name': name}),
                        style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                              fontWeight: FontWeight.bold,
                            ),
                      ),
                      const SizedBox(height: 20),

                      // Stats grid
                      if (_summary != null) _buildStatsGrid(context, _summary!),
                      const SizedBox(height: 20),

                      // Quick action
                      FilledButton.icon(
                        onPressed: widget.onNewRecitation,
                        icon: const Icon(Icons.add),
                        label: Text(Tr.translate('newRecitation')),
                        style: FilledButton.styleFrom(
                          padding: const EdgeInsets.symmetric(vertical: 14),
                        ),
                      ),
                      const SizedBox(height: 24),

                      // Recent recitations
                      Text(
                        Tr.translate('recentRecitations'),
                        style: Theme.of(context).textTheme.titleMedium?.copyWith(
                              fontWeight: FontWeight.bold,
                            ),
                      ),
                      const SizedBox(height: 8),
                      if (_recent.isNotEmpty)
                        ..._recent.map((r) => RecentRecitationTile(
                              recitation: r as Map<String, dynamic>,
                              primaryColor: colorScheme.primary,
                            ))
                      else
                        Padding(
                          padding: const EdgeInsets.symmetric(vertical: 24),
                          child: Center(
                            child: Text(
                              Tr.translate('noRecentRecitations'),
                              style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                                    color: colorScheme.onSurfaceVariant,
                                  ),
                            ),
                          ),
                        ),
                    ],
                  ),
                ),
    );
  }

  Widget _buildStatsGrid(BuildContext context, Map<String, dynamic> summary) {
    final stats = [
      (Tr.translate('students'), summary['totalStudents'], Icons.people_outline),
      (Tr.translate('groups'), summary['totalGroups'], Icons.groups_outlined),
      (Tr.translate('recitations'), summary['totalRecitations'], Icons.menu_book_outlined),
      (Tr.translate('averageGrade'), summary['averageGrade'], Icons.star_outline),
    ];

    return GridView.count(
      crossAxisCount: 2,
      shrinkWrap: true,
      physics: const NeverScrollableScrollPhysics(),
      crossAxisSpacing: 12,
      mainAxisSpacing: 12,
      childAspectRatio: 1.4,
      children: stats.map((s) => StatCard(label: s.$1, value: s.$2, icon: s.$3)).toList(),
    );
  }
}

