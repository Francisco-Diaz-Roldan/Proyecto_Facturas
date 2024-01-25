package com.example.proyecto_facturas.data.rom

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Factura::class], version = 1, exportSchema = false)
abstract class FacturaDatabase : RoomDatabase() {
    abstract fun getAppDAO(): FacturaDAO

    companion object {
        //Instancia de la Base de Datos
        private var DB_INSTANCE: FacturaDatabase? = null

        fun getAppDBInstance(context: Context): FacturaDatabase {
            if (DB_INSTANCE == null) {
                DB_INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    FacturaDatabase::class.java,
                    "invoice_database"
                )
                    .allowMainThreadQueries()
                    .build()
            }
            return DB_INSTANCE!!
        }
    }
}