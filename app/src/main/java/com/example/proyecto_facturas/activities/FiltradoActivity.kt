package com.example.proyecto_facturas.activities

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.proyecto_facturas.Filtro
import com.example.proyecto_facturas.R
import com.example.proyecto_facturas.constantes.Constantes.Companion.ANULADAS
import com.example.proyecto_facturas.constantes.Constantes.Companion.CUOTA_FIJA
import com.example.proyecto_facturas.constantes.Constantes.Companion.PAGADAS
import com.example.proyecto_facturas.constantes.Constantes.Companion.PENDIENTES_DE_PAGO
import com.example.proyecto_facturas.constantes.Constantes.Companion.PLAN_DE_PAGO
import com.example.proyecto_facturas.databinding.ActivityFiltradoBinding
import com.google.gson.Gson
import java.util.Locale

class FiltradoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFiltradoBinding
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
    private lateinit var preferenciasCompartidas: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFiltradoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inicializarPreferenciasCompartidas()

        configurarBotonDesde()

        configurarBotonHasta()

        configurarSeekbar()

        configurarCheckBoxes()

        configurarBotonEliminarFiltros()

        configurarBotonAplicarFiltros()

        configurarToolbar()

        aplicarFiltrosGuardados()
    }

    private fun configurarBotonHasta() {
        btnHasta = binding.btnHasta
        btnHasta.setOnClickListener {
            if (btnDesde.text.toString() == getString(R.string.dia_mes_ano)) {
                mostrarAlertDialog()
            } else {
                obtenerFecha(
                    btnHasta, restriccionMinDate = true, restriccionMaxDate = true,
                    fechaMinima = obtenerFechaDesde()
                )
            }
        }
    }

    private fun configurarBotonDesde() {
        btnDesde = binding.btnDesde
        btnDesde.setOnClickListener {
            obtenerFecha(btnDesde, restriccionMinDate = true)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun obtenerFecha(
        button: Button,
        restriccionMaxDate: Boolean = false,
        restriccionMinDate: Boolean = false,
        fechaMinima: Long? = null // Parametro para la fecha minima
    ) {
        val calendario = Calendar.getInstance()
        val anno = calendario.get(Calendar.YEAR)
        val mes = calendario.get(Calendar.MONTH)
        val dia = calendario.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                button.text = "$dayOfMonth/${month + 1}/$year"
            }, anno, mes, dia
        )

        // Establezco las fechas mínimas y máximas
        restriccionMinDate.let {
            datePickerDialog.datePicker.minDate = fechaMinima ?: 0
        }

        restriccionMaxDate.let {
            val calendarioMax = Calendar.getInstance()
            calendarioMax.add(Calendar.MONTH, 12) // Establece la fecha maxima en 12 meses
            datePickerDialog.datePicker.maxDate = calendarioMax.timeInMillis
        }
        datePickerDialog.show()
    }

    private fun obtenerFechaDesde(): Long {
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val fechaDesdeTexto = btnDesde.text.toString()
        try {
            val fechaDesde = formato.parse(fechaDesdeTexto)
            return fechaDesde?.time ?: 0L
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0L // En caso de error devuelve el valor predeterminado
    }

    private fun inicializarPreferenciasCompartidas() {
        preferenciasCompartidas = getSharedPreferences(
            "preferencias_filtrado",
            Context.MODE_PRIVATE
        )
    }

    private fun configurarSeekbar() {
        //Recibo el valor máximo de las facturas de la ventana anterior y lo redondeo
        val valorMax = calcularValorMax()

        //Configuro la seekbar y los textos con sus valores min, max y actuales
        seekbarImporte = binding.seekbarImporte
        seekbarImporte.max = valorMax //Le indico el valor máximo para que no vaya hasta 100
        tvMinImporte = binding.tvMinImporte
        tvMaxImporte = binding.tvMaxImporte
        tvImporteActual = binding.tvImporteActual
        tvMinImporte.text = getString(R.string._0e)
        tvImporteActual.text = getString(R.string._0e)

        calcularValorActualSeekbar(valorMax)
    }

    private fun calcularValorMax(): Int {
        return intent.getDoubleExtra("valorMax", 0.0).toInt() + 1
    }

    private fun configurarToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Filtrar facturas"// Cambio el título de la toolbar
    }

    private fun configurarBotonAplicarFiltros() {
        binding.btnAplicar.setOnClickListener {
            val gson = instanciarGson()
            val intent = Intent(this, MainActivity::class.java)
            val estadoCheckBox = hashMapOf(
                PAGADAS to binding.checkboxPagada.isChecked,
                ANULADAS to binding.checkboxAnuladas.isChecked,
                CUOTA_FIJA to binding.checkboxCuotaFija.isChecked,
                PENDIENTES_DE_PAGO to binding.checkboxPendientesDePago.isChecked,
                PLAN_DE_PAGO to binding.checkboxPlanDePago.isChecked
            )
            val fechaDesde = binding.btnDesde.text.toString()
            val fechaHasta = binding.btnHasta.text.toString()
            val importe = binding.seekbarImporte.progress.toDouble()
            val filtro = Filtro(fechaHasta, fechaDesde, importe, estadoCheckBox)
            intent.putExtra("datosFiltrados", gson.toJson(filtro))
            cargarPreferenciasCompartidas()
            startActivity(intent)
        }
    }

    private fun instanciarGson(): Gson { return Gson() }

    private fun configurarBotonEliminarFiltros() {
        binding.btnEliminarFiltros.setOnClickListener {
            resetearFecha()
            resetearSeekbar()
            resetearCheckBoxes()
        }
    }

    private fun configurarCheckBoxes() {
        checkboxPagada = binding.checkboxPagada
        checkboxAnuladas = binding.checkboxAnuladas
        checkboxCuotaFija = binding.checkboxCuotaFija
        checkboxPendientesDePago = binding.checkboxPendientesDePago
        checkboxPlanDePago = binding.checkboxPlanDePago
    }

    private fun mostrarAlertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alerta")
        builder.setMessage("Por favor, selecciona primero la fecha Desde")
        builder.setPositiveButton("Aceptar") { dialog, _ -> dialog.dismiss() }
        val alertDialog = builder.create()
        alertDialog.show()
    }


    private fun resetearFecha() {
        btnDesde.text = getString(R.string.dia_mes_ano)
        btnHasta.text = getString(R.string.dia_mes_ano)
    }

    private fun resetearSeekbar() {
        val valorMax = calcularValorMax()
        seekbarImporte.progress = valorMax
    }

    private fun resetearCheckBoxes() {
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
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun calcularValorActualSeekbar(maxImporte: Int) {
        //Seekbar y textos de la seekbar
        val seekbarImporte = findViewById<SeekBar>(R.id.seekbarImporte)
        val tvMaxImporte = findViewById<TextView>(R.id.tvMaxImporte)
        val tvImporteActual = findViewById<TextView>(R.id.tvImporteActual)

        tvMaxImporte.text = "${maxImporte}€" //Escribo el valor maximo de la seekbar y le añado €

        //Acciones a realizar en caso de mover la barra (seekbar)
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

    // Métodos para las preferencias compartidas
    private fun guardarEstadoFiltro(filtro: Filtro) {
        val preferencias = getSharedPreferences("preferencias_filtrado", Context.MODE_PRIVATE)
        val gson = instanciarGson()
        val jsonFiltro = gson.toJson(filtro)
        preferencias.edit().putString("ESTADO_FILTRO", jsonFiltro).apply()
    }

    private fun aplicarFiltrosGuardados() {
        val preferencias = getSharedPreferences("preferencias_filtrado", Context.MODE_PRIVATE)
        val jsonFiltro = preferencias.getString("ESTADO_FILTRO", null)
        val gson = instanciarGson()
        // Creo un filtro predeterminado en caso de que no haya ningún filtro almacenado
        val filtroPredeterminado = Filtro(
            "fechaHasta", "fechaDesde",
            0.0, hashMapOf()
        )
        // Si jsonFiltro no es nulo se pasa a un objeto Filtro utilizando Gson
        val filtro = jsonFiltro?.let {
            gson.fromJson(it, Filtro::class.java)
        } ?: filtroPredeterminado // Si jsonFiltro es nulo, se asigna el filtro predeterminado
        cargarFiltros(filtro)
    }

    private fun cargarFiltros(filtro: Filtro) {
        // Asegúrate de tener acceso a las vistas correspondientes desde esta función
        btnDesde.text = filtro.fechaDesde
        btnHasta.text = filtro.fechaHasta
        seekbarImporte.progress = filtro.importe.toInt()
        checkboxPagada.isChecked = filtro.estado[PAGADAS] ?: false
        checkboxAnuladas.isChecked = filtro.estado[ANULADAS] ?: false
        checkboxCuotaFija.isChecked = filtro.estado[CUOTA_FIJA] ?: false
        checkboxPendientesDePago.isChecked = filtro.estado[PENDIENTES_DE_PAGO] ?: false
        checkboxPlanDePago.isChecked = filtro.estado[PLAN_DE_PAGO] ?: false
    }

    private fun cargarPreferenciasCompartidas() {
        val slider = seekbarImporte.progress.toDouble()
        val checkBoxes = hashMapOf(
            PAGADAS to checkboxPagada.isChecked,
            ANULADAS to checkboxAnuladas.isChecked,
            CUOTA_FIJA to checkboxCuotaFija.isChecked,
            PENDIENTES_DE_PAGO to checkboxPendientesDePago.isChecked,
            PLAN_DE_PAGO to checkboxPlanDePago.isChecked
        )
        val fechaMinima = btnDesde.text.toString()
        val fechaMaxima = btnHasta.text.toString()
        val filtro = Filtro(fechaMaxima, fechaMinima, slider, checkBoxes)
        guardarEstadoFiltro(filtro)
    }
}