package fr.isen.repplinger.androidsmartdevice.services

object BleInstance {
    val instance: BleService by lazy {
        BleService()
    }
}