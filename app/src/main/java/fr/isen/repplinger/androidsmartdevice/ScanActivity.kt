package fr.isen.repplinger.androidsmartdevice

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import fr.isen.repplinger.androidsmartdevice.models.Device
import fr.isen.repplinger.androidsmartdevice.services.BleInstance
import fr.isen.repplinger.androidsmartdevice.ui.theme.AndroidSmartDeviceTheme
import fr.isen.repplinger.androidsmartdevice.views.ScanScreen

class ScanActivity: ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.S)
    private val requestPermissionLauncher = registerForActivityResult(RequestMultiplePermissions()) { permissions ->
        val granted = permissions.entries.all { it.value }
        if (granted) {
            checkAndRequestPermissions()
        } else {
            Toast.makeText(this, "Permissions are required to scan for devices", Toast.LENGTH_LONG).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidSmartDeviceTheme {
                val context = this
                var isScanning by remember { mutableStateOf(false) }
                var devices = remember { mutableStateListOf<Device>() }
                var showUnknownDevices by remember { mutableStateOf(true) }
                val deviceAddresses = remember { mutableSetOf<String>() }

                ScanScreen(
                    isScanning = isScanning,
                    devices = devices,
                    showUnknownDevices = showUnknownDevices,
                    onShowUnknownDevicesChange = { showUnknownDevices = it },
                    onScanButtonClick = {
                        if (BleInstance.instance.bleInitError(context = context)) {
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                                ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                                if (isScanning) {
                                    BleInstance.instance.stopScan {
                                        isScanning = false
                                    }
                                } else {
                                    devices.clear()
                                    deviceAddresses.clear()
                                    BleInstance.instance.startScan(context, { device ->
                                        if (deviceAddresses.add(device.address)) {
                                            devices.add(device)
                                        }
                                    }) {
                                        isScanning = false
                                    }
                                    isScanning = true
                                }
                            } else {
                                Toast.makeText(context, "Permissions are required to scan for devices", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                )
            }
        }
        checkAndRequestPermissions()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun checkAndRequestPermissions() {
        val permissions = mutableListOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
        } else{
            checkAndEnableBluetooth()
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun checkAndEnableBluetooth() {
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not supported on this device", Toast.LENGTH_SHORT).show()
            return
        }
        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }
    }

    companion object {
        private const val REQUEST_ENABLE_BT = 1
    }
}