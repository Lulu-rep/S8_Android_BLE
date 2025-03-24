import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import fr.isen.repplinger.androidsmartdevice.services.BleInstance

@Composable
fun DeviceDetailScreen(modifier: Modifier, deviceName: String?, deviceAddress: String?) {
    val context = LocalContext.current
    var isConnecting by remember { mutableStateOf(true) }

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
        }
    }
}