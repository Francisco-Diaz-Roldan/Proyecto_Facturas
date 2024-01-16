package com.example.proyecto_facturas.data.rom

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Factura:: class], version = 1, exportSchema = false)
abstract class FacturaDatabase: RoomDatabase() {

    abstract fun facturaDAO(): FacturaDAO

    companion object{
            @Volatile
            private var INSTANCE: FacturaDatabase?= null

        fun getDatabaseInstance(context: Context): FacturaDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FacturaDatabase::class.java,
                    "factura_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}