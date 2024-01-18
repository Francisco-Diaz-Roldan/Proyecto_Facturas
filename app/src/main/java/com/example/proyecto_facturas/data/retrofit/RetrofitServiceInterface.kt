package com.example.proyecto_facturas.data.retrofit

import com.example.proyecto_facturas.data.retrofit.model.FacturaRepositoriesListResponse
import retrofit2.Call
import retrofit2.http.GET

interface RetrofitServiceInterface {
    @GET("facturas")
    fun obtenerFacturasApi(): Call<FacturaRepositoriesListResponse>
}