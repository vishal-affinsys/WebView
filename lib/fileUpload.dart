import 'package:flutter/material.dart';
import 'package:flutter_inappwebview/flutter_inappwebview.dart';

class FileUpload extends StatefulWidget {
  const FileUpload({super.key});

  @override
  State<FileUpload> createState() => _FileUploadState();
}

class _FileUploadState extends State<FileUpload> {
  late InAppWebViewController _webViewController;

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
          print("onDownloadStart $url");
          // final taskId = await FlutterDownloader.enqueue(
          //   url: url,
          //   savedDir: (await getExternalStorageDirectory()).path,
          //   showNotification:
          //       true, // show download progress in status bar (for Android)
          //   openFileFromNotification:
          //       true, // click on notification to open downloaded file (for Android)
          // );
        },
      ),
    );
  }
}
