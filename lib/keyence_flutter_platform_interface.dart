import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'keyence_flutter_method_channel.dart';

abstract class KeyenceFlutterPlatform extends PlatformInterface {
  /// Constructs a KeyenceFlutterPlatform.
  KeyenceFlutterPlatform() : super(token: _token);

  static final Object _token = Object();

  static KeyenceFlutterPlatform _instance = MethodChannelKeyenceFlutter();

  /// The default instance of [KeyenceFlutterPlatform] to use.
  ///
  /// Defaults to [MethodChannelKeyenceFlutter].
  static KeyenceFlutterPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [KeyenceFlutterPlatform] when
  /// they register themselves.
  static set instance(KeyenceFlutterPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  Future<void> createManager() {
    throw UnimplementedError('createManager() has not been implemented.');
  }

  Future<void> triggerAction() {
    throw UnimplementedError('triggerAction() has not been implemented.');
  }

  Future<void> onPause() {
    throw UnimplementedError('onPause() has not been implemented.');
  }

  Future<void> onResume() {
    throw UnimplementedError('onResume() has not been implemented.');
  }

  Future<void> onDestroy() {
    throw UnimplementedError('onDestroy() has not been implemented.');
  }

  Stream<String> get onScanSuccess {
    throw UnimplementedError('onScanSuccess has not been implemented.');
  }

  Stream<String> get onScanFailure {
    throw UnimplementedError('onScanFailure has not been implemented.');
  }
}
