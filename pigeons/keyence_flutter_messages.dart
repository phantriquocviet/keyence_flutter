import 'package:pigeon/pigeon.dart';

@ConfigurePigeon(
  PigeonOptions(
    dartOut: 'lib/src/messages.g.dart',
    kotlinOut:
        'android/src/main/kotlin/vn/tridev/keyenceflutter/keyence_flutter/Messages.g.kt',
    kotlinOptions: KotlinOptions(
      package: 'vn.tridev.keyenceflutter.keyence_flutter',
    ),
    dartPackageName: 'keyence_flutter',
  ),
)
@HostApi()
abstract class KeyenceScannerHostApi {
  String getPlatformVersion();
  void createManager();
  void triggerAction();
  void onPause();
  void onResume();
  void onDestroy();
}

@FlutterApi()
abstract class KeyenceScannerFlutterApi {
  void onHandleScanSuccess(String data);
  void onHandleScanFailure(String error);
}
