package com.credibanco.dummy_demo_ingenico.helper

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import com.usdk.apiservice.aidl.UDeviceService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class UDeviceServiceManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private var deviceService: UDeviceService? = null

    fun bindService(onServiceBound: (UDeviceService?) -> Unit) {
        val intent = Intent("com.usdk.apiservice").apply {
            setPackage("com.usdk.apiservice")
        }

        context.bindService(intent, object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
                deviceService = UDeviceService.Stub.asInterface(binder)

                try {
                    deviceService?.register(null, Binder())
                    Log.d("TAG-1.UDeviceServiceManager", "Registered in UDeviceService | Register OK!")
                    println("Registro exitoso en UDeviceService.")
                } catch (e: RemoteException) {
                    e.printStackTrace()
                    Log.d("TAG-1.UDeviceServiceManager", "Error | Register BAD!")
                    println("Error al registrar en UDeviceService: ${e.message}")
                }

                onServiceBound(deviceService)
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                deviceService = null
            }
        }, Context.BIND_AUTO_CREATE)
    }

    fun getDeviceService(): UDeviceService? = deviceService
}