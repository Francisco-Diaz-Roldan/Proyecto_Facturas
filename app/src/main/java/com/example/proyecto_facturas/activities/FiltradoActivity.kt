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
import com.example.proyecto_facturas.data.rom.Factura
import java.util.Date

class FiltradoActivity : AppCompatActivity() {

    private lateinit var listaFactura: MutableList<Factura>
    private lateinit var binding: ActivityFiltradoBinding
    private lateinit var intentLaunch: ActivityResultLauncher<Intent>
    private lateinit var btnDesde: Button
    private lateinit var btnHasta: Button
    private lateinit var tvMinImporte: TextView
    private lateinit var tvMaxImporte: TextView
    private lateinit var tvImporteActual: TextView
    private lateinit var seekbarImporte: SeekBar
    private lateinit var checkboxPagada: CheckBox
    private lateinit var checkboxAnuladas: CheckBox
    private lateinit var checkboxCuotaFija: CheckBox
    private lateinit var checkboxPendientesDePago: CheckBox
    private lateinit var checkboxPlanDePago: CheckBox
    private lateinit var btnAplicar: Button
    private lateinit var btnEliminarFiltros: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFiltradoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Para la lista de facturas
        listaFactura = FacturaProvider.listaFacturas
        //Para el botón desde
        btnDesde = binding.btnDesde
        btnDesde.setOnClickListener {
            obtenerFecha(binding.btnDesde, true, false)
        }
        //Para el botón hasta
        btnHasta = binding.btnHasta
        btnHasta.setOnClickListener {
            obtenerFecha(binding.btnHasta, true, false)
        }
        //Para la seekbar
        //Recibo el valor máximo de las facturas de la ventana anterior y lo redondeo
        val valorMax = intent.getDoubleExtra("valorMax", 0.0).toInt() + 1

        //Configuro la seekbar y los textos con sus valores min, max y actuales
        seekbarImporte = binding.seekbarImporte
        seekbarImporte.max = valorMax   //Le indico el valor máximo para que no vaya hasta 100
        tvMinImporte = binding.tvMinImporte
        tvMaxImporte = binding.tvMaxImporte
        tvImporteActual = binding.tvImporteActual
        tvMinImporte.text = getString(R.string.`_0e`)
        tvImporteActual.text = getString(R.string.`_0e`)
        calcularValorActualSeekbar(valorMax)

        //Para las checkboxes
        checkboxPagada = binding.checkboxPagada
        checkboxAnuladas = binding.checkboxAnuladas
        checkboxCuotaFija = binding.checkboxCuotaFija
        checkboxPendientesDePago = binding.checkboxPendientesDePago
        checkboxPlanDePago = binding.checkboxPlanDePago


        //Para el boton de eliminar filtros
        btnEliminarFiltros = binding.btnEliminarFiltros
        btnEliminarFiltros.setOnClickListener {
            //Para restablecer los valores de texto de los botones con las fechas
            resetearFecha()
            //Para restablecer el valor de la seekbar a 0
            resetearSeekbar()
            //Para restablecer los valores de las checkboxes
            resetearCheckBoxes()
        }

        //Para el boton de aplicar filtros
        btnAplicar = binding.btnAplicar
        btnAplicar.setOnClickListener {
            //TODO
        }


        // Configuro la toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Modifico el título de la barra de herramientas (toolbar)
        supportActionBar?.title = "Filtrar facturas"
    }

    private fun resetearFecha() {
        btnDesde.text = getString(R.string.dia_mes_ano)
        btnHasta.text = getString(R.string.dia_mes_ano)
    }

    private fun resetearSeekbar(){
        seekbarImporte.progress = 0
    }

    private fun resetearCheckBoxes(){
        checkboxPagada.isChecked = false
        checkboxAnuladas.isChecked = false
        checkboxCuotaFija.isChecked = false
        checkboxPendientesDePago.isChecked = false
        checkboxPlanDePago.isChecked = false
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

    private fun obtenerFecha(
        button: Button, restriccionMaxDate: Boolean = false, restriccionMinDate: Boolean = false
    ) {
        val calendario = Calendar.getInstance()
        val anno = calendario.get(Calendar.YEAR)
        val mes = calendario.get(Calendar.MONTH)
        val dia = calendario.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this,
            { _, year, month, dayOfMonth ->
                button.text = "$dayOfMonth/${month + 1}/$year"
            }, anno, mes, dia
        )

        // Establezco las fechas mínimas y máximas
        if (restriccionMinDate) {
            datePickerDialog.datePicker.minDate = calendario.timeInMillis
        }

        if (restriccionMaxDate) {
            datePickerDialog.datePicker.maxDate = Date().time
        }
        datePickerDialog.show()
    }

    private fun calcularValorActualSeekbar(maxImporte: Int) {
        //Seekbar y textos de la seekbar
        val seekbarImporte = findViewById<SeekBar>(R.id.seekbarImporte)
        val tvMaxImporte = findViewById<TextView>(R.id.tvMaxImporte)
        val tvImporteActual = findViewById<TextView>(R.id.tvImporteActual)

        tvMaxImporte.text = "${maxImporte}€" //Escribo el valor maximo de la seekbar y le añado €

        //Acciones a realizar en caso de mover la barra
        seekbarImporte.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                tvImporteActual.text = "${i}€"  //Escribo el valor actual de la seekbar y le añado €
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                //No hace nada
                Log.d("onStartTrackingTouch", "onStartTrackingTouch: algo ha fallado")
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                //No hace nada
                Log.d("onStopTrackingTouch", "onStopTrackingTouch: algo ha fallado")
            }
        })
    }
}