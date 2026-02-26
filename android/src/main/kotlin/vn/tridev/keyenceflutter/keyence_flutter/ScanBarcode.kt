package vn.tridev.keyenceflutter.keyence_flutter

import android.content.Context
import android.util.Log
import com.keyence.autoid.sdk.SdkStatus
import com.keyence.autoid.sdk.scan.DecodeResult
import com.keyence.autoid.sdk.scan.ScanManager
import com.keyence.autoid.sdk.scan.scanparams.CodeType
import com.keyence.autoid.sdk.scan.scanparams.ScanParams
import com.keyence.autoid.sdk.scan.scanparams.scanParams.Trigger

class ScanBarcode(context: Context, private val api: KeyenceScannerFlutterApi) : ScanManager.DataListener {

    private var tagName: String = ScanBarcode::class.java.simpleName
    private var mScanManager: ScanManager? = null

    init {
        //init & create scanner
        mScanManager = ScanManager.createScanManager(context)
        if (mScanManager!!.isEnabled) {
            mScanManager!!.addDataListener(this)
            initScanner()
            setScanParams()
        }
    }

    fun triggerAction() {
        Log.v(tagName, "triggerAction()")

        try {
            if (mScanManager != null) {
                if (mScanManager!!.isEnabled) {
                    // Start reading.
                    mScanManager!!.startRead()
                } else {
                    api.onHandleScanFailure("ScanManager is not available.") {}
                }
            }
        } catch (ex: Exception) {
            Log.v(tagName, "Exception: " + ex.localizedMessage)
        }
    }

    private fun setScanParams() {
        //to set scan trigger
        val scanParams = ScanParams()
        scanParams.trigger.triggerMode = Trigger.TriggerMode.NORMAL
        scanParams.trigger.continuousMode.redundancyTimeout = 5000 //milliseconds
        scanParams.trigger.continuousMode.successCodesCounter.enable = false //to use continuous count
        scanParams.trigger.scannerTimeout = 5 //seconds
        if (mScanManager != null) {
            Log.v(tagName, "setConfig")
            mScanManager!!.setConfig(scanParams)
        }
    }

    private fun cancelTriggerEvent() {
        Log.v(tagName, "cancelTriggerEvent()")
        // Acquire the reading status.
        if (mScanManager != null && mScanManager!!.isReading) {
            // Stop reading.
            mScanManager!!.stopRead()
        }
    }

    fun onPause() {
        Log.v(tagName, "onPause() - lockScanner()")
        lookScanner()
    }

    private fun lookScanner() {
        if (mScanManager != null && mScanManager!!.isEnabled) {
            //lock scanner
            mScanManager?.lockScanner()
        }
    }

    fun onResume() {
        Log.v(tagName, "onResume() - unlockScanner()")
        unlockScanner()
    }

    private fun unlockScanner() {
        if (mScanManager != null && mScanManager!!.isEnabled) {
            //claim barcode reader
            mScanManager?.unlockScanner()
        }
    }

    fun onDestroy() {
        Log.v(tagName, "onDestroy()")
        if (mScanManager != null) {
            mScanManager!!.removeDataListener(this)
            mScanManager!!.releaseScanManager()
        }
    }

    private fun initScanner() {
        Log.v(tagName, "initScanner()")
        if (mScanManager != null) {
            // Define a variable to store the code type.
            val codeType = CodeType()
            // Acquire the current setting values.
            var status = mScanManager!!.getConfig(codeType)
            if (status == SdkStatus.SUCCESS) {
                Log.v(tagName, "getConfig status: $status")
            }
            // Change the setting values.
            // Disable JAN code reading and enable QR code reading
            codeType.upcEanJan = false
            codeType.qrCode = true
            // Apply the setting values.
            status = mScanManager!!.setConfig(codeType)
            if (status == SdkStatus.SUCCESS) {
                Log.v(tagName, "setConfig status: $status")
            }
        }
    }

    // Create a read event.
    override fun onDataReceived(decodeResult: DecodeResult?) {
        Log.v(tagName, "onDataReceived()")
        // Acquire the reading result.
        val result: DecodeResult.Result = decodeResult!!.result
        Log.v(tagName, "result: $result")

        when (result) {
            DecodeResult.Result.SUCCESS -> {
                // Acquire the read code type.
                val data: String = decodeResult!!.data

                Log.v(tagName, "readData: $data")
                api.onHandleScanSuccess(data) {}

            }
            DecodeResult.Result.WARNING -> {
                api.onHandleScanFailure("WARNING") {}

            }
            DecodeResult.Result.TIMEOUT -> {
                api.onHandleScanFailure("TIMEOUT") {}

            }
            DecodeResult.Result.CANCELED -> {
                api.onHandleScanFailure("CANCELED") {}

            }
            DecodeResult.Result.FAILED -> {
                api.onHandleScanFailure("FAILED") {}

            }
        }
        //finally cancel the trigger event
        cancelTriggerEvent()
        onDestroy()
    }
}