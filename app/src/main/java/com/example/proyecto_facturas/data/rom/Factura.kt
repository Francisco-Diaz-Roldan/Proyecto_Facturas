package com.example.proyecto_facturas.data.rom

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "factura_table")
data class Factura(
    @PrimaryKey(autoGenerate = true)
    val id:Int = 0,
    val descEstado: String?,
    val importeOrdenacion: Double?,
    val fecha: String?
)