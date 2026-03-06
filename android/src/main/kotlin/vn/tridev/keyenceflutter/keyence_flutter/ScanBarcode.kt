package vn.tridev.keyenceflutter.keyence_flutter

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.keyence.autoid.sdk.SdkStatus
import com.keyence.autoid.sdk.scan.DecodeResult
import com.keyence.autoid.sdk.scan.ScanManager
import com.keyence.autoid.sdk.scan.scanparams.CodeType
import com.keyence.autoid.sdk.scan.scanparams.ScanParams
import com.keyence.autoid.sdk.scan.scanparams.scanParams.Trigger

class ScanBarcode(private val context: Context, private val api: KeyenceScannerFlutterApi) : ScanManager.DataListener {

    private var tagName: String = ScanBarcode::class.java.simpleName
    private var mScanManager: ScanManager? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    init {
        //init & create scanner
        mScanManager = ScanManager.createScanManager(context)
        mScanManager?.let { manager ->
            if (manager.isEnabled) {
                manager.addDataListener(this)
                initScanner(manager)
                setScanParams()
            }
        }
    }

    fun triggerAction() {
        Log.v(tagName, "triggerAction() called")

        try {
            val manager = checkAndGetManager()
            if (manager != null) {
                if (manager.isEnabled) {
                    Log.v(tagName, "Executing startRead()")
                    manager.startRead()
                } else {
                    sendFailure("Scanner disabled")
                }
            } else {
                sendFailure("ScanManager not available")
            }
        } catch (t: Throwable) {
            Log.e(tagName, "triggerAction error", t)
            sendFailure("Hardware Error: ${t.message}")
        }
    }

    private fun checkAndGetManager(): ScanManager? {
        if (mScanManager == null) {
            Log.v(tagName, "ScanManager is null, attempting to initialize...")
            initScanManager()
        }
        return mScanManager
    }

    private fun initScanManager() {
        try {
            mScanManager = ScanManager.createScanManager(context)
            mScanManager?.let { manager ->
                if (manager.isEnabled) {
                    manager.addDataListener(this)
                    initScanner(manager)
                    Log.v(tagName, "ScanManager re-initialized successfully")
                }
            }
        } catch (t: Throwable) {
            Log.e(tagName, "Failed to init ScanManager", t)
        }
    }

    private fun initScanner(manager: ScanManager) {
        try {
            val codeType = CodeType()
            if (manager.getConfig(codeType) == SdkStatus.SUCCESS) {
                codeType.upcEanJan = true
                codeType.code39 = true
                codeType.code128 = true
                codeType.qrCode = true
                manager.setConfig(codeType)
            }
        } catch (t: Throwable) {
            Log.w(tagName, "initScanner failed: ${t.message}")
        }
    }

    private fun setScanParams() {
        //to set scan trigger
        val scanParams = ScanParams()
        scanParams.trigger.triggerMode = Trigger.TriggerMode.NORMAL
        scanParams.trigger.scannerTimeout = 10 // Increased to 10 seconds
        mScanManager?.let { manager ->
            Log.v(tagName, "setConfig")
            manager.setConfig(scanParams)
        }
    }

    private fun cancelTriggerEvent() {
        Log.v(tagName, "cancelTriggerEvent()")
        // Acquire the reading status.
        mScanManager?.let { manager ->
            if (manager.isReading) {
                // Stop reading.
                manager.stopRead()
            }
        }
    }

    fun onPause() {
        Log.v(tagName, "onPause() - lockScanner()")
        lockScanner()
    }

    private fun lockScanner() {
        mScanManager?.let { manager ->
            if (manager.isEnabled) {
                //lock scanner
                manager.lockScanner()
            }
        }
    }

    fun onResume() {
        Log.v(tagName, "onResume() - unlockScanner()")
        unlockScanner()
    }

    private fun unlockScanner() {
        mScanManager?.let { manager ->
            if (manager.isEnabled) {
                //claim barcode reader
                manager.unlockScanner()
            }
        }
    }

    fun onDestroy() {
        Log.v(tagName, "onDestroy()")
        mScanManager?.let { manager ->
            manager.removeDataListener(this)
            manager.releaseScanManager()
        }
        mScanManager = null
    }

    // Create a read event.
    override fun onDataReceived(decodeResult: DecodeResult?) {
        Log.v(tagName, "onDataReceived()")
        
        if (decodeResult == null) return

        // Acquire the reading result.
        val result: DecodeResult.Result = decodeResult.result
        Log.v(tagName, "result: $result")

        when (result) {
            DecodeResult.Result.SUCCESS -> {
                // Acquire the read code type.
                val data: String = decodeResult.data
                Log.v(tagName, "readData: $data")
                sendSuccess(data)
            }
            DecodeResult.Result.TIMEOUT -> {
                sendFailure("TIMEOUT")
            }
            DecodeResult.Result.CANCELED -> {
                sendFailure("CANCELED")
            }
            DecodeResult.Result.FAILED -> {
                sendFailure("FAILED")
            }
            else -> {
                sendFailure(result.name)
            }
        }
    }

    private fun sendSuccess(data: String) {
        mainHandler.post {
            api.onHandleScanSuccess(data) {}
        }
    }

    private fun sendFailure(error: String) {
        mainHandler.post {
            api.onHandleScanFailure(error) {}
        }
    }
}