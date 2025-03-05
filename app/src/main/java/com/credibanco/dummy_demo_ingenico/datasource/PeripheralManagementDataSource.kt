package com.credibanco.dummy_demo_ingenico.datasource

import com.credibanco.dummy_demo_ingenico.dto.PeripheralDTO

interface PeripheralManagementDataSource {
    suspend operator fun invoke() : PeripheralDTO
}