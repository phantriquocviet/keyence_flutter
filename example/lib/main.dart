import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:keyence_flutter/keyence_flutter.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      theme: ThemeData(useMaterial3: true, colorSchemeSeed: Colors.blue),
      home: const KeyenceExampleHome(),
    );
  }
}

class KeyenceExampleHome extends StatefulWidget {
  const KeyenceExampleHome({super.key});

  @override
  State<KeyenceExampleHome> createState() => _KeyenceExampleHomeState();
}

class _KeyenceExampleHomeState extends State<KeyenceExampleHome>
    with WidgetsBindingObserver {
  String _platformVersion = 'Unknown';
  final _keyenceFlutterPlugin = KeyenceFlutter();
  final List<String> _scanResults = [];
  StreamSubscription? _scanSuccessSubscription;
  StreamSubscription? _scanFailureSubscription;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addObserver(this);
    initPlatformState();
    _initScanner();
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    _scanSuccessSubscription?.cancel();
    _scanFailureSubscription?.cancel();
    _keyenceFlutterPlugin.onDestroy();
    super.dispose();
  }

  void _initScanner() {
    _keyenceFlutterPlugin.createManager();

    _scanSuccessSubscription = _keyenceFlutterPlugin.onScanSuccess.listen((
      data,
    ) {
      setState(() {
        _scanResults.insert(0, data);
      });
    });

    _scanFailureSubscription = _keyenceFlutterPlugin.onScanFailure.listen((
      error,
    ) {
      if (!mounted) return;
      ScaffoldMessenger.of(
        context,
      ).showSnackBar(SnackBar(content: Text('Scan Failed: $error')));
    });
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    try {
      platformVersion =
          await _keyenceFlutterPlugin.getPlatformVersion() ??
          'Unknown platform version';
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Keyence Plugin Example'), elevation: 2),
      body: Column(
        children: [
          Padding(
            padding: const EdgeInsets.all(16.0),
            child: Card(
              child: Padding(
                padding: const EdgeInsets.all(16.0),
                child: Column(
                  children: [
                    Text(
                      'Running on: $_platformVersion',
                      style: Theme.of(context).textTheme.titleMedium,
                    ),
                    const SizedBox(height: 16),
                    ElevatedButton.icon(
                      onPressed: () => _keyenceFlutterPlugin.triggerAction(),
                      icon: const Icon(Icons.qr_code_scanner),
                      label: const Text('Trigger Scan'),
                      style: ElevatedButton.styleFrom(
                        minimumSize: const Size.fromHeight(50),
                      ),
                    ),
                  ],
                ),
              ),
            ),
          ),
          const Padding(
            padding: EdgeInsets.symmetric(horizontal: 16.0),
            child: Divider(),
          ),
          Expanded(
            child: _scanResults.isEmpty
                ? const Center(child: Text('No scans yet'))
                : ListView.builder(
                    itemCount: _scanResults.length,
                    itemBuilder: (context, index) {
                      return ListTile(
                        leading: const Icon(Icons.barcode_reader),
                        title: Text(_scanResults[index]),
                        subtitle: Text('Scan #${_scanResults.length - index}'),
                        trailing: IconButton(
                          icon: const Icon(Icons.copy),
                          onPressed: () {
                            Clipboard.setData(
                              ClipboardData(text: _scanResults[index]),
                            );
                            ScaffoldMessenger.of(context).showSnackBar(
                              const SnackBar(
                                content: Text('Copied to clipboard'),
                              ),
                            );
                          },
                        ),
                      );
                    },
                  ),
          ),
        ],
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          setState(() {
            _scanResults.clear();
          });
        },
        tooltip: 'Clear Results',
        child: const Icon(Icons.delete_outline),
      ),
    );
  }
}
