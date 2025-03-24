package fr.isen.repplinger.androidsmartdevice.views

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import fr.isen.repplinger.androidsmartdevice.DeviceDetailActivity
import fr.isen.repplinger.androidsmartdevice.R
import fr.isen.repplinger.androidsmartdevice.models.Device
import fr.isen.repplinger.androidsmartdevice.services.BleInstance

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun ScanScreen(modifier: Modifier) {
    val context = LocalContext.current
    var isScanning by remember { mutableStateOf(false) }
    var devices = remember { mutableStateListOf<Device>() }
    var showUnknownDevices by remember { mutableStateOf(true) }
    val deviceAddresses = remember { mutableSetOf<String>() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.la_mere_patriev3),
                contentDescription = "La mère patrie",
                modifier = Modifier
                    .size(120.dp)
                    .padding(top = 32.dp)
            )
            Text(
                text = "Scan for BLE Devices",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = showUnknownDevices,
                    onCheckedChange = { showUnknownDevices = it }
                )
                Text("Show Unknown Devices", modifier = Modifier.padding(start = 8.dp))
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    if (BleInstance.instance.BleInitError(context = context)) {
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
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(if (isScanning) "Stop Scan" else "Start Scan", color = Color.White)
            }
            Text(
                text = if (isScanning) "Scanning for devices..." else "Not scanning",
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(8.dp),
                fontSize = 16.sp
            )
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(devices.filter { showUnknownDevices || it.name != "Unknown" }) { device ->
                    Text(
                        text = "${device.name} - ${device.address}",
                        fontSize = 16.sp,
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable {
                                val intent = Intent(context, DeviceDetailActivity::class.java).apply {
                                    putExtra("DEVICE_NAME", device.name)
                                    putExtra("DEVICE_ADDRESS", device.address)
                                }
                                context.startActivity(intent)
                            }
                    )
                }
            }
        }
    }
}