package com.example.proyecto_facturas.data.retrofit

import co.infinum.retromock.meta.Mock
import co.infinum.retromock.meta.MockCircular
import co.infinum.retromock.meta.MockResponse
import co.infinum.retromock.meta.MockResponses
import com.example.proyecto_facturas.data.retrofit.model.FacturaRepositoriesListResponse
import retrofit2.Call
import retrofit2.http.GET

//Creo una interfaz general que divido en dos en función de si quiero usar Retrofit o RetroMock
interface ServiceInterface {
    fun obtenerFacturasApi(): Call<FacturaRepositoriesListResponse>
}

//Para usar RetroFit
interface APIRetrofitServiceInterface: ServiceInterface {
    @GET("facturas")
    override fun obtenerFacturasApi(): Call<FacturaRepositoriesListResponse>
}

//Para usar RetroMock
interface APIRetromockServiceInterface: ServiceInterface {
    @Mock
    @MockResponses(
        MockResponse(body = "mock.json"),
        MockResponse(body = "mock2.json"),
        MockResponse(body = "mock3.json")
    )
    @MockCircular
    @GET("/")
    override fun obtenerFacturasApi(): Call<FacturaRepositoriesListResponse>
}