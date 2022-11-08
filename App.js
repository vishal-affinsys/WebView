import React, {useEffect, useRef} from 'react';
import {
  StyleSheet,
  View,
  PermissionsAndroid,
  LoadingIndicatorView,
  Platform,
  BackHandler,
} from 'react-native';
import WebView from 'react-native-webview';

const App = () => {
  const webView = useRef(null);
  // const [canGoBack, setCanGoBack] = useState(false);

  useEffect(() => {
    if (Platform.OS === 'android') {
      BackHandler.addEventListener('hardwareBackPress', HandleBackPressed);

      return () => {
        BackHandler.removeEventListener('hardwareBackPress', HandleBackPressed);
      };
    }
  }, []); // INITIALIZE ONLY ONCE

  const HandleBackPressed = () => {
    if (webView.current.canGoBack) {
      webView.current.goBack();
      return true; // PREVENT DEFAULT BEHAVIOUR (EXITING THE APP)
    }
    return false;
  };

  useEffect(() => {
    async function getPermissions() {
      const granted = await PermissionsAndroid.requestMultiple([
        PermissionsAndroid.PERMISSIONS.CAMERA,
        PermissionsAndroid.PERMISSIONS.ACCESS_FINE_LOCATION,
        PermissionsAndroid.PERMISSIONS.ACCESS_COARSE_LOCATION,
        PermissionsAndroid.PERMISSIONS.READ_EXTERNAL_STORAGE,
      ]);
      console.log(granted);
      return granted;
    }
    getPermissions();
  }, []);

  return (
    <View style={styles.body}>
      <WebView
        ref={webView}
        onNavigationStateChange={navState => {
          webView.current.canGoBack = navState.canGoBack;
        }}
        source={{uri: 'www.google.com'}}
        geolocationEnabled={true}
        renderLoading={LoadingIndicatorView}
        startInLoadingState={true}
        mediaPlaybackRequiresUserAction={false}
        javaScriptEnabled={true}
        allowFileAccess={true}
      />
    </View>
  );
};

const styles = StyleSheet.create({
  body: {
    flex: 1,
  },
});

export default App;
