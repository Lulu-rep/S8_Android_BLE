import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
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
fun DeviceDetailScreen(modifier: Modifier, deviceName: String?, deviceAddress: String?) {
    val context = LocalContext.current
    var isConnecting by remember { mutableStateOf(true) }
    var led1Status by remember { mutableStateOf(false) }
    var led2Status by remember { mutableStateOf(false) }
    var led3Status by remember { mutableStateOf(false) }
    var mainButtonClicks by remember { mutableIntStateOf(0) }
    var thirdButtonClicks by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            BleInstance.instance.connectToDevice(context, deviceAddress.toString()) {
                isConnecting = false
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
                Button(
                    onClick = { TODO() },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                ) {
                    Text(text = "Main Button Clicks: $mainButtonClicks", color = Color.White)
                }
                Button(
                    onClick = { TODO() },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                ) {
                    Text(text = "Third Button Clicks: $thirdButtonClicks", color = Color.White)
                }
            }
        }
    }
}