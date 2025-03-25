package fr.isen.repplinger.androidsmartdevice

import fr.isen.repplinger.androidsmartdevice.views.DeviceDetailScreen
import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import fr.isen.repplinger.androidsmartdevice.services.BleInstance
import fr.isen.repplinger.androidsmartdevice.ui.theme.AndroidSmartDeviceTheme

class DeviceDetailActivity: ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidSmartDeviceTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val deviceName = intent.getStringExtra("DEVICE_NAME")
                    val deviceAddress = intent.getStringExtra("DEVICE_ADDRESS")
                    DeviceDetailScreen(modifier = Modifier.padding(innerPadding), deviceName, deviceAddress)
                }
            }
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun onDestroy() {
        super.onDestroy()
        BleInstance.instance.disconnectDevice()
    }

}