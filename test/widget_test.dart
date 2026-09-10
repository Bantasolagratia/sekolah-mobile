import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:sekolah_mobile_app/main.dart';
import 'package:sekolah_mobile_app/screens/login_screen.dart';
import 'package:shared_preferences/shared_preferences.dart';

void main() {
  setUp(() {
    SharedPreferences.setMockInitialValues({});
  });

  testWidgets('SekolahMobileApp renders LoginScreen when not authenticated', (WidgetTester tester) async {
    await tester.pumpWidget(const SekolahMobileApp());
    await tester.pumpAndSettle();

    expect(find.byType(LoginScreen), findsOneWidget);
    expect(find.text('SISTEM INFORMASI AKADEMIK'), findsOneWidget);
    expect(find.text('PORTAL MOBILE SEKOLAH'), findsOneWidget);
    expect(find.text('Masuk ke Portal'), findsOneWidget);
    expect(find.byType(TextFormField), findsNWidgets(2));
  });
}
