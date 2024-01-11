package com.example.proyecto_facturas.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.CheckBox
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.widget.Toolbar
import com.example.proyecto_facturas.R
import com.example.proyecto_facturas.adapter.FacturaProvider
import com.example.proyecto_facturas.databinding.ActivityFiltradoBinding
import com.example.proyecto_facturas.domain.Factura
import java.util.Date

class FiltradoActivity : AppCompatActivity() {

    private lateinit var listaFactura: MutableList<Factura>
    private lateinit var binding: ActivityFiltradoBinding
    private lateinit var intentLaunch: ActivityResultLauncher<Intent>
    private lateinit var btnDesde: Button
    private lateinit var btnHasta: Button
    private lateinit var tvMinImporte:TextView
    private lateinit var tvMaxImporte:TextView
    private lateinit var tvIntervaloImporte:TextView
    private lateinit var seekbarImporte:SeekBar
    private lateinit var checkboxPagada:CheckBox
    private lateinit var checkboxAnuladas:CheckBox
    private lateinit var checkboxCuotaFija:CheckBox
    private lateinit var checkboxPendientesDePago:CheckBox
    private lateinit var checkboxPlanDePago:CheckBox
    private lateinit var btnAplicar:Button
    private lateinit var btnEliminarFiltros:Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFiltradoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listaFactura = FacturaProvider.listaFacturas

        btnDesde = binding.btnDesde
        btnDesde.setOnClickListener{
            obtenerFechaDesde()
        }

        btnHasta = binding.btnHasta
        btnHasta.setOnClickListener{
            obtenerFechaHasta()
        }

        // Configuro la toolbar genérica
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Modifico el título de la barra de herramientas (toolbar)
        supportActionBar?.title = "Filtrar facturas"
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_cerrar, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuCerrar -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun obtenerFechaDesde() {
        val calendario = Calendar.getInstance()
        val anno = calendario.get(Calendar.YEAR)
        val mes = calendario.get(Calendar.MONTH) + 1
        val dia = calendario.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(this,
            { view, year1, month, dayOfMonth ->
                binding.btnDesde.text = "$dayOfMonth/${month + 1}/$year1"
            }, anno, mes, dia)

        datePickerDialog.datePicker.maxDate = Date().time
        datePickerDialog.show()
    }

    private fun obtenerFechaHasta() {
        val calendario = Calendar.getInstance()
        val anno = calendario.get(Calendar.YEAR)
        val mes = calendario.get(Calendar.MONTH) + 1
        val dia = calendario.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(this,
            { view, year1, month, dayOfMonth ->
                binding.btnHasta.text = "$dayOfMonth/${month + 1}/$year1"
            }, anno, mes, dia)

        //datePickerDialog.datePicker.maxDate = Date().time
        datePickerDialog.show()
    }
}