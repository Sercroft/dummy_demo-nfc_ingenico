package com.credibanco.dummy_demo_ingenico.di

import android.content.Context
import com.credibanco.dummy_demo_ingenico.datasource.NFCTagDataSource
import com.credibanco.dummy_demo_ingenico.datasource.impl.NFCTagDataSourceImpl
import com.credibanco.dummy_demo_ingenico.repository.NFCTagRepository
import com.credibanco.dummy_demo_ingenico.repository.impl.NFCTagRepositoryImpl
import com.credibanco.dummy_demo_ingenico.usecase.NFCTagUseCase
import com.credibanco.dummy_demo_ingenico.usecase.impl.NFCTagUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MainModule {

    @Provides
    @Singleton
    fun provideNFCTagUseCase(nfcTagRepository: NFCTagRepository): NFCTagUseCase =
        NFCTagUseCaseImpl(nfcTagRepository)

    @Provides
    @Singleton
    fun provideNFCTagRepository(nfcTagDataSource: NFCTagDataSource): NFCTagRepository =
        NFCTagRepositoryImpl(nfcTagDataSource)

    @Provides
    @Singleton
    fun provideNFCTagDataSource(@ApplicationContext context: Context): NFCTagDataSource =
        NFCTagDataSourceImpl(context)
}