package com.example.proyecto_facturas.inyeccion_dependencias

import android.content.Context
import com.example.proyecto_facturas.data.retrofit.RetrofitServiceInterface
import com.example.proyecto_facturas.data.rom.FacturaDAO
import com.example.proyecto_facturas.data.rom.FacturaDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun getAppDatabase(@ApplicationContext context: Context): FacturaDatabase {
        return FacturaDatabase.getAppDBInstance(context)
    }

    @Provides
    @Singleton
    fun getAppDao(invoiceDatabase: FacturaDatabase): FacturaDAO {
        return invoiceDatabase.getAppDAO()
    }

    val BASE_URL = "https://viewnextandroid.wiremockapi.cloud/"


    @Provides
    @Singleton
    fun getRetroServiceInterface(retrofit: Retrofit): RetrofitServiceInterface {
        return retrofit.create(RetrofitServiceInterface::class.java)
    }

    @Provides
    @Singleton
    fun getRetroInstance(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}