import 'package:flutter/material.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'l10n/tr.dart';
import 'services/api_service.dart';
import 'screens/login_screen.dart';
import 'screens/recitation_form_screen.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await Tr.load('ar');
  await ApiService.init();
  runApp(Al7afezApp(startLoggedIn: ApiService.hasToken));
}

class Al7afezApp extends StatelessWidget {
  final bool startLoggedIn;
  const Al7afezApp({super.key, required this.startLoggedIn});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: Tr.translate('appTitle'),
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: const Color(0xFF1E6F50)),
        useMaterial3: true,
        fontFamily: 'Roboto',
      ),
      locale: const Locale('ar'),
      supportedLocales: const [Locale('ar'), Locale('en')],
      localizationsDelegates: const [
        GlobalMaterialLocalizations.delegate,
        GlobalWidgetsLocalizations.delegate,
        GlobalCupertinoLocalizations.delegate,
      ],
      builder: (context, child) => Directionality(
        textDirection: TextDirection.rtl,
        child: child!,
      ),
      home: startLoggedIn ? const RecitationFormScreen() : const LoginScreen(),
    );
  }
}
