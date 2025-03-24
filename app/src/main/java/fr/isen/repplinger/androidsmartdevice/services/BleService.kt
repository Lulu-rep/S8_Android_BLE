package fr.isen.repplinger.androidsmartdevice.services

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

class BleService {
    fun BleInitError(context : Context): Boolean {
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            Log.e("BleService", "Bluetooth not supported on this device")
            Toast.makeText(context, "Bluetooth not supported on this device", Toast.LENGTH_LONG).show()
            return false
        }

        if (!bluetoothAdapter.isEnabled) {
            Log.e("BleService", "Bluetooth is not enabled")
            Toast.makeText(context, "Bluetooth not enabled on this device", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }
}