package com.example.proyecto_facturas.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.proyecto_facturas.data.retrofit.RetrofitServiceInterface
import com.example.proyecto_facturas.data.retrofit.model.FacturaRepositoriesListResponse
import com.example.proyecto_facturas.data.rom.FacturaDAO
import com.example.proyecto_facturas.data.rom.Factura
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class FacturaRepository @Inject constructor(
    private val facturaDAO: FacturaDAO,
    private val retrofitServiceInterface: RetrofitServiceInterface
) {
    fun obtenerFacturasDesdeRoom(): LiveData<List<Factura>> {
        return facturaDAO.getAllFacturas()
    }

    fun insertarFacturasEnRoom(factura: Factura) {
        facturaDAO.insertarFactura(factura)
    }

    fun llamarApi() {
        val call: Call<FacturaRepositoriesListResponse> =
            retrofitServiceInterface.obtenerFacturasApi()
        call?.enqueue(object : Callback<FacturaRepositoriesListResponse> {
            override fun onResponse(
                call: Call<FacturaRepositoriesListResponse>,
                response: Response<FacturaRepositoriesListResponse>
            ) {
                if (response.isSuccessful) {
                    facturaDAO.deleteAllFacturas()
                    response.body()?.facturas?.forEach {
                        insertarFacturasEnRoom(
                            Factura(
                                descEstado = it.descEstado,
                                importeOrdenacion = it.importeOrdenacion,
                                fecha = it.fecha
                            )
                        )
                    }
                }
            }

            override fun onFailure(call: Call<FacturaRepositoriesListResponse>, t: Throwable) {
                Log.d("ERROR", "Ha ocurrido un error al establecer la conexión.")
            }
        })
    }
}