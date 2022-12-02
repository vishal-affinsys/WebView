import 'package:flutter/material.dart';
import 'package:permission_handler/permission_handler.dart';

import 'camera.dart';
import 'fileUpload.dart';
import 'location.dart';

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key});

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  @override
  void initState() {
    Future.microtask(() async {
      await Permission.camera.request();
      await Permission.location.request();
      await Permission.microphone.request();
      await Permission.accessMediaLocation.request();
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
