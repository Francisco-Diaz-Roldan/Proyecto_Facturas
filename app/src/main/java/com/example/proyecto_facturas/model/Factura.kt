package com.example.proyecto_facturas.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "factura_table")
data class Factura(
    @PrimaryKey(autoGenerate = true)
    val id:Int,
    var descEstado: String,
    var importeOrdenacion: Double,
    var fecha: String
)

