import 'package:flutter/material.dart';
import 'package:flutter_inappwebview/flutter_inappwebview.dart';

class LocationPage extends StatefulWidget {
  const LocationPage({super.key});

  @override
  State<LocationPage> createState() => _LocationPageState();
}

class _LocationPageState extends State<LocationPage> {
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
      child: SizedBox(
        child: InAppWebView(
          initialUrlRequest: URLRequest(url: Uri.parse('www.google.com/maps')),
          initialOptions: InAppWebViewGroupOptions(
            crossPlatform: InAppWebViewOptions(
              mediaPlaybackRequiresUserGesture: false,
              cacheEnabled: true,
              javaScriptEnabled: true,
              useShouldOverrideUrlLoading: true,
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
        ),
      ),
    );
  }
}
