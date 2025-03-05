package com.credibanco.dummy_demo_ingenico.datasource.impl

import android.os.IBinder
import android.os.RemoteException
import com.credibanco.dummy_demo_ingenico.datasource.GetDeviceManagerDataSource
import com.usdk.apiservice.aidl.UDeviceService
import com.usdk.apiservice.aidl.device.UDeviceManager
import javax.inject.Inject

class GetDeviceManagerDataSourceImpl @Inject constructor(
    private val deviceService: UDeviceService
) : GetDeviceManagerDataSource {

    @Throws(Exception::class)
    override fun getDeviceManager(): UDeviceManager? {
        val iBinder = object : IBinderCreator() {
            @Throws(RemoteException::class)
            override fun create(): IBinder {
                return deviceService.deviceManager!!
            }
        }.start()
        return UDeviceManager.Stub.asInterface(iBinder)
    }

    internal abstract class IBinderCreator {
        @Throws(java.lang.IllegalStateException::class)
        fun start(): IBinder {
            return create()
        }

        @Throws(RemoteException::class)
        abstract fun create(): IBinder
    }
}