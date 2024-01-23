package com.example.proyecto_facturas.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.icu.text.SimpleDateFormat
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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.proyecto_facturas.Filtro
import com.example.proyecto_facturas.R
import com.example.proyecto_facturas.constantes.Constantes
import com.example.proyecto_facturas.databinding.ActivityFiltradoBinding
import com.example.proyecto_facturas.data.rom.Factura
import com.google.gson.Gson
import java.util.Date
import java.util.Locale

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

        //Para el botón desde
        btnDesde = binding.btnDesde
        btnDesde.setOnClickListener {
            obtenerFecha(btnDesde, restriccionMinDate = true)
        }

        //Para el botón hasta ( fecha mínima)
        btnHasta = binding.btnHasta
        btnHasta.setOnClickListener {
            if (btnDesde.text.toString() == getString(R.string.dia_mes_ano)) {
                mostrarAlertDialog()
            } else {
                obtenerFecha(btnHasta, restriccionMinDate = true, fechaMinima = obtenerFechaDesde())
            }
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
            val gson = Gson()
            val intent = Intent(this, MainActivity::class.java)

            val estadoCheckBox = hashMapOf(
               Constantes.PAGADAS to binding.checkboxPagada.isChecked,
               Constantes.ANULADAS to binding.checkboxAnuladas.isChecked,
               Constantes.CUOTA_FIJA to binding.checkboxCuotaFija.isChecked,
               Constantes.PENDIENTES_DE_PAGO to binding.checkboxPendientesDePago.isChecked,
               Constantes.PLAN_DE_PAGO to binding.checkboxPlanDePago.isChecked
            )

            var fechaDesde = binding.btnDesde.text.toString()
            var fechaHasta = binding.btnHasta.text.toString()
            var importe = binding.seekbarImporte.progress.toDouble()

            var filtro = Filtro(fechaHasta, fechaDesde, importe, estadoCheckBox)
            Log.d("IntentFiltradoActivity", filtro.toString())
            intent.putExtra("datosFiltrados", gson.toJson(filtro))

            startActivity(intent)
            Log.d("BtnAplicar", "El botón de aplicación de filtros funciona correctamente")
        }


        // Configuro la toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Modifico el título de la barra de herramientas (toolbar)
        supportActionBar?.title = "Filtrar facturas"
    }

    private fun mostrarAlertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alerta")
        builder.setMessage("Por favor, selecciona primero la fecha Desde")

        builder.setPositiveButton("Aceptar") { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = builder.create()
        alertDialog.show()
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
        button: Button,
        restriccionMaxDate: Boolean = false,
        restriccionMinDate: Boolean = false,
        fechaMinima: Long? = null // Nueva parametro para la fecha minima
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
            fechaMinima?.let {
                datePickerDialog.datePicker.minDate = it
            }
        }

        if (restriccionMaxDate) {
            datePickerDialog.datePicker.maxDate = Date().time
        }
        datePickerDialog.show()
    }

    private fun obtenerFechaDesde(): Long {
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        // Obtengo la fecha desde  donde almaceno la fecha seleccionada
        val fechaDesdeTexto = btnDesde.text.toString()

        try {
            // Parseo la fecha y devuelvo el tiempo en milisegundos
            val fechaDesde = formato.parse(fechaDesdeTexto)
            return fechaDesde?.time ?: 0L
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // En caso de error devuelve el valor predeterminado
        return 0L
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