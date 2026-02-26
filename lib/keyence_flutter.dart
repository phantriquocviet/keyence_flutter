import 'keyence_flutter_platform_interface.dart';

class KeyenceFlutter {
  Future<String?> getPlatformVersion() {
    return KeyenceFlutterPlatform.instance.getPlatformVersion();
  }

  Future<void> createManager() {
    return KeyenceFlutterPlatform.instance.createManager();
  }

  Future<void> triggerAction() {
    return KeyenceFlutterPlatform.instance.triggerAction();
  }

  Future<void> onPause() {
    return KeyenceFlutterPlatform.instance.onPause();
  }

  Future<void> onResume() {
    return KeyenceFlutterPlatform.instance.onResume();
  }

  Future<void> onDestroy() {
    return KeyenceFlutterPlatform.instance.onDestroy();
  }

  Stream<String> get onScanSuccess =>
      KeyenceFlutterPlatform.instance.onScanSuccess;

  Stream<String> get onScanFailure =>
      KeyenceFlutterPlatform.instance.onScanFailure;
}
