package fr.isen.repplinger.androidsmartdevice.views

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.repplinger.androidsmartdevice.models.Device

@Composable
fun ScanScreen(modifier: Modifier) {
    var isScanning by remember { mutableStateOf(false) }
    val devices = remember { mutableStateListOf<Device>() }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text("Scan Activity", modifier = Modifier.padding(16.dp), fontSize = 24.sp)
        Icon(
            if(isScanning) Icons.Default.Clear else Icons.Default.Add,
            contentDescription = if (isScanning) "Stop Scan" else "Start Scan",
            modifier = Modifier
                .size(64.dp)
                .clickable {
                    isScanning = !isScanning
                    if (isScanning) {
                        //TODO: Start scanning logic
                    } else {
                        //TODO: Stop scanning logic
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