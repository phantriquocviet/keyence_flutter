import 'dart:async';

import 'keyence_flutter_platform_interface.dart';
import 'src/messages.g.dart';

/// An implementation of [KeyenceFlutterPlatform] that uses Pigeon.
class MethodChannelKeyenceFlutter extends KeyenceFlutterPlatform
    implements KeyenceScannerFlutterApi {
  final _hostApi = KeyenceScannerHostApi();

  final _onScanSuccessController = StreamController<String>.broadcast();
  final _onScanFailureController = StreamController<String>.broadcast();

  MethodChannelKeyenceFlutter() {
    KeyenceScannerFlutterApi.setUp(this);
  }

  @override
  Future<String?> getPlatformVersion() {
    return _hostApi.getPlatformVersion();
  }

  @override
  Future<void> createManager() {
    return _hostApi.createManager();
  }

  @override
  Future<void> triggerAction() {
    return _hostApi.triggerAction();
  }

  @override
  Future<void> onPause() {
    return _hostApi.onPause();
  }

  @override
  Future<void> onResume() {
    return _hostApi.onResume();
  }

  @override
  Future<void> onDestroy() {
    return _hostApi.onDestroy();
  }

  @override
  Stream<String> get onScanSuccess => _onScanSuccessController.stream;

  @override
  Stream<String> get onScanFailure => _onScanFailureController.stream;

  @override
  void onHandleScanSuccess(String data) {
    _onScanSuccessController.add(data);
  }

  @override
  void onHandleScanFailure(String error) {
    _onScanFailureController.add(error);
  }
}
