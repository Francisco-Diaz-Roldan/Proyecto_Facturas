package com.example.proyecto_facturas.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.proyecto_facturas.repository.FacturaRepository
import com.example.proyecto_facturas.data.rom.Factura
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

//Inyección de dependencias. Indica que la clase FacturaViewModel debe ser gestionada por Hilt.
@HiltViewModel
class facturaViewModel @Inject constructor(private val facturaRepository: FacturaRepository) :
    ViewModel() {
    fun getAllRepositoryList(): LiveData<List<Factura>> {
        return facturaRepository.obtenerFacturasDesdeRoom()
    }

    fun llamarApi() {
        facturaRepository.llamarApi()
    }
}
