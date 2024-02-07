package com.example.proyecto_facturas.inyeccion_dependencias

import android.content.Context
import co.infinum.retromock.Retromock
import com.example.proyecto_facturas.constantes.Constantes
import com.example.proyecto_facturas.constantes.Constantes.Companion.BASE_URL
import com.example.proyecto_facturas.data.retrofit.APIRetrofitServiceInterface
import com.example.proyecto_facturas.data.retrofit.APIRetromockServiceInterface
import com.example.proyecto_facturas.data.rom.FacturaDAO
import com.example.proyecto_facturas.data.rom.FacturaDatabase
import com.example.proyecto_facturas.retromock.ResourceBodyFactory
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

    @Provides
    @Singleton
    fun obtenerRetroServiceInterface(retrofit: Retrofit): APIRetrofitServiceInterface {
        return retrofit.create(APIRetrofitServiceInterface::class.java)

    }

    @Provides
    @Singleton
    fun obtenerRetromockInstance(retromock: Retromock): APIRetromockServiceInterface {
        return retromock.create(APIRetromockServiceInterface::class.java)

    }

    @Provides
    @Singleton
    fun construirRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun construirRetromock(retrofit: Retrofit): Retromock {
        return Retromock.Builder()
            .retrofit(retrofit)
            .defaultBodyFactory(ResourceBodyFactory())
            .build()
    }
}