package fr.isen.repplinger.androidsmartdevice.views

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import fr.isen.repplinger.androidsmartdevice.R
import fr.isen.repplinger.androidsmartdevice.services.BleInstance

@Composable
fun DeviceDetailScreen(modifier: Modifier = Modifier, deviceName: String?, deviceAddress: String?) {
    val context = LocalContext.current
    var isConnecting by remember { mutableStateOf(true) }
    var led1Status by remember { mutableStateOf(false) }
    var led2Status by remember { mutableStateOf(false) }
    var led3Status by remember { mutableStateOf(false) }
    var b1Notif by remember { mutableStateOf(false) }
    var b3Notif by remember { mutableStateOf(false) }
    var b1Clicks by remember { mutableStateOf("") }
    var b3Clicks by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            BleInstance.instance.connectToDevice(context, deviceAddress.toString()) { gatt ->
                isConnecting = false
                BleInstance.instance.onCharacteristicChangedCallback = { characteristic ->
                    val value = characteristic.value
                    Log.d("BLE", "Characteristic changed: ${value.joinToString()}")
                    Log.d("BLE", "Characteristic UUID: ${characteristic.uuid}")
                    when (characteristic.uuid) {
                        BleInstance.instance.bluetoothGatt?.services[3]?.characteristics?.get(0)?.uuid -> {
                            b1Clicks = value.joinToString()
                        }

                        BleInstance.instance.bluetoothGatt?.services[2]?.characteristics?.get(1)?.uuid -> {
                            b3Clicks = value.joinToString()
                        }
                    }
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.la_mere_patriev3),
                contentDescription = "Device Image",
                modifier = Modifier
                    .size(120.dp)
                    .padding(top = 32.dp)
            )
            Text(
                text = deviceName ?: "Unknown Device",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = "Address: ${deviceAddress ?: "N/A"}",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(16.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            if (isConnecting) {
                CircularProgressIndicator()
                Text(text = "Connecting to BLE device...")
            } else {
                Text(text = "Device Details")
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        led1Status = !led1Status
                        BleInstance.instance.toggleLed(0x01, led1Status)
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                ) {
                    Text(text = "Toggle LED 1 (Current: ${if (led1Status) "On" else "Off"})", color = Color.White)
                }
                Button(
                    onClick = {
                        led2Status = !led2Status
                        BleInstance.instance.toggleLed(0x02, led2Status)
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                ) {
                    Text(text = "Toggle LED 2 (Current: ${if (led2Status) "On" else "Off"})", color = Color.White)
                }
                Button(
                    onClick = {
                        led3Status = !led3Status
                        BleInstance.instance.toggleLed(0x03, led3Status)
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                ) {
                    Text(text = "Toggle LED 3 (Current: ${if (led3Status) "On" else "Off"})", color = Color.White)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text("Subscribe to Button 1")
                    Checkbox(
                        checked = b1Notif,
                        onCheckedChange = { isChecked ->
                            b1Notif = isChecked
                            if (isChecked) {
                                BleInstance.instance.setCharacteristicNotification(3, 0, true)
                            } else {
                                BleInstance.instance.setCharacteristicNotification(3, 0, false)
                            }
                        },
                    )
                }
                if (b1Notif) {
                    Text("Button 1 value: $b1Clicks")
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text("Subscribe to Button 3")
                    Checkbox(
                        checked = b3Notif,
                        onCheckedChange = { isChecked ->
                            b3Notif = isChecked
                            if (isChecked) {
                                BleInstance.instance.setCharacteristicNotification(2, 1, true)
                            } else {
                                BleInstance.instance.setCharacteristicNotification(2, 1, false)
                            }
                        }
                    )
                }
                if (b3Notif) {
                    Text("Button 3 value: $b3Clicks")
                }
            }
        }
    }
}