package fr.isen.repplinger.androidsmartdevice.views

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import fr.isen.repplinger.androidsmartdevice.ScanActivity
import fr.isen.repplinger.androidsmartdevice.models.Device
import fr.isen.repplinger.androidsmartdevice.services.BleService

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun ScanScreen(modifier: Modifier) {
    val context = LocalContext.current
    var isScanning by remember { mutableStateOf(false) }
    var devices = remember { mutableStateListOf<Device>() }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text("Scan Activity", modifier = Modifier.padding(16.dp), fontSize = 24.sp)
        Icon(
            if(isScanning) Icons.Default.Clear else Icons.Default.Add,
            contentDescription = if (isScanning) "Stop Scan" else "Start Scan",
            modifier = Modifier
                .size(64.dp)
                .clickable {
                    if (BleService().BleInitError(context = context)) {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                            BleService().startScan(context) { device ->
                                devices.add(device)
                            }
                        } else {
                            Toast.makeText(context, "Permissions are required to scan for devices", Toast.LENGTH_LONG).show()
                        }

                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
                            BleService().stopScan()
                        } else {
                            Toast.makeText(context, "Permissions are required to scan for devices", Toast.LENGTH_LONG).show()
                        }
                    }
                }
                .align(Alignment.CenterHorizontally)
                .padding(16.dp)
        )
        Text(
            text = if (isScanning) "Scanning for devices..." else "Not scanning",
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(8.dp),
            fontSize = 16.sp
        )
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(devices) { device ->
                Text(text = device.name, fontSize = 16.sp)
            }
        }
    }
}