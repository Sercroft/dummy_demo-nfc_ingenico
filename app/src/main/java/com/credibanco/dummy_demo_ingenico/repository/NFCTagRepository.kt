package com.credibanco.dummy_demo_ingenico.repository

import com.credibanco.dummy_demo_ingenico.util.KernelAvailableStateCallBackObjectSDK

interface NFCTagRepository {
    suspend fun invoke(isRequiredUID: Boolean?): String

    suspend fun isKernelRun(): Boolean

    fun isKernelAvailableState(kernelAvailableCallback: (callbackObjectType: KernelAvailableStateCallBackObjectSDK) -> Unit)
}