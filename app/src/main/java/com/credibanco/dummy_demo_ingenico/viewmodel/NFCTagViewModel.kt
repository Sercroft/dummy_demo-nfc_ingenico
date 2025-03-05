package com.credibanco.dummy_demo_ingenico.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.credibanco.dummy_demo_ingenico.usecase.NFCTagUseCase
import com.credibanco.dummy_demo_ingenico.util.KernelAvailableStateCallBackObjectSDK
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class NFCTagReaderViewModel @Inject constructor(
    private val nfcTagUseCase: NFCTagUseCase
) : ViewModel() {
    val responseNfcTagReading = MutableLiveData<String>()

    fun isKernelRun() {
        viewModelScope.launch {
            withContext(viewModelScope.coroutineContext) {
                nfcTagUseCase.isKernelRun()

                Log.d("TAG-1.VIEWMODEL", "isKernelRun!")
            }
        }
    }

    fun readNfcTag(isRequiredUID: Boolean?){
        Log.d("TAG-1.VIEWMODEL", "Before enter viewModelScope.launch in readNfcTag | Starting NFC Tag scan")
        viewModelScope.launch {
            nfcTagUseCase.isKernelAvailableState {
                when (it) {
                    is KernelAvailableStateCallBackObjectSDK.AvailableKernel -> {
                        viewModelScope.launch {
                            val response = nfcTagUseCase.invoke(isRequiredUID)
                            Log.d("TAG-1.VIEWMODEL", "Enter viewModelScope.launch in readNfcTag")

                            responseNfcTagReading.value = response
                        }
                    }
                }
            }
        }

        Log.d("TAG-1.VIEWMODEL", "Before enter viewModelScope.launch in readNfcTag")
    }
}