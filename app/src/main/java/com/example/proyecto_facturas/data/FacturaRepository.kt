package com.example.proyecto_facturas.data

import androidx.lifecycle.LiveData

class FacturaRepository(private val facturaDAO: FacturaDAO) {

    val getAllFacturas:LiveData<List<Factura>> = facturaDAO.getAllFacturas()

    suspend fun addFactura(factura: Factura){
        facturaDAO.addFactura(factura)
    }

    suspend fun deleteAllFacturas() {
        facturaDAO.deleteAllFacturas()
    }
}