package com.credibanco.dummy_demo_ingenico.datasource

import com.credibanco.dummy_demo_ingenico.util.KernelAvailableStateCallBackObjectSDK

interface NFCTagDataSource {
    suspend fun invoke(isRequiredUID: Boolean?) : String

    suspend fun isKernelRun(): Boolean

    fun isKernelAvailableState(kernelAvailableCallback: (callbackObjectType: KernelAvailableStateCallBackObjectSDK) -> Unit)
}