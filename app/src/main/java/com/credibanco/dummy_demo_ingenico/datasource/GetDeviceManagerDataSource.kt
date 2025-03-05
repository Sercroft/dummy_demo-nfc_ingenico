package com.credibanco.dummy_demo_ingenico.datasource

import com.usdk.apiservice.aidl.device.UDeviceManager

interface GetDeviceManagerDataSource {
    @Throws(Exception::class)
    fun getDeviceManager(): UDeviceManager?
}
