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
fun DeviceDetailScreen(
    modifier: Modifier = Modifier,
    deviceName: String?,
    deviceAddress: String?,
    isConnecting: Boolean,
    led1Status: Boolean,
    led2Status: Boolean,
    led3Status: Boolean,
    b1Notif: Boolean,
    b3Notif: Boolean,
    b1Clicks: String,
    b3Clicks: String,
    onToggleLed1: () -> Unit,
    onToggleLed2: () -> Unit,
    onToggleLed3: () -> Unit,
    onB1NotifChange: (Boolean) -> Unit,
    onB3NotifChange: (Boolean) -> Unit
) {
    Scaffold { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
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
                        onClick = onToggleLed1,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "Toggle LED 1 (Current: ${if (led1Status) "On" else "Off"})",
                            color = Color.White
                        )
                    }
                    Button(
                        onClick = onToggleLed2,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "Toggle LED 2 (Current: ${if (led2Status) "On" else "Off"})",
                            color = Color.White
                        )
                    }
                    Button(
                        onClick = onToggleLed3,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "Toggle LED 3 (Current: ${if (led3Status) "On" else "Off"})",
                            color = Color.White
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Text("Subscribe to Button 1")
                        Checkbox(
                            checked = b1Notif,
                            onCheckedChange = onB1NotifChange,
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
                            onCheckedChange = onB3NotifChange
                        )
                    }
                    if (b3Notif) {
                        Text("Button 3 value: $b3Clicks")
                    }
                }
            }
        }
    }
}