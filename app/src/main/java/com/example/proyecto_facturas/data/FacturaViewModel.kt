package com.example.proyecto_facturas.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FacturaViewModel(application: Application): AndroidViewModel(application) {

    private val getAllFacturas: LiveData<List<Factura>>
    private val repository:FacturaRepository

    init {
        val facturaDAO = FacturaDatabase.getDatabaseInstance(application).facturaDAO()
        repository = FacturaRepository(facturaDAO)
        getAllFacturas = repository.getAllFacturas
    }

    fun addFactura(factura: Factura){
        viewModelScope.launch(Dispatchers.IO){
            repository.addFactura(factura)
        }
    }

    fun deleteAllFacturas() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllFacturas()
        }
    }
}