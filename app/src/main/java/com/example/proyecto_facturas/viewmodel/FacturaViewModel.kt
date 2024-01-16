package com.example.proyecto_facturas.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.proyecto_facturas.data.retrofit.RetrofitService  // Importo RetrofitService
import com.example.proyecto_facturas.data.rom.FacturaDatabase
import com.example.proyecto_facturas.repository.FacturaRepository
import com.example.proyecto_facturas.data.rom.Factura
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FacturaViewModel(
    application: Application,
    retrofitService: RetrofitService ): AndroidViewModel(application) {

    val getAllFacturas: LiveData<List<Factura>>
    private val repository: FacturaRepository

    init {
        val facturaDAO = FacturaDatabase.getDatabaseInstance(application).facturaDAO()
        repository = FacturaRepository(facturaDAO, retrofitService)// Paso RetrofitService al constructor 
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