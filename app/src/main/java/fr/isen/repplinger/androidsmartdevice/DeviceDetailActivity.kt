package fr.isen.repplinger.androidsmartdevice

import fr.isen.repplinger.androidsmartdevice.views.DeviceDetailScreen
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import fr.isen.repplinger.androidsmartdevice.services.BleInstance
import fr.isen.repplinger.androidsmartdevice.ui.theme.AndroidSmartDeviceTheme

class DeviceDetailActivity : ComponentActivity() {
    private var isConnecting = mutableStateOf(true)
    private var led1Status = mutableStateOf(false)
    private var led2Status = mutableStateOf(false)
    private var led3Status = mutableStateOf(false)
    private var b1Notif = mutableStateOf(false)
    private var b3Notif = mutableStateOf(false)
    private var b1Clicks = mutableStateOf("")
    private var b3Clicks = mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidSmartDeviceTheme {
                val context = LocalContext.current
                val deviceName = intent.getStringExtra("DEVICE_NAME")
                val deviceAddress = intent.getStringExtra("DEVICE_ADDRESS")

                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    BleInstance.instance.connectToDevice(context, deviceAddress.toString()) { gatt ->
                        isConnecting.value = false
                        BleInstance.instance.onCharacteristicChangedCallback = { characteristic ->
                            val value = characteristic.value
                            Log.d("BLE", "Characteristic changed: ${value.joinToString()}")
                            Log.d("BLE", "Characteristic UUID: ${characteristic.uuid}")
                            when (characteristic.uuid) {
                                BleInstance.instance.bluetoothGatt?.services?.get(3)?.characteristics?.get(0)?.uuid -> {
                                    b1Clicks.value = value.joinToString()
                                }

                                BleInstance.instance.bluetoothGatt?.services?.get(2)?.characteristics?.get(1)?.uuid -> {
                                    b3Clicks.value = value.joinToString()
                                }
                            }
                        }
                    }
                }

                DeviceDetailScreen(
                    deviceName = deviceName,
                    deviceAddress = deviceAddress,
                    isConnecting = isConnecting.value,
                    led1Status = led1Status.value,
                    led2Status = led2Status.value,
                    led3Status = led3Status.value,
                    b1Notif = b1Notif.value,
                    b3Notif = b3Notif.value,
                    b1Clicks = b1Clicks.value,
                    b3Clicks = b3Clicks.value,
                    onToggleLed1 = {
                        led1Status.value = !led1Status.value
                        BleInstance.instance.toggleLed(0x01, led1Status.value)
                    },
                    onToggleLed2 = {
                        led2Status.value = !led2Status.value
                        BleInstance.instance.toggleLed(0x02, led2Status.value)
                    },
                    onToggleLed3 = {
                        led3Status.value = !led3Status.value
                        BleInstance.instance.toggleLed(0x03, led3Status.value)
                    },
                    onB1NotifChange = { isChecked ->
                        b1Notif.value = isChecked
                        BleInstance.instance.setCharacteristicNotification(3, 0, isChecked)
                    },
                    onB3NotifChange = { isChecked ->
                        b3Notif.value = isChecked
                        BleInstance.instance.setCharacteristicNotification(2, 1, isChecked)
                    }
                )
            }
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun onDestroy() {
        super.onDestroy()
        BleInstance.instance.disconnectDevice()
    }
}