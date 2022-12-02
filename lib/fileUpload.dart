import 'dart:io';
import 'dart:ui';
import 'dart:isolate';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_downloader/flutter_downloader.dart';
import 'package:flutter_inappwebview/flutter_inappwebview.dart';
import 'package:permission_handler/permission_handler.dart';

class FileUpload extends StatefulWidget {
  const FileUpload({super.key});

  @override
  State<FileUpload> createState() => _FileUploadState();
}

class _FileUploadState extends State<FileUpload> {
  late InAppWebViewController _webViewController;

  final ReceivePort _port = ReceivePort();

  @override
  void initState() {
    super.initState();

    IsolateNameServer.registerPortWithName(
        _port.sendPort, 'downloader_send_port');
    _port.listen((dynamic data) {
      String id = data[0];
      DownloadTaskStatus status = data[1];
      int progress = data[2];
      print(progress);
      print(status.toString());
      setState(() {});
    });

    FlutterDownloader.registerCallback(downloadCallback);
  }

  @override
  void dispose() {
    IsolateNameServer.removePortNameMapping('downloader_send_port');
    super.dispose();
  }

  @pragma('vm:entry-point')
  static void downloadCallback(
      String id, DownloadTaskStatus status, int progress) {
    final SendPort send =
        IsolateNameServer.lookupPortByName('downloader_send_port')!;
    send.send([id, status, progress]);
  }

  void _download(String url) async {
    final status = await Permission.storage.request();

    if (status.isGranted) {
      Directory dir = Directory('/storage/emulated/0/Download');

      final id = await FlutterDownloader.enqueue(
        url: url,
        savedDir: dir.path,
        // fileName: 'bankBuddy_pdf.docx',
        showNotification: true,
        openFileFromNotification: true,
        saveInPublicStorage: true,
      );
    } else {
      print('Permission Denied');
    }
  }

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: () async {
        if (await _webViewController.canGoBack()) {
          _webViewController.goBack();
          return false;
        } else {
          return true;
        }
      },
      child: InAppWebView(
        initialUrlRequest: URLRequest(url: Uri.parse('www.google.com')),
        initialOptions: InAppWebViewGroupOptions(
          crossPlatform: InAppWebViewOptions(
            mediaPlaybackRequiresUserGesture: false,
            cacheEnabled: true,
            javaScriptEnabled: true,
            useOnDownloadStart: true,
            supportZoom: true,
          ),
          android: AndroidInAppWebViewOptions(
            domStorageEnabled: true,
            databaseEnabled: true,
            clearSessionCache: true,
            thirdPartyCookiesEnabled: true,
            allowFileAccess: true,
            allowContentAccess: true,
          ),
        ),
        onWebViewCreated: (InAppWebViewController controller) {
          _webViewController = controller;
        },
        androidOnPermissionRequest: (InAppWebViewController controller,
            String origin, List<String> resources) async {
          return PermissionRequestResponse(
            resources: resources,
            action: PermissionRequestResponseAction.GRANT,
          );
        },
        androidOnGeolocationPermissionsShowPrompt:
            (InAppWebViewController controller, String origin) async {
          return GeolocationPermissionShowPromptResponse(
            origin: origin,
            allow: true,
            retain: true,
          );
        },
        onDownloadStartRequest: (controller, url) async {
          String path = Directory('/storage/emulated/0/Download').path;
          // print();
          _download(url.url.toString());
        },
      ),
    );
  }
}
