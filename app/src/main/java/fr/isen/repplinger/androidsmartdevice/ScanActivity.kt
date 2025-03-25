package fr.isen.repplinger.androidsmartdevice

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
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
                            if (BleInstance.instance.checkPermission(context)) {
                                if (BleInstance.instance.bleInitError(context)) {
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
                                }
                            } else {
                                Toast.makeText(context, "Permissions are required to scan for devices", Toast.LENGTH_LONG).show()
                            }

                    }
                )
            }
        }
        BleInstance.instance.checkPermission(context = this)
    }
}