package com.credibanco.dummy_demo_ingenico.datasource.impl

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import com.credibanco.dummy_demo_ingenico.datasource.NFCTagDataSource
import com.credibanco.dummy_demo_ingenico.util.BytesUtil
import com.credibanco.dummy_demo_ingenico.util.CardOption
import com.credibanco.dummy_demo_ingenico.util.Constants.TAG
import com.credibanco.dummy_demo_ingenico.util.EMVOption
import com.credibanco.dummy_demo_ingenico.util.ErrorUtil
import com.credibanco.dummy_demo_ingenico.util.KernelAvailableStateCallBackObjectSDK
import com.usdk.apiservice.aidl.DeviceServiceData
import com.usdk.apiservice.aidl.UDeviceService
import com.usdk.apiservice.aidl.constants.RFDeviceName
import com.usdk.apiservice.aidl.data.BytesValue
import com.usdk.apiservice.aidl.emv.UEMV
import com.usdk.apiservice.aidl.rfreader.CardType
import com.usdk.apiservice.aidl.rfreader.KeyType
import com.usdk.apiservice.aidl.rfreader.OnPassListener
import com.usdk.apiservice.aidl.rfreader.PollMode.ULTRALIGHT
import com.usdk.apiservice.aidl.rfreader.URFReader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.CompletableFuture
import javax.inject.Inject

class NFCTagDataSourceImpl @Inject constructor(
    private val context: Context
):
    NFCTagDataSource,
    ServiceConnection
{

    private var kernelRunState = false
    private var kernelAvailableState = false
    private val MAX_RETRY_COUNT = 3
    private val RETRY_INTERVALS: Long = 3000
    private var retry = 0
    private lateinit var kernelAvailableCallback: (callbackObjectType: KernelAvailableStateCallBackObjectSDK) -> Unit
    private var isKernelFunctionCall: Boolean = false
    private var deviceService: UDeviceService? = null
    protected var emv: UEMV? = null
    protected var emvOption: EMVOption? = null
    protected var cardOption: CardOption? = null
    private var rfReader: URFReader? = null
    private var nfcResult: String = "Reading TAG NFC..."
    private var cardType = CardType.PRO_CARD
    private var isUID: Boolean? = null
    private var resultUID: String? = null

    init {
        kernelRunState = false
        emvOption = EMVOption.create()
        cardOption = CardOption.create()
        bindService()
    }

    private fun bindService(): Boolean {
        Log.d("TAG-1.TAG_READER", "bindService() - Binding to USDK service")

        if (kernelRunState) {
            return kernelRunState
        }
        val service = Intent("com.usdk.apiservice")
        service.setPackage("com.usdk.apiservice")
        kernelRunState = context.bindService(service, this, Context.BIND_AUTO_CREATE)

        // If the binding fails, it is rebinded
        if (!kernelRunState && retry++ < MAX_RETRY_COUNT) {
            Handler().postDelayed({ bindService() }, RETRY_INTERVALS)
        }
        return kernelRunState
    }

    @Throws(IllegalStateException::class)
    fun getEMV(): UEMV? {
        val iBinder: IBinder = object : IBinderCreator() {
            override fun create(): IBinder {
                return deviceService?.emv!!
            }
        }.start()
        return UEMV.Stub.asInterface(iBinder)
    }

    internal abstract class IBinderCreator {
        @Throws(java.lang.IllegalStateException::class)
        fun start(): IBinder {
            return create()
        }

        @Throws(RemoteException::class)
        abstract fun create(): IBinder
    }


    override suspend fun invoke(isRequiredUID: Boolean?): String {
        if (deviceService == null || !kernelRunState) {
            Log.e(TAG, "El servicio NFC no está disponible antes de leer. Intentando reconectar...")
            bindService()
            return "Error: deviceService no disponible"
        }

        rfReader = getRFCardReader()
        isUID = isRequiredUID

        val futureCardData = CompletableFuture<String>()
        val timeoutMillis = 5000L

        val timeoutJob = CoroutineScope(Dispatchers.IO).launch {
            delay(timeoutMillis)
            if (!futureCardData.isDone) {
                futureCardData.complete("No Data")
            }
        }

        rfReader?.pollCard(ULTRALIGHT, createOnPassListener(futureCardData))
        nfcResult = futureCardData.join()
        timeoutJob.cancel()

        Log.d(TAG, "nfcResult: $nfcResult")

        return nfcResult
    }

    private fun createOnPassListener(future: CompletableFuture<String>): OnPassListener {
        return object : OnPassListener.Stub() {
            @Throws(RemoteException::class)
            override fun onCardPass(cardtype: Int) {

                var returnData = "NoData"

                cardType = cardtype
                val dataOut = BytesValue()
                rfReader?.activate(cardtype, dataOut)

                if (isUID == true) {
                    resultUID = getUIDHex(dataOut)
                }
                returnData = if (cardtype == CardType.ULTRALIGHT_CARD){
                    getUltralight(rfReader)
                } else {
                    readAllBlocks(rfReader)
                }

                future.complete(returnData)
            }

            @Throws(RemoteException::class)
            override fun onFail(i: Int) {
                val message = ErrorUtil.getErrorDetail(i)
                Log.d(TAG, "Error message $message")
                future.complete("Error: $message")
            }
        }
    }

    fun getUIDHex(dataOut: BytesValue): String {
        val response = rfReader?.getCardSerialNo(dataOut.data)
        return BytesUtil.bytes2HexString(response)
    }

    override suspend fun isKernelRun(): Boolean {
        if (kernelRunState  && deviceService != null){
            return kernelRunState
        }else{
            //startkernel to do
            retry = 0
            return bindService()
        }
    }

    override fun isKernelAvailableState(kernelAvailableCallback: (callbackObjectType: KernelAvailableStateCallBackObjectSDK) -> Unit) {
        this.kernelAvailableCallback =  kernelAvailableCallback
        isKernelFunctionCall = true
        if(this.kernelRunState && this.kernelAvailableState) kernelAvailableCallback.invoke(
            KernelAvailableStateCallBackObjectSDK.AvailableKernel(
                KernelAvailableStateCallBackObjectSDK.AVAILABLE_KERNEL))
    }

    private fun getRFCardReader(): URFReader? {
        if (rfReader == null) {
            val param = Bundle().apply {
                putString(DeviceServiceData.RF_DEVICE_NAME, RFDeviceName.INNER)
            }

            rfReader = URFReader.Stub.asInterface(deviceService?.getRFReader(param))
        }

        Log.d("TAG-1", rfReader.toString())
        return rfReader
    }

    private fun getUltralight(
        rfReader: URFReader?
    ): String {
        var result = ""
        val sb = StringBuilder()
        var block = 0
        val totalBlocks = 16 * 4 // Ajusta según la capacidad de la tarjeta Ultralight

        try {

            // Leer bloques de datos utilizando rfReader
            while (block < totalBlocks) {
                val dataOut = BytesValue()
                val readResult = rfReader?.readBlock(block, dataOut)

                Log.d(TAG, "Read result: $readResult")

                if (readResult == 0) { // 0 indica éxito según la mayoría de los SDK
                    sb.append(BytesUtil.bytes2HexString(dataOut.data))
                } else {
                    Log.e("Read Error", "Fallo en la lectura del bloque $block")
                }
                block += 4 // Incrementar según el tamaño de los bloques
            }
        } catch (e: Exception) {
            Log.e("Error", "Error al procesar la tarjeta: ${e.message}")
        }

        val finalData = sb.toString()

        // Retornar UID + datos o solo los datos según el parámetro
        result = if (isUID == true) {
            "$resultUID;$finalData"
        } else {
            finalData
        }

        return result
    }

    private fun readAllBlocks(reader: URFReader?): String {
        var toReturn = ""
        var finalData = ""
        // step 4, block authority
        val authorityBlock = 4

        val blockData4 = authenticateAndReadBlock(reader, authorityBlock)

        if (blockData4 != null) {
            finalData = String(blockData4)
        } else {
            Log.d(TAG, "Fallo al leer el bloque $authorityBlock")
        }

        // step 5, block
        val stepBlock5 = 5

        val blockData5 = authenticateAndReadBlock(reader, stepBlock5)

        if (blockData5 != null) {
            finalData = finalData.plus(String(blockData5))
        } else {
            Log.d(TAG, "Fallo al leer el bloque $stepBlock5")
        }

        // step 6, block
        val stepBlock6 = 6

        val blockData6 = authenticateAndReadBlock(reader, stepBlock6)

        if (blockData6 != null) {
            finalData = finalData.plus(String(blockData6))
        } else {
            Log.d(TAG, "Fallo al leer el bloque $stepBlock6")
        }

        toReturn = if (isUID == true) {
            "$resultUID;$finalData"
        } else {
            finalData

        }

        return toReturn
    }

    private fun authenticateAndReadBlock(reader: URFReader?, blockNo: Int): ByteArray? {
        val keyType = KeyType.KEY_B // //keytype A or B

        val key = byteArrayOf(
            0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(),
            0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte()
        ) // Clave predeterminada para Mifare

        return try {
            // Paso 1: Autenticación del bloque
            val authResult = reader!!.authBlock(blockNo, keyType, key)
            if (authResult != 0) {
                println("Fallo en la autenticación del bloque $blockNo. Código de error: $authResult")
                return null
            }
            println("Autenticación exitosa para el bloque $blockNo")

            // Paso 2: Leer el bloque
            val dataOut = BytesValue() // El bloque debe contener 16 bytes
            val readResult = reader.readBlock(blockNo, dataOut)

            if (readResult == 0) {
                println("Lectura exitosa del bloque $blockNo")
                dataOut.data // Retornar los datos leídos
            } else {
                println("Fallo en la lectura del bloque $blockNo. Código de error: $readResult")
                null
            }
        } catch (e: RemoteException) {
            println("Error de comunicación con el lector NFC: ${e.message}")
            null
        }
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {

        try {
            Log.d("TAG-1.SERVICE", "Enter onServiceConnected()")
            deviceService = UDeviceService.Stub.asInterface(service)


            Log.d("TAG-1.SERVICE", "Calling register()")
            register(true)

            deviceService?.setLogLevel(
                com.usdk.apiservice.aidl.constants.LogLevel.USDKLOG_VERBOSE,
                com.usdk.apiservice.aidl.constants.LogLevel.EMVLOG_REALTIME
            )

            val logOption = Bundle()
            logOption.putBoolean(DeviceServiceData.COMMON_LOG, true)
            logOption.putBoolean(DeviceServiceData.MASTERCONTROL_LOG, true)
            deviceService?.debugLog(logOption)

            kernelRunState = emv != null

        } catch (e: java.lang.Exception) {
            kernelRunState = false
        }

    }

    @Throws(java.lang.IllegalStateException::class)
    private fun register(useEpayModule: Boolean) {
        try {
            Log.d("TAG-1.SERVICE", "register() - Registering USDK");

            val param = Bundle()
            param.putBoolean(DeviceServiceData.USE_EPAY_MODULE, useEpayModule)
            deviceService?.register(param, Binder())
            kernelAvailableState = true
            if(isKernelFunctionCall) kernelAvailableCallback.invoke(
                KernelAvailableStateCallBackObjectSDK.AvailableKernel(
                    KernelAvailableStateCallBackObjectSDK.AVAILABLE_KERNEL))
        } catch (e: RemoteException) {
            e.printStackTrace()
            throw java.lang.IllegalStateException(e.message)
        } catch (e: SecurityException) {
            e.printStackTrace()
            throw java.lang.IllegalStateException(e.message)
        }
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        TODO("Not yet implemented")
    }

}