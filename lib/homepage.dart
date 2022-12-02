import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:web/fileUpload.dart';

import 'camera.dart';
import 'location.dart';

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key});

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  String _batteryLevel = 'Unknown battery level.';
  static const platform = MethodChannel('samples.flutter.dev/battery');

  Future<void> _getBatteryLevel() async {
    String batteryLevel;
    try {
      final int result = await platform.invokeMethod('getBatteryLevel');
      print("batter: $result");
      batteryLevel = 'Battery level at $result % .';
    } on PlatformException catch (e) {
      batteryLevel = "Failed to get battery level: '${e.message}'.";
    }

    setState(() {
      _batteryLevel = batteryLevel;
    });
  }

  @override
  void initState() {
    Future.microtask(() async {
      await Permission.camera.request();
      await Permission.location.request();
      await Permission.accessMediaLocation.request();
      await Permission.storage.request();
      await Permission.manageExternalStorage.request();
      await _getBatteryLevel();
    });

    super.initState();
  }

  int currentIndex = 0;
  List<Widget> screens = [
    const CameraPage(),
    const LocationPage(),
    const FileUpload()
  ];

  @override
  Widget build(BuildContext context) {
    return SafeArea(
      child: Scaffold(
        body: SizedBox(child: screens[currentIndex]),
        bottomNavigationBar: BottomNavigationBar(
          items: const [
            BottomNavigationBarItem(icon: Icon(Icons.camera), label: "Camera"),
            BottomNavigationBarItem(
                icon: Icon(Icons.location_pin), label: "Location"),
            BottomNavigationBarItem(
                icon: Icon(Icons.file_upload), label: "Upload")
          ],
          currentIndex: currentIndex,
          onTap: (value) => setState(() {
            currentIndex = value;
          }),
        ),
      ),
    );
  }
}
