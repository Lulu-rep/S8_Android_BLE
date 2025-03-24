package fr.isen.repplinger.androidsmartdevice.services

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresPermission
import fr.isen.repplinger.androidsmartdevice.models.Device

class BleService {
    private var bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var isScanning = false
    private lateinit var scanCallback: ScanCallback
    fun BleInitError(context : Context): Boolean {
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            Log.e("BleService", "Bluetooth not supported on this device")
            Toast.makeText(context, "Bluetooth not supported on this device", Toast.LENGTH_LONG).show()
            return false
        }

        if (!bluetoothAdapter.isEnabled) {
            Log.e("BleService", "Bluetooth is not enabled")
            Toast.makeText(context, "Bluetooth not enabled on this device", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun startScan(context: Context, onDeviceFound: (Device) -> Unit, onScanStopped: () -> Unit) {
        if (isScanning) return
        if (!BleInitError(context)) return

        scanCallback = object : ScanCallback() {
            @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                super.onScanResult(callbackType, result)
                val device = Device(result.device.name ?: "Unknown", result.device.address)
                onDeviceFound(device)
            }

            @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
            override fun onBatchScanResults(results: List<ScanResult>) {
                super.onBatchScanResults(results)
                for (result in results) {
                    val device = Device(result.device.name ?: "Unknown", result.device.address)
                    onDeviceFound(device)
                }
            }

            override fun onScanFailed(errorCode: Int) {
                super.onScanFailed(errorCode)
                Log.e("BleService", "Scan failed with error: $errorCode")
            }
        }

        val bluetoothLeScanner = bluetoothAdapter?.bluetoothLeScanner
        bluetoothLeScanner?.startScan(scanCallback)
        isScanning = true
        Log.d("BleService", "Started BLE scan")

        // Stop scan after a predefined scan period.
        Handler().postDelayed({
            stopScan {
                onScanStopped()
            }
        }, SCAN_PERIOD)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun stopScan(onScanStopped: () -> Unit) {
        if (!isScanning) return

        val bluetoothLeScanner = bluetoothAdapter?.bluetoothLeScanner
        bluetoothLeScanner?.stopScan(scanCallback)
        isScanning = false
        Log.d("BleService", "Stopped BLE scan")
        onScanStopped()
    }

    companion object {
        private const val SCAN_PERIOD: Long = 10000 // 10 seconds
    }
}