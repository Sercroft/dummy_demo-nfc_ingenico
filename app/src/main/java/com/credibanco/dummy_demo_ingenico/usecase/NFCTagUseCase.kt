package com.credibanco.dummy_demo_ingenico.usecase

import com.credibanco.dummy_demo_ingenico.util.KernelAvailableStateCallBackObjectSDK

interface NFCTagUseCase {
    suspend operator fun invoke(isRequiredUID: Boolean?): String

    suspend fun isKernelRun(): Boolean

    fun isKernelAvailableState(kernelAvailableCallback: (callbackObjectType: KernelAvailableStateCallBackObjectSDK) -> Unit)
}