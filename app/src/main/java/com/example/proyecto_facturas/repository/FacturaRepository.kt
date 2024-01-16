package com.example.proyecto_facturas.repository

import androidx.lifecycle.LiveData
import com.example.proyecto_facturas.data.rom.FacturaDAO
import com.example.proyecto_facturas.data.rom.Factura

class FacturaRepository(private val facturaDAO: FacturaDAO) {

    val getAllFacturas:LiveData<List<Factura>> = facturaDAO.getAllFacturas()

    suspend fun addFactura(factura: Factura){
        facturaDAO.addFactura(factura)
    }

    suspend fun deleteAllFacturas() {
        facturaDAO.deleteAllFacturas()
    }
}