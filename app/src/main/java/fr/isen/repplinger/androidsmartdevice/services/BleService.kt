package fr.isen.repplinger.androidsmartdevice.services

import android.Manifest
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import fr.isen.repplinger.androidsmartdevice.models.Device
import java.util.UUID
import kotlin.collections.get
import kotlin.text.compareTo

class BleService {
    private var bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    internal var bluetoothGatt: BluetoothGatt? = null
    private var isScanning = false
    private lateinit var scanCallback: ScanCallback
    var services: List<BluetoothGattService> = listOf()
    var onCharacteristicChangedCallback: ((BluetoothGattCharacteristic) -> Unit)? = null

    fun bleInitError(context: Context): Boolean {
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
        if (!bleInitError(context)) return

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

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun connectToDevice(context: Context, deviceAddress: String, onConnected: (BluetoothGatt) -> Unit) {
        val device: BluetoothDevice? = bluetoothAdapter?.getRemoteDevice(deviceAddress)
        if (device == null) {
            Log.e("BleService", "Device not found. Unable to connect.")
            return
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            if (context.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                Log.e("BleService", "BLUETOOTH_CONNECT permission not granted.")
                return
            }
        } else {
            if (context.checkSelfPermission(Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
                context.checkSelfPermission(Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                Log.e("BleService", "BLUETOOTH or BLUETOOTH_ADMIN permission not granted.")
                return
            }
        }

        bluetoothGatt = device.connectGatt(context, false, object : BluetoothGattCallback() {
            @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                if (newState == BluetoothGatt.STATE_CONNECTED) {
                    Log.d("BleService", "Connected to GATT server.")
                    gatt.discoverServices()
                } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                    Log.d("BleService", "Disconnected from GATT server.")
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Log.d("BleService", "Service discovery completed.")
                    services = gatt.services
                    onConnected(gatt)
                } else {
                    Log.e("BleService", "Service discovery failed with status: $status")
                }
            }

            override fun onCharacteristicChanged(
                gatt: BluetoothGatt,
                characteristic: BluetoothGattCharacteristic
            ) {
                val value = characteristic.value
                Log.i("BLEService", "Characteristic changed: ${value.joinToString()}")
                onCharacteristicChangedCallback?.invoke(characteristic)
            }
        })
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun toggleLed(ledNumber: Byte, turnOn: Boolean) {
        val characteristic = services.getOrNull(2)?.characteristics?.getOrNull(0)
        if (characteristic == null) {
            Log.e("BleService", "Characteristic not found!")
            return
        }

        characteristic.value = if (turnOn) byteArrayOf(ledNumber) else byteArrayOf(0x00)
        val success = bluetoothGatt?.writeCharacteristic(characteristic) == true
        if (success) {
            Log.d("BleService", "Characteristic written successfully: ${characteristic.value}")
        } else {
            Log.e("BleService", "Failed to write characteristic")
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun disconnectDevice() {
        bluetoothGatt?.disconnect()
        bluetoothGatt?.close()
        bluetoothGatt = null
        Log.d("BleService", "Disconnected from GATT server.")
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun setCharacteristicNotification(
        serviceIndex: Int,
        characteristicIndex: Int,
        enable: Boolean
    ) {
        val gatt = bluetoothGatt
        if (gatt == null || serviceIndex >= gatt.services.size || characteristicIndex >= gatt.services[serviceIndex].characteristics.size) {
            Log.e("BleService", "Invalid service or characteristic index")
            return
        }
        val characteristic = gatt.services[serviceIndex]?.characteristics[characteristicIndex]
        gatt.setCharacteristicNotification(characteristic, enable)

        val descriptor = characteristic?.getDescriptor(
            UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
        )
        val value =
            if (enable) BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE else BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
        descriptor?.setValue(value)
        gatt.writeDescriptor(descriptor)
    }

    fun checkPermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val scanPermission = context.checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED
            val connectPermission = context.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
            val fineLocationPermission = context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            val backgroundLocation = context.checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED

            Log.d("BleService", "BLUETOOTH_SCAN permission granted: $scanPermission")
            Log.d("BleService", "BLUETOOTH_CONNECT permission granted: $connectPermission")
            Log.d("BleService", "ACCESS_FINE_LOCATION permission granted: $fineLocationPermission")
            Log.d("BleService", "ACCESS_BACKGROUND_LOCATION permission granted: $backgroundLocation")

            return scanPermission && connectPermission && fineLocationPermission
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val bluetoothPermission = context.checkSelfPermission(Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED
            val bluetoothAdminPermission = context.checkSelfPermission(Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED
            val fineLocationPermission = context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            val backgroundLocation = context.checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED

            Log.d("BleService", "BLUETOOTH permission granted: $bluetoothPermission")
            Log.d("BleService", "BLUETOOTH_ADMIN permission granted: $bluetoothAdminPermission")
            Log.d("BleService", "ACCESS_FINE_LOCATION permission granted: $fineLocationPermission")

            return bluetoothPermission && bluetoothAdminPermission && fineLocationPermission && backgroundLocation
        }
        else{
            val bluetoothPermission = context.checkSelfPermission(Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED
            val bluetoothAdminPermission = context.checkSelfPermission(Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED
            val fineLocationPermission = context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

            Log.d("BleService", "BLUETOOTH permission granted: $bluetoothPermission")
            Log.d("BleService", "BLUETOOTH_ADMIN permission granted: $bluetoothAdminPermission")
            Log.d("BleService", "ACCESS_FINE_LOCATION permission granted: $fineLocationPermission")

            return bluetoothPermission && bluetoothAdminPermission && fineLocationPermission
        }
    }


    companion object {
        private const val SCAN_PERIOD: Long = 10000
    }
}