package com.credibanco.dummy_demo_ingenico.di

import android.content.Context
import com.credibanco.dummy_demo_ingenico.datasource.GetDeviceManagerDataSource
import com.credibanco.dummy_demo_ingenico.datasource.PeripheralManagementDataSource
import com.credibanco.dummy_demo_ingenico.datasource.impl.GetDeviceManagerDataSourceImpl
import com.credibanco.dummy_demo_ingenico.datasource.impl.PeripheralManagementDataSourceImpl
import com.credibanco.dummy_demo_ingenico.helper.UDeviceServiceManager
import com.usdk.apiservice.aidl.UDeviceService
import com.usdk.apiservice.aidl.device.UDeviceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DeviceEngineModule {

    @Provides
    @Singleton
    fun provideUDeviceServiceManager(@ApplicationContext context: Context): UDeviceServiceManager {
        return UDeviceServiceManager(context)
    }

    @Provides
    @Singleton
    fun provideUDeviceManager(deviceService: UDeviceService): UDeviceManager {
        return UDeviceManager.Stub.asInterface(deviceService.deviceManager)
    }

    @Singleton
    @Provides
    fun peripheralDataSourceProvider(deviceService: UDeviceServiceManager): PeripheralManagementDataSource =
        PeripheralManagementDataSourceImpl(deviceService)

    @Singleton
    @Provides
    fun provideGetDeviceManagerDataSource(deviceService: UDeviceService): GetDeviceManagerDataSource {
        return GetDeviceManagerDataSourceImpl(deviceService)
    }
}