package com.example.proyecto_facturas.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FacturaDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addFactura(factura: Factura)

    @Query("SELECT * FROM factura_table")
    fun getAllFacturas(): LiveData<List<Factura>>

    @Query("DELETE FROM factura_table")
    fun deleteAllFacturas()
}