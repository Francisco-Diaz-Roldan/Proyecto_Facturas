package com.example.proyecto_facturas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Configuro la toolbar genérica
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

// Puedes modificar el título de la barra de herramientas si es necesario
        supportActionBar?.title = "Título de tu Actividad Principal"
    }


}