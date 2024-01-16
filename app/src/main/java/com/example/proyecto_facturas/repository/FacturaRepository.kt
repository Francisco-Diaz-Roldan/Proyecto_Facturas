package com.example.proyecto_facturas.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.proyecto_facturas.data.retrofit.RetrofitService
import com.example.proyecto_facturas.data.retrofit.model.FacturaRepositoriesListResponse
import com.example.proyecto_facturas.data.rom.FacturaDAO
import com.example.proyecto_facturas.data.rom.Factura
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FacturaRepository(
    private val facturaDAO: FacturaDAO,
    private val retrofitService: RetrofitService
) {

    //ROOM
    val getAllFacturas:LiveData<List<Factura>> = facturaDAO.getAllFacturas()

    suspend fun addFactura(factura: Factura){
        facturaDAO.addFactura(factura)
    }

    suspend fun deleteAllFacturas() {
        facturaDAO.deleteAllFacturas()
    }

    //RETROFIT
    fun makeApiCall() {
        val call: Call<FacturaRepositoriesListResponse> = retrofitService.getFacturasFromApi()
        call?.enqueue(object : Callback<FacturaRepositoriesListResponse>{
            override fun onResponse(
                call: Call<FacturaRepositoriesListResponse>,
                response: Response<FacturaRepositoriesListResponse>
            ) {
                if (response.isSuccessful) {
                    facturaDAO.deleteAllFacturas()
                    response.body()?.facturas?.forEach{
                        addFacturaInRoom(Factura(
                            descEstado = it.descEstado,
                            importeOrdenacion = it.importeOrdenacion,
                            fecha = it.fecha))
                    }
                }
            }


            override fun onFailure(call: Call<FacturaRepositoriesListResponse>, t: Throwable) {
                Log.d("ERROR", "Error al establecer la conexión.")
            }
        })
    }

    private fun addFacturaInRoom(factura: Factura) {
        facturaDAO.addFactura(factura)
    }
}