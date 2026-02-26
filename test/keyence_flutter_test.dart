import 'package:flutter_test/flutter_test.dart';
import 'package:keyence_flutter/keyence_flutter.dart';
import 'package:keyence_flutter/keyence_flutter_method_channel.dart';
import 'package:keyence_flutter/keyence_flutter_platform_interface.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockKeyenceFlutterPlatform
    with MockPlatformInterfaceMixin
    implements KeyenceFlutterPlatform {
  @override
  Future<String?> getPlatformVersion() => Future.value('42');

  @override
  Future<void> createManager() => Future.value();

  @override
  Future<void> onDestroy() => Future.value();

  @override
  Future<void> onPause() => Future.value();

  @override
  Future<void> onResume() => Future.value();

  @override
  Future<void> triggerAction() => Future.value();

  @override
  Stream<String> get onScanFailure => const Stream.empty();

  @override
  Stream<String> get onScanSuccess => const Stream.empty();
}

void main() {
  final KeyenceFlutterPlatform initialPlatform =
      KeyenceFlutterPlatform.instance;

  test('$MethodChannelKeyenceFlutter is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelKeyenceFlutter>());
  });

  test('getPlatformVersion', () async {
    KeyenceFlutter keyenceFlutterPlugin = KeyenceFlutter();
    MockKeyenceFlutterPlatform fakePlatform = MockKeyenceFlutterPlatform();
    KeyenceFlutterPlatform.instance = fakePlatform;

    expect(await keyenceFlutterPlugin.getPlatformVersion(), '42');
  });
}
