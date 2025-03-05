package com.credibanco.dummy_demo_ingenico.datasource.impl

import android.os.IBinder
import android.os.RemoteException
import com.credibanco.dummy_demo_ingenico.datasource.PeripheralManagementDataSource
import com.credibanco.dummy_demo_ingenico.dto.PeripheralDTO
import com.credibanco.dummy_demo_ingenico.helper.UDeviceServiceManager
import com.credibanco.dummy_demo_ingenico.util.Constants.DX4000
import com.usdk.apiservice.aidl.device.UDeviceManager
import javax.inject.Inject

class PeripheralManagementDataSourceImpl @Inject constructor(
    private val deviceServiceManager: UDeviceServiceManager
) : PeripheralManagementDataSource {

    private fun getDeviceManager(onReady: (UDeviceManager?) -> Unit) {
        deviceServiceManager.bindService { deviceService ->
            if (deviceService == null) {
                throw IllegalStateException("Error al conectar con UDeviceService.")
            }

            val iBinder = object : IBinderCreator() {
                @Throws(RemoteException::class)
                override fun create(): IBinder {
                    return deviceService.deviceManager
                }
            }.start()

            onReady(UDeviceManager.Stub.asInterface(iBinder))
        }
    }

    internal abstract class IBinderCreator {
        @Throws(java.lang.IllegalStateException::class)
        fun start(): IBinder {
            return create()
        }

        @Throws(RemoteException::class)
        abstract fun create(): IBinder
    }

    override suspend fun invoke(): PeripheralDTO {
        var peripheralDTO = PeripheralDTO(DX4000)

        getDeviceManager { manager ->
            val modelDevice = manager?.deviceInfo?.model

            when (modelDevice) {
                DX4000 -> {
                    peripheralDTO = peripheralDTO.apply {
                        model       = modelDevice
                        flash       = true
                        band        = true
                        camera      = false
                        frontCamera = false
                        printer     = true
                        chip        = true
                        nfc         = true
                    }
                }
            }
        }

        return peripheralDTO
    }
}