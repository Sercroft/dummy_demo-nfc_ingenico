package com.credibanco.dummy_demo_ingenico.usecase.impl

import com.credibanco.dummy_demo_ingenico.repository.NFCTagRepository
import com.credibanco.dummy_demo_ingenico.usecase.NFCTagUseCase
import com.credibanco.dummy_demo_ingenico.util.KernelAvailableStateCallBackObjectSDK
import javax.inject.Inject

class NFCTagUseCaseImpl @Inject constructor(
    private val nfcTagRepository: NFCTagRepository
) : NFCTagUseCase {

    override suspend fun invoke(isRequiredUID: Boolean?): String {
        return nfcTagRepository.invoke(isRequiredUID)
    }

    override suspend fun isKernelRun(): Boolean {
        return nfcTagRepository.isKernelRun()
    }
    override fun isKernelAvailableState(kernelAvailableCallback: (callbackObjectType: KernelAvailableStateCallBackObjectSDK) -> Unit) {
        return nfcTagRepository.isKernelAvailableState(kernelAvailableCallback)
    }
}