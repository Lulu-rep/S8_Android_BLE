package fr.isen.repplinger.androidsmartdevice.views

import android.content.Intent
import android.os.Build
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.repplinger.androidsmartdevice.DeviceDetailActivity
import fr.isen.repplinger.androidsmartdevice.R
import fr.isen.repplinger.androidsmartdevice.models.Device

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun ScanScreen(
    modifier: Modifier = Modifier,
    isScanning: Boolean,
    devices: List<Device>,
    showUnknownDevices: Boolean,
    onShowUnknownDevicesChange: (Boolean) -> Unit,
    onScanButtonClick: () -> Unit
) {
    val context = LocalContext.current
    Scaffold { contentPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(contentPadding)
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
                        onCheckedChange = onShowUnknownDevicesChange
                    )
                    Text("Show Unknown Devices", modifier = Modifier.padding(start = 8.dp))
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = onScanButtonClick,
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
                                    val intent =
                                        Intent(context, DeviceDetailActivity::class.java).apply {
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
}