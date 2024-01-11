package com.example.proyecto_facturas.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.CheckBox
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
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
    private lateinit var tvImporteActual:TextView
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
        //Para la lista de facturas
        listaFactura = FacturaProvider.listaFacturas
        //Para el botón desde
        btnDesde = binding.btnDesde
        btnDesde.setOnClickListener{
            obtenerFecha(binding.btnDesde, true)
        }
        //Para el botón hasta
        btnHasta = binding.btnHasta
        btnHasta.setOnClickListener{
            obtenerFecha(binding.btnHasta, false)
        }
        //Para la seekbar
        //Recibo el valor máximo de las facturas de la ventana anterior
        val valorMax = intent.getDoubleExtra("valorMax", 0.0)

        seekbarImporte = binding.seekbarImporte
        tvMinImporte = binding.tvMinImporte
        tvMinImporte.text = getString(R.string.`_0e`)
        tvMaxImporte = binding.tvMaxImporte
        tvMaxImporte.text = "${valorMax.toInt()+1}€" //Redondeo el valor máximo
        tvImporteActual = binding.tvImporteActual
        tvImporteActual.text = getString(R.string.`_0e`)
        calcularValorActualSeekbar(valorMax.toInt())


        // Configuro la toolbar
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

    private fun obtenerFecha(button: Button, restriccionMaxDate: Boolean = false) {
        val calendario = Calendar.getInstance()
        val anno = calendario.get(Calendar.YEAR)
        val mes = calendario.get(Calendar.MONTH) + 1
        val dia = calendario.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(this,
            { view, year1, month, dayOfMonth ->
                button.text = "$dayOfMonth/${month + 1}/$year1"
            }, anno, mes, dia)

        if (restriccionMaxDate) {
            datePickerDialog.datePicker.maxDate = Date().time
        }
        datePickerDialog.show()
    }

    private fun calcularValorActualSeekbar(maxImporte: Int) {
        //Seekbar y textos de la seekbar, inicializar y onClick
        val seekBar = findViewById<SeekBar>(R.id.seekbarImporte)
        val tvMaxSeekBar = findViewById<TextView>(R.id.tvMaxImporte)
        val tvValorImporte = findViewById<TextView>(R.id.tvImporteActual)

        tvMaxSeekBar.text = maxImporte.toString()

        //Acciones a realizar en caso de mover la barra
        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                tvValorImporte.text = i.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                //No hace nada
                Log.d("onStartTrackingTouch", "onStartTrackingTouch: ha fallado")
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                //No hace nada
                Log.d("onStopTrackingTouch", "onStopTrackingTouch: ha fallado")
            }
        })
    }
}