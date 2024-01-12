package com.example.proyecto_facturas.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyecto_facturas.R
import com.example.proyecto_facturas.adapter.FacturaAdapter
import com.example.proyecto_facturas.adapter.FacturaProvider
import com.example.proyecto_facturas.databinding.ActivityMainBinding
import com.example.proyecto_facturas.data.Factura

class MainActivity : AppCompatActivity() {

    private lateinit var listaFactura: MutableList<Factura>
    private lateinit var binding: ActivityMainBinding
    private lateinit var intentLaunch: ActivityResultLauncher<Intent>
    private lateinit var adapter: FacturaAdapter
    private var valorMax: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listaFactura = FacturaProvider.listaFacturas
        binding.rvFacturas.layoutManager = LinearLayoutManager(this)
        binding.rvFacturas.adapter =
            FacturaAdapter(listaFactura) { factura ->
                onItemSelected(factura)
            }

        this.onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })

        //Declaro el valor máximo que le pasaré a la Toolbar
        valorMax = calcularMaximo()

        // Configuro la toolbar genérica
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Modifico el título de la barra de herramientas (toolbar)
        supportActionBar?.title = "Facturas"

    }

    private fun calcularMaximo(): Double {
        var importeMaximo = 0.0
        for (factura in listaFactura) {
            val facturaActual = factura.importeOrdenacion
            if(importeMaximo < facturaActual) importeMaximo = facturaActual
        }
        return  importeMaximo
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_filtros, menu)
        return true
    }

    private fun onItemSelected(factura: Factura) {
        mostrarDialogAlerta()
    }

    private fun mostrarDialogAlerta() {
        val builder = AlertDialog.Builder(this)

        // Configuro el diálogo
        builder.setTitle("Informción")
        builder.setMessage("Esta funcionalidad aún no está disponible")

        // Botón negativo Cerrar
        builder.setNegativeButton("Cerrar") { dialog, which ->
            dialog.dismiss()
        }

        // Creo y enseño el diálogo
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menuFiltrar -> {
                val intent = Intent(this, FiltradoActivity::class.java)
                intent.putExtra("valorMax", valorMax)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}