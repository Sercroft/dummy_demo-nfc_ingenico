package com.credibanco.dummy_demo_ingenico.repository.impl

import com.credibanco.dummy_demo_ingenico.datasource.NFCTagDataSource
import com.credibanco.dummy_demo_ingenico.repository.NFCTagRepository
import com.credibanco.dummy_demo_ingenico.util.KernelAvailableStateCallBackObjectSDK
import javax.inject.Inject

class NFCTagRepositoryImpl @Inject constructor(
    private val nfcTagDataSource: NFCTagDataSource
) : NFCTagRepository {

    override suspend fun invoke(isRequiredUID: Boolean?): String {
        return nfcTagDataSource.invoke(isRequiredUID)
    }


    override suspend fun isKernelRun(): Boolean {
        return nfcTagDataSource.isKernelRun()
    }

    override fun isKernelAvailableState(kernelAvailableCallback: (callbackObjectType: KernelAvailableStateCallBackObjectSDK) -> Unit) {
        return nfcTagDataSource.isKernelAvailableState(kernelAvailableCallback)
    }
}