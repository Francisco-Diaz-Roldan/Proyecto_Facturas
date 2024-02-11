package com.example.proyecto_facturas.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyecto_facturas.Filtro
import com.example.proyecto_facturas.R
import com.example.proyecto_facturas.adapter.FacturaAdapter
import com.example.proyecto_facturas.constantes.Constantes
import com.example.proyecto_facturas.constantes.Constantes.Companion.ANULADAS
import com.example.proyecto_facturas.constantes.Constantes.Companion.CUOTA_FIJA
import com.example.proyecto_facturas.constantes.Constantes.Companion.DATOS_FILTRADOS
import com.example.proyecto_facturas.constantes.Constantes.Companion.ESTADO_SWITCH
import com.example.proyecto_facturas.constantes.Constantes.Companion.FICTICIO
import com.example.proyecto_facturas.constantes.Constantes.Companion.PAGADAS
import com.example.proyecto_facturas.constantes.Constantes.Companion.PENDIENTES_DE_PAGO
import com.example.proyecto_facturas.constantes.Constantes.Companion.PLAN_DE_PAGO
import com.example.proyecto_facturas.constantes.Constantes.Companion.REAL
import com.example.proyecto_facturas.data.rom.Factura
import com.example.proyecto_facturas.databinding.ActivityMainBinding
import com.example.proyecto_facturas.viewmodel.FacturaViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import java.text.ParseException
import java.util.Date
import java.util.Locale


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var facturaAdapter: FacturaAdapter
    private var valorMax: Double = Double.MIN_VALUE // Inicio por defecto el valor más pequeño
    private var filtro: Filtro? = null
    private lateinit var intentLaunchActivityResult: ActivityResultLauncher<Intent>
    private val preferenciasCompartidas: SharedPreferences by lazy {
        getSharedPreferences("preferencias_app", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBackPressedDispatcher.addCallback(this, onBackCallback)//Para salir de la app al darle a atrás

        inicializarAdapter()

        inicializarViewModel()

        inicializarDividerDecoration()

        inicializarMainViewModel()

        configurarFacturaAdapter()

        configurarToolbar()

        inicializarintentLaunchActivityResult()
    }

    private fun inicializarintentLaunchActivityResult() {
        intentLaunchActivityResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result: ActivityResult ->
                if (result.resultCode == RESULT_OK) {
                    valorMax = result.data?.extras?.getDouble(Constantes.VALOR_MAX)
                        ?: 0.0// De no ser una constante tendría que ir entre comillado
                    val filtroJson = result.data?.extras?.getString(DATOS_FILTRADOS)
                    Log.d("hola", "Filtro recibido: $filtroJson") //TODO no se reciben bien las preferencias de fecha
                    if (filtroJson != null) {
                        val gson = Gson()
                        val objetoFiltrado = gson.fromJson(filtroJson, FiltradoActivity::class.java)
                        println(objetoFiltrado)
                    }
                }
            }
        val estadoSwitch = cargarEstadoSwitch()
        binding.switchRetromock.isChecked = estadoSwitch
    }

    private fun guardarEstadoSwitch(state: Boolean) {
        val prefs = getPreferences(MODE_PRIVATE)
        prefs.edit().putBoolean(ESTADO_SWITCH, state).apply()
    }

    private fun cargarEstadoSwitch(): Boolean {
        val prefs: SharedPreferences = getPreferences(MODE_PRIVATE)
        return prefs.getBoolean(ESTADO_SWITCH, false)
    }

    private fun configurarFacturaAdapter() {
        // Declaro las preferencias compartidas
        val listaFiltradaGuardada = obtenerListaFiltradaDesdePreferencias()

        facturaAdapter.setListaFacturas(listaFiltradaGuardada ?: emptyList())

        this.onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    private fun inicializarDividerDecoration() {
        // Crear un objeto DividerItemDecoration
        val itemDecorator = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)

        // Asigno el decorador al RecyclerView
        binding.rvFacturas.addItemDecoration(itemDecorator)
    }

    private fun inicializarAdapter() {
        facturaAdapter = FacturaAdapter { onItemSelected() }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun inicializarMainViewModel() {
        // Inicializa la vista(ViewModel) y las listas de facturas
        val viewModel = ViewModelProvider(this)[FacturaViewModel::class.java]

        // Obtengo la lista filtrada desde las preferencias compartidas
        val listaFiltradaGuardada = obtenerListaFiltradaDesdePreferencias()

        viewModel.getAllRepositoryList().observe(this) { listaCompleta ->
            // Uso la lista filtrada si está presente; de lo contrario, usar la lista completa
            val listaFactura = listaFiltradaGuardada ?: listaCompleta

            facturaAdapter.setListaFacturas(listaFactura)

            // Si la lista completa está vacía, llamo a la API para obtener datos
            if (listaCompleta.isEmpty()) {
                viewModel.llamarApi()
            }
            binding.switchRetromock.setOnClickListener{
                val isChecked = binding.switchRetromock.isChecked
                guardarEstadoSwitch(isChecked) 
                if(binding.switchRetromock.isChecked){
                    viewModel.cambiarServicio(FICTICIO)
                    viewModel.llamarApi()
                }else{
                    viewModel.cambiarServicio(REAL)
                    viewModel.llamarApi()
                }
            }

            // Para Filtrar los datos. Se obtienen los datos de la actividad FiltradoActivity
            val datosFiltrados = intent.getStringExtra(Constantes.DATOS_FILTRADOS)
            if (datosFiltrados != null) {
                // Convierto strings JSON (datosFiltrados) en objeto de tipo Filtro utilizando Gson
                val filtrosAplicados = Gson().fromJson(datosFiltrados, Filtro::class.java)

                // Aplico los filtros de fecha
                var listaFiltrada = comprobarFechaFiltrado(
                    filtrosAplicados.fechaDesde,
                    filtrosAplicados.fechaHasta,
                    listaCompleta // Uso la lista completa iterada aquí
                )

                // Aplico los filtros de importe
                listaFiltrada = comprobarImporteFiltrado(filtrosAplicados.importe, listaFiltrada)

                // Aplico los filtros de estado de las checkBoxes
                listaFiltrada = comprobarEstadoPagoFiltrado(filtrosAplicados.estado, listaFiltrada)

                // Guardo la lista filtrada en preferencias
                guardarListaFiltradaEnPreferencias(listaFiltrada)

                // Muestro un texto en caso de que la lista esté vacía
                val tvSinFacturas = binding.tvSinFacturas
                tvSinFacturas.visibility = if (listaFiltrada.isEmpty()) View.VISIBLE else View.GONE

                // Muestro por pantalla la lista filtrada
                facturaAdapter.setListaFacturas(listaFiltrada)
            }

            // Si hay preferencias compartidas y no hay datos filtrados, cargo la lista filtrada
            if (listaFiltradaGuardada != null && datosFiltrados == null) {
                val tvSinFacturas = binding.tvSinFacturas
                tvSinFacturas.visibility = if (listaFiltradaGuardada.isEmpty())
                    View.VISIBLE else View.GONE

                // Muestro por pantalla la lista filtrada guardada
                facturaAdapter.setListaFacturas(listaFiltradaGuardada)
            }
            // Actualizo el valor máximo solo con la lista completa
            valorMax = calcularMaximo(listaCompleta)
        }
    }

    private fun inicializarViewModel() {
        //Inicializo la configuración del RecyclerView
        binding.rvFacturas.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            inicializarAdapter()
            adapter = facturaAdapter
        }
    }

    /*private fun comprobarFechaFiltrado(// Para los filtros de la fecha
        fechaDesdeStr: String,
        fechaHastaStr: String,
        listaFacturas: List<Factura>
    ): List<Factura> {
        // Creo una segunda lista para devolverla despues con los datos filtrados
        val listaFiltradaPorFecha = ArrayList<Factura>()

        //En caso de que se haya modificado la fecha en la pantalla de fitrado
        if (fechaDesdeStr != getString(R.string.dia_mes_ano) && fechaHastaStr !=
            getString(R.string.dia_mes_ano)
        ) {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val fechaDesde: Date? = sdf.parse(fechaDesdeStr)
            val fechaHasta: Date? = sdf.parse(fechaHastaStr)

            //Añado a la lista listaFiltrada las fechas comprendidas entre desde y hasta
            for (factura in listaFacturas) {
                val fecha = sdf.parse(factura.fecha)!!
                if (fecha.after(fechaDesde) && fecha.before(fechaHasta)) {
                    listaFiltradaPorFecha.add(factura)
                }
            }
            //Si no se ha modificado la lista en la pantalla de filtros devuelve la lista original
        } else {
            return listaFacturas
        }
        return listaFiltradaPorFecha //Devuelvo la lista filtrada por fecha
    }*/

   /* private fun comprobarFechaFiltrado(
        fechaDesdeStr: String,
        fechaHastaStr: String,
        listaFacturas: List<Factura>
    ): List<Factura> {
        // Creo una lista para almacenar las facturas filtradas por fecha
        val listaFiltradaPorFecha = mutableListOf<Factura>()

        // Verifico si se ha seleccionado un rango de fechas en la pantalla de filtros
        if (fechaDesdeStr != getString(R.string.dia_mes_ano) && fechaHastaStr != getString(R.string.dia_mes_ano)) {
            // Creo un formato de fecha para convertir las cadenas de fecha a objetos Date
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            try {
                // Convierto las cadenas de fecha a objetos Date
                val fechaDesde: Date = sdf.parse(fechaDesdeStr) ?: Date(Long.MIN_VALUE)
                val fechaHasta: Date = sdf.parse(fechaHastaStr) ?: Date(Long.MAX_VALUE)

                // Itero sobre la lista completa de facturas
                for (factura in listaFacturas) {
                    // Obtengo la fecha de la factura como un objeto Date
                    val fechaFactura: Date = sdf.parse(factura.fecha) ?: Date()

                    // Verifico si la fecha de la factura está dentro del rango seleccionado
                    if (!fechaFactura.before(fechaDesde) && !fechaFactura.after(fechaHasta)) {
                        // Agrego la factura a la lista filtrada
                        listaFiltradaPorFecha.add(factura)
                    }
                }
            } catch (e: ParseException) {
                // Manejo de excepciones en caso de errores al analizar fechas
                e.printStackTrace()
            }
        } else {
            // Si no se ha modificado la lista en la pantalla de filtros, devuelvo la lista original
            return listaFacturas
        }

        // Devuelvo la lista filtrada por fecha
        return listaFiltradaPorFecha
    }*/

    private fun comprobarFechaFiltrado(
        fechaDesdeStr: String,
        fechaHastaStr: String,
        listaFacturas: List<Factura>
    ): List<Factura> {
        // Compruebo si se ha seleccionado un rango de fechas en la pantalla de filtros
        if (fechaDesdeStr == getString(R.string.dia_mes_ano) || fechaHastaStr == getString(R.string.dia_mes_ano)) {
            // Si no se ha modificado la lista en la pantalla de filtros, devuelvo la lista original
            return listaFacturas
        }

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        try {
            val fechaDesde: Date = sdf.parse(fechaDesdeStr) ?: Date(Long.MIN_VALUE)
            val fechaHasta: Date = sdf.parse(fechaHastaStr) ?: Date(Long.MAX_VALUE)

            return listaFacturas.filter { factura ->
                val fechaFactura: Date = sdf.parse(factura.fecha) ?: Date()
                !fechaFactura.before(fechaDesde) && !fechaFactura.after(fechaHasta)
            }
        } catch (e: ParseException) {
            e.printStackTrace()
            return emptyList()
        }
    }

    private fun comprobarImporteFiltrado(//Para filtrar por importe
        importe: Double, listaFactura: List<Factura>
    ): List<Factura> {
        // Creo una segunda lista para devolverla despues con los datos filtrados
        val listaFiltradaPorImporte = ArrayList<Factura>()
        // Si el importe de la factura es menor o igual que el seleccionado se añade a la lista
        for (factura in listaFactura) {
            if (factura.importeOrdenacion!! <= importe) {
                listaFiltradaPorImporte.add(factura)
            }
        }
        return listaFiltradaPorImporte
    }

    //Para filtrar por las checkboxes (estado de pago)
    private fun comprobarEstadoPagoFiltrado(
        estado: HashMap<String, Boolean>, listaFactura: List<Factura>
    ): List<Factura> {
        // Creo una segunda lista para devolverla despues con los datos filtrados
        val listaFiltradaPorEstado = ArrayList<Factura>()

        //Declaro las checkBoxes (recibe el texto)
        val chBoxPagadas = estado[PAGADAS] ?: false
        val chBoxAnuladas = estado[ANULADAS] ?: false
        val chBoxCuotaFija = estado[CUOTA_FIJA] ?: false
        val chBoxPendientesPago = estado[PENDIENTES_DE_PAGO] ?: false
        val chBoxPlanPago = estado[PLAN_DE_PAGO] ?: false

        //En caso de no haber seleccionado ninguna checkbox
        if (!chBoxPagadas && !chBoxAnuladas && !chBoxCuotaFija && !chBoxPendientesPago
            && !chBoxPlanPago
        ) {
            return listaFactura
        }
        // Compruebo el estado de cada checkbox con respecto al JSON, no con respecto a
        // Strings/Constantes
        for (factura in listaFactura) {
            val estadoFactura = factura.descEstado
            val estaPagada = estadoFactura == "Pagada"
            val estaAnulada = estadoFactura == "Anuladas"
            val estaCuotaFija = estadoFactura == "cuotaFija"
            val estaPendientePago = estadoFactura == "Pendiente de pago"
            val estaPlanPago = estadoFactura == "Plan de Pago"

            // Compruebo si el estado de la factura coincide con alguna checkbox seleccionada
            if ((estaPagada && chBoxPagadas) || (estaAnulada && chBoxAnuladas) ||
                (estaCuotaFija && chBoxCuotaFija) || (estaPendientePago && chBoxPendientesPago) ||
                (estaPlanPago && chBoxPlanPago)) { listaFiltradaPorEstado.add(factura) }
        }
        return listaFiltradaPorEstado
    }

    private fun configurarToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Cambio el color de la barra de herramientas a blanco
        toolbar.setBackgroundColor(Color.parseColor("#FFFFFFFF"))

        // Creo un SpannableString para aplicar estilos al título
        val spannableString = SpannableString("Facturas")

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

    private fun calcularMaximo(listaFactura: List<Factura>): Double {
        var importeMaximo = 0.0
        for (factura in listaFactura) {
            val facturaActual = factura.importeOrdenacion
            if (importeMaximo < facturaActual!!) importeMaximo = facturaActual
        }
        return importeMaximo
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_filtros, menu)
        return true
    }

    private fun onItemSelected() {
        mostrarDialogAlerta()
    }

    private fun mostrarDialogAlerta() {
        val builder = AlertDialog.Builder(this)

        // Configuro el diálogo
        builder.setTitle("Información")
        builder.setMessage("Esta funcionalidad aún no está disponible")

        // Botón negativo Cerrar
        builder.setNegativeButton("Cerrar") { dialog, which -> dialog.dismiss() }
        // Creo y enseño el diálogo
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menuFiltrar -> {
                val intent = Intent(this, FiltradoActivity::class.java)
                intent.putExtra(Constantes.VALOR_MAX, valorMax)
                if (filtro != null) {
                    val gson = Gson()
                    intent.putExtra(Constantes.DATOS_FILTRADOS, gson.toJson(filtro))
                }
                intentLaunchActivityResult.launch(intent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    // Métodos para guardar y obtener la lista filtrada desde preferencias compartidas
    private fun guardarListaFiltradaEnPreferencias(listaFiltrada: List<Factura>) {
        val gson = Gson()
        val listaFiltradaJson = gson.toJson(listaFiltrada)
        preferenciasCompartidas.edit().putString(Constantes.LISTA_FILTRADA, listaFiltradaJson).apply()
    }

    private fun obtenerListaFiltradaDesdePreferencias(): List<Factura>? {
        val listaFiltradaJson = preferenciasCompartidas.getString(
            Constantes.LISTA_FILTRADA,
            null
        )
        if (listaFiltradaJson != null) {
            val gson = Gson()
            val tipoLista = object : TypeToken<List<Factura>>() {}.type
            return gson.fromJson(listaFiltradaJson, tipoLista)
        }
        return null
    }

    private val onBackCallback = object : OnBackPressedCallback(true){
        override fun handleOnBackPressed() {
            finishAffinity()
        }
    }
}