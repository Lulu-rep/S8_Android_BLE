import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import fr.isen.repplinger.androidsmartdevice.services.BleInstance

@Composable
fun DeviceDetailScreen(modifier: Modifier, deviceName: String?, deviceAddress: String?) {
    val context = LocalContext.current
    var isConnecting by remember { mutableStateOf(true) }
    var led1Status by remember { mutableStateOf(false) }
    var led2Status by remember { mutableStateOf(false) }
    var led3Status by remember { mutableStateOf(false) }
    var mainButtonClicks by remember { mutableStateOf(0) }
    var thirdButtonClicks by remember { mutableStateOf(0) }

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

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        if (isConnecting) {
            CircularProgressIndicator()
            Text(text = "Connexion BLE en cours...")
        } else {
            Text(text = "Détails du périphérique")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                led1Status = !led1Status
                BleInstance.instance.toggleLed(0x01, led1Status)
            }) {
                Text(text = "Toggle LED 1 (Current: ${if (led1Status) "On" else "Off"})")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                led2Status = !led2Status
                BleInstance.instance.toggleLed(0x02, led2Status)
            }) {
                Text(text = "Toggle LED 2 (Current: ${if (led2Status) "On" else "Off"})")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                led3Status = !led3Status
                BleInstance.instance.toggleLed(0x03, led3Status)
            }) {
                Text(text = "Toggle LED 3 (Current: ${if (led3Status) "On" else "Off"})")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { mainButtonClicks = getMainButtonClicks() }) {
                Text(text = "Main Button Clicks: $mainButtonClicks")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { thirdButtonClicks = getThirdButtonClicks() }) {
                Text(text = "Third Button Clicks: $thirdButtonClicks")
            }
        }
    }
}
fun getMainButtonClicks(): Int {
    // Code to get main button clicks
    return 0
}

fun getThirdButtonClicks(): Int {
    // Code to get third button clicks
    return 0
}