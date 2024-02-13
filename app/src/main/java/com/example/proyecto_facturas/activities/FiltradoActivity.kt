package com.example.proyecto_facturas.activities

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.CheckBox
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.proyecto_facturas.model.Filtro
import com.example.proyecto_facturas.R
import com.example.proyecto_facturas.constantes.Constantes
import com.example.proyecto_facturas.constantes.Constantes.Companion.ANULADAS
import com.example.proyecto_facturas.constantes.Constantes.Companion.CUOTA_FIJA
import com.example.proyecto_facturas.constantes.Constantes.Companion.DD_MM_YYYY
import com.example.proyecto_facturas.constantes.Constantes.Companion.ESTADO_FILTRO
import com.example.proyecto_facturas.constantes.Constantes.Companion.PAGADAS
import com.example.proyecto_facturas.constantes.Constantes.Companion.PENDIENTES_DE_PAGO
import com.example.proyecto_facturas.constantes.Constantes.Companion.PLAN_DE_PAGO
import com.example.proyecto_facturas.constantes.Constantes.Companion.PREFERENCIAS_FILTRADO
import com.example.proyecto_facturas.databinding.ActivityFiltradoBinding
import com.google.gson.Gson
import java.util.Date
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
    private lateinit var intentLaunchActivityResult: ActivityResultLauncher<Intent>
    private var filtro: Filtro? = null

    private var fechaDesdeSeleccionada = false
    private var fechaHastaSeleccionada = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFiltradoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inicializarComponentes()

        inicializarPreferenciasCompartidas()

        aplicarFiltrosGuardados()

        inicializarintentLaunchActivityResult()
    }

    private fun inicializarComponentes() {
        configurarBotonesFecha()
        configurarSeekbar()
        configurarCheckBoxes()
        configurarBotonesFiltros()
        configurarToolbar()
    }

    private fun configurarBotonesFiltros() {
        configurarBotonEliminarFiltros()
        configurarBotonAplicarFiltros()
    }

    private fun inicializarintentLaunchActivityResult() {
        intentLaunchActivityResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result: ActivityResult ->
                if (result.resultCode == RESULT_OK) {
                    val filtroJson = result.data?.extras?.getString(Constantes.DATOS_FILTRADOS)
                    if (filtroJson != null) {
                        val gson = Gson()
                        val objFiltro = gson.fromJson(filtroJson, MainActivity::class.java)
                        val valorMax = result.data?.extras?.getDouble(Constantes.VALOR_MAX) ?: 0.0
                    }
                }
            }
    }

    private fun configurarBotonesFecha() {
        configurarBotonDesde()
        configurarBotonHasta()
    }

    private fun configurarBotonDesde() {
        btnDesde = binding.btnDesde
        btnDesde.setOnClickListener {
            fechaDesdeSeleccionada = true
            obtenerFecha(btnDesde, restriccionFechaMin = false)
        }
    }

    private fun configurarBotonHasta() {
        btnHasta = binding.btnHasta
        btnHasta.setOnClickListener {
            fechaHastaSeleccionada = true
            obtenerFecha(
                btnHasta,
                restriccionFechaMin = true,
                restriccionFechaMax = true,
                fechaMinima = obtenerFechaDesde()
            )
        }
    }

    @SuppressLint("SetTextI18n")
    private fun obtenerFecha(
        button: Button,
        restriccionFechaMax: Boolean = false,
        restriccionFechaMin: Boolean = false,
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
        restriccionFechaMin.let {
            datePickerDialog.datePicker.minDate = fechaMinima ?: 0
        }

        restriccionFechaMax.let {
            val calendarioMax = Calendar.getInstance()
            calendarioMax.add(Calendar.YEAR, 100) // Establezco la fecha maxima en 100 años
            datePickerDialog.datePicker.maxDate = calendarioMax.timeInMillis
        }

        if (btnHasta.text != getString(R.string.dia_mes_ano)) {
            val maxDate = obtenerFechaHasta(btnHasta.text.toString())
            datePickerDialog.datePicker.maxDate = maxDate.time
        }
        datePickerDialog.show()
    }

    private fun obtenerFechaDesde(): Long {
        val formato = SimpleDateFormat(DD_MM_YYYY, Locale.getDefault())
        val fechaDesdeTexto = btnDesde.text.toString()
        try {
            val fechaDesde = formato.parse(fechaDesdeTexto)
            return fechaDesde?.time ?: 0L
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0L // En caso de error devuelve el valor predeterminado
    }

    private fun obtenerFechaHasta(dateText: String): Date {
        val dateFormat = SimpleDateFormat(DD_MM_YYYY, Locale.getDefault())
        return dateFormat.parse(dateText) ?: Date()
    }


    private fun inicializarPreferenciasCompartidas() {
        preferenciasCompartidas = getSharedPreferences(
            PREFERENCIAS_FILTRADO, Context.MODE_PRIVATE
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
        return intent.getDoubleExtra(Constantes.VALOR_MAX, 0.0).toInt() + 1
    }

    private fun configurarToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Cambio el color de la barra de herramientas a blanco
        toolbar.setBackgroundColor(Color.parseColor("#FFFFFFFF"))

        // Creo un SpannableString para aplicar estilos al título
        val spannableString = SpannableString("Filtrar facturas")

        // Aplico el estilo bold al texto
        spannableString.setSpan(
            StyleSpan(Typeface.BOLD),
            0, // Inicio del texto
            spannableString.length, // Fin del texto
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Establezco el tamaño de texto en píxeles
        val tamanoTextoEnPixeles = resources.getDimensionPixelSize(R.dimen.tamano_texto_toolbar)
        spannableString.setSpan(
            AbsoluteSizeSpan(tamanoTextoEnPixeles),
            0,
            spannableString.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Establezco el título de la barra de herramientas con el SpannableString
        supportActionBar?.title = spannableString
    }

    private fun configurarBotonAplicarFiltros() {
        binding.btnAplicar.setOnClickListener {
            val gson = Gson()
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

            val fechaDesdeReal = if (fechaDesdeSeleccionada) fechaDesde else "01/01/1900"
            val fechaHastaReal = if (fechaHastaSeleccionada) fechaHasta else "31/12/9999"

            val importe = binding.seekbarImporte.progress.toDouble()
            val filtro = Filtro(fechaHastaReal, fechaDesdeReal, importe, estadoCheckBox)
            intent.putExtra(Constantes.DATOS_FILTRADOS, gson.toJson(filtro))
            cargarPreferenciasCompartidas()
            intentLaunchActivityResult.launch(intent)
            finish()
        }
    }

    private fun instanciarGson(): Gson {
        return Gson()
    }

    private fun configurarBotonEliminarFiltros() {
        binding.btnEliminarFiltros.setOnClickListener {
            resetearParametros()
        }
    }

    private fun resetearParametros() {
        resetearFecha()
        resetearSeekbar()
        resetearCheckBoxes()
    }

    private fun configurarCheckBoxes() {
        checkboxPagada = binding.checkboxPagada
        checkboxAnuladas = binding.checkboxAnuladas
        checkboxCuotaFija = binding.checkboxCuotaFija
        checkboxPendientesDePago = binding.checkboxPendientesDePago
        checkboxPlanDePago = binding.checkboxPlanDePago
    }

    private fun resetearFecha() {
        btnDesde.text = getString(R.string.dia_mes_ano)
        btnHasta.text = getString(R.string.dia_mes_ano)
        fechaDesdeSeleccionada = false
        fechaHastaSeleccionada = false
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
        val preferencias = getSharedPreferences(PREFERENCIAS_FILTRADO, Context.MODE_PRIVATE)
        val gson = instanciarGson()
        val jsonFiltro = gson.toJson(filtro)
        preferencias.edit().putString(ESTADO_FILTRO, jsonFiltro).apply()
    }


    private fun aplicarFiltrosGuardados() {
        val preferencias = getSharedPreferences((PREFERENCIAS_FILTRADO), Context.MODE_PRIVATE)
        val filtroJson = preferencias.getString(ESTADO_FILTRO, null)

        if (filtroJson != null) {
            val gson = Gson()
            filtro = gson.fromJson(filtroJson, Filtro::class.java)
            filtro?.let { nonNullFilter ->
                cargarFiltros(nonNullFilter)
            }
        }
    }

    private fun cargarFiltros(filtro: Filtro) {
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