package com.example.proyecto_facturas.inyeccion_dependencias

import android.content.Context
import co.infinum.retromock.Retromock
import com.example.proyecto_facturas.constantes.Constantes.Companion.BASE_URL
import com.example.proyecto_facturas.constantes.Constantes.Companion.FICTICIO
import com.example.proyecto_facturas.constantes.Constantes.Companion.NO_IMPLEMENTADO
import com.example.proyecto_facturas.constantes.Constantes.Companion.REAL
import com.example.proyecto_facturas.data.retrofit.APIRetrofitServiceInterface
import com.example.proyecto_facturas.data.retrofit.APIRetromockServiceInterface
import com.example.proyecto_facturas.data.retrofit.RetrofitServiceInterface
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

    //Añado lo siguiente para implementar Retromock
    companion object {
        private var datos = FICTICIO

        @Provides
        fun getDatos(): String {
            return datos
        }

        fun setDatos(newDatos: String) {
            datos = newDatos
        }
    }

    //Para Retrofit
    @Provides
    @Singleton
    fun getRetroInstance(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    //Para Retromock
    @Provides
    @Singleton
    fun getRetromockInstance(retrofit: Retrofit): Retromock {
        return Retromock.Builder()
            .retrofit(retrofit)
            .defaultBodyFactory(ResourceBodyFactory())
            .build()
    }

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


    //Modifico lo siguiente para implementar Retromock
    @Provides
    @Singleton
    fun getRetroServiceInterface(retrofit: Retrofit, retromock: Retromock, datos: String)
            : RetrofitServiceInterface {
        return if (datos == REAL) retrofit.create(APIRetrofitServiceInterface::class.java)
        else if (datos == FICTICIO) retromock.create(APIRetromockServiceInterface::class.java)
        else throw Error(NO_IMPLEMENTADO)
    }


}