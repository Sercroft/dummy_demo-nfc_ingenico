package com.credibanco.dummy_demo_ingenico

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.credibanco.dummy_demo_ingenico.datasource.NFCTagDataSource
import com.credibanco.dummy_demo_ingenico.datasource.impl.NFCTagDataSourceImpl
import com.credibanco.dummy_demo_ingenico.util.Constants.TAG
import com.credibanco.dummy_demo_ingenico.viewmodel.NFCTagReaderViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val nfcTagReaderViewModel: NFCTagReaderViewModel by viewModels()

    private lateinit var switchNfc: Switch
    private var isNfcEnabled: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val btn = findViewById<Button>(R.id.btn_nfc)
        val tvResponse = findViewById<TextView>(R.id.tv_response)
        switchNfc = findViewById(R.id.switch_nfc)

        nfcTagReaderViewModel.responseNfcTagReading.observe(this) { response ->
            tvResponse.text = response
        }

        switchNfc.setOnCheckedChangeListener { _, isChecked ->
            isNfcEnabled = isChecked
            Log.d(TAG, "NFC Enabled: $isNfcEnabled")
        }

        btn.setOnClickListener {
            isKernelRun()
            startNFCTagReader()
        }
    }

    private fun isKernelRun() {
        nfcTagReaderViewModel.isKernelRun()
    }

    private fun startNFCTagReader() {
        nfcTagReaderViewModel.readNfcTag(isNfcEnabled)
    }
}