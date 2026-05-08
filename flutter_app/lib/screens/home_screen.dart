import 'package:flutter/material.dart';
import '../l10n/tr.dart';
import 'dashboard_tab.dart';
import 'profile_tab.dart';
import 'recitation_form_screen.dart';

enum _Tab { dashboard, recitation, profile }

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  _Tab _selected = _Tab.dashboard;

  Widget _tabWidget(_Tab tab) => switch (tab) {
        _Tab.dashboard => DashboardTab(
            onNewRecitation: () => setState(() => _selected = _Tab.recitation),
          ),
        _Tab.recitation => const RecitationFormScreen(),
        _Tab.profile => const ProfileTab(),
      };

  BottomNavigationBarItem _navItem(_Tab tab) => switch (tab) {
        _Tab.dashboard => BottomNavigationBarItem(
            icon: const Icon(Icons.home_outlined),
            activeIcon: const Icon(Icons.home),
            label: Tr.translate('home'),
          ),
        _Tab.recitation => BottomNavigationBarItem(
            icon: const Icon(Icons.add_circle_outline),
            activeIcon: const Icon(Icons.add_circle),
            label: Tr.translate('newRecitation'),
          ),
        _Tab.profile => BottomNavigationBarItem(
            icon: const Icon(Icons.person_outline),
            activeIcon: const Icon(Icons.person),
            label: Tr.translate('profile'),
          ),
      };

  @override
  Widget build(BuildContext context) {
    const tabs = _Tab.values;

    return Scaffold(
      body: IndexedStack(
        index: _selected.index,
        children: tabs.map(_tabWidget).toList(),
      ),
      bottomNavigationBar: BottomNavigationBar(
        currentIndex: _selected.index,
        onTap: (i) => setState(() => _selected = _Tab.values[i]),
        items: tabs.map(_navItem).toList(),
      ),
    );
  }
}
