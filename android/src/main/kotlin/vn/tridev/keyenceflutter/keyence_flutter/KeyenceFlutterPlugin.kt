package vn.tridev.keyenceflutter.keyence_flutter

import io.flutter.embedding.engine.plugins.FlutterPlugin
import android.content.Context
import android.os.Build

/** KeyenceFlutterPlugin */
class KeyenceFlutterPlugin : FlutterPlugin, KeyenceScannerHostApi {
    
    private var scanBarcode: ScanBarcode? = null
    private var context: Context? = null
    private var flutterApi: KeyenceScannerFlutterApi? = null

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        context = flutterPluginBinding.applicationContext
        val messenger = flutterPluginBinding.binaryMessenger
        KeyenceScannerHostApi.setUp(messenger, this)
        flutterApi = KeyenceScannerFlutterApi(messenger)
    }

    override fun getPlatformVersion(): String {
        return "Android ${Build.VERSION.RELEASE}"
    }

    override fun createManager() {
        context?.let { ctx ->
            flutterApi?.let { api ->
                // Release old manager if exists
                scanBarcode?.onDestroy()
                scanBarcode = ScanBarcode(ctx, api)
            }
        }
    }

    override fun triggerAction() {
        scanBarcode?.triggerAction()
    }

    override fun onPause() {
        scanBarcode?.onPause()
    }

    override fun onResume() {
        scanBarcode?.onResume()
    }

    override fun onDestroy() {
        scanBarcode?.onDestroy()
        scanBarcode = null
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        KeyenceScannerHostApi.setUp(binding.binaryMessenger, null)
        context = null
        flutterApi = null
        scanBarcode?.onDestroy()
        scanBarcode = null
    }
}
