package com.example.proyecto_facturas.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.icu.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyecto_facturas.Filtro
import com.example.proyecto_facturas.R
import com.example.proyecto_facturas.adapter.FacturaAdapter
import com.example.proyecto_facturas.constantes.Constantes.Companion.ANULADAS
import com.example.proyecto_facturas.constantes.Constantes.Companion.CUOTA_FIJA
import com.example.proyecto_facturas.constantes.Constantes.Companion.PAGADAS
import com.example.proyecto_facturas.constantes.Constantes.Companion.PENDIENTES_DE_PAGO
import com.example.proyecto_facturas.constantes.Constantes.Companion.PLAN_DE_PAGO
import com.example.proyecto_facturas.databinding.ActivityMainBinding
import com.example.proyecto_facturas.data.rom.Factura
import com.example.proyecto_facturas.viewmodel.FacturaViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date
import java.util.Locale


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var listaFactura: MutableList<Factura> = mutableListOf()
    private lateinit var binding: ActivityMainBinding
    //private lateinit var intentLaunch: ActivityResultLauncher<Intent>
    private lateinit var facturaAdapter: FacturaAdapter
    private var valorMax: Double = 0.0
    //private lateinit var retrofitServiceInterface: RetrofitServiceInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializo el adaptador
        inicializarAdapter()
        //Inicializo la configuración del RecyclerView
        initViewModel()
        //Inicializa la vista(ViewModel) y las listas de facturas
        initMainViewModel()

        //Aquí m
        //TODO seguir por aqui

        this.onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
        setToolbar()
    }

    private fun inicializarAdapter() {
        facturaAdapter = FacturaAdapter() { factura ->
            onItemSelected(factura)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initMainViewModel() {
        val viewModel = ViewModelProvider(this).get(FacturaViewModel::class.java)
        viewModel.getAllRepositoryList().observe(this, Observer<List<Factura>> {
            facturaAdapter.setListaFacturas(it)
            facturaAdapter.notifyDataSetChanged()
            if (it.isEmpty()) {
                viewModel.llamarApi()
                Log.d("Datos", it.toString())
            }
            //TODO Usar un preferencias compartidas para almacenar los datos filtrados

            //Para Filtrar los datos. Obtenemos los datos de la actividad FiltradoActivity
            var datosFiltrados = intent.getStringExtra("datosFiltrados")
            Log.d("hoa2", datosFiltrados.toString())
            if (datosFiltrados != null){
               // Convierte strings JSON (datosFiltrados) en objeto  de tipo Filtro utilizando Gson
                var filtrosAplicados = Gson().fromJson(datosFiltrados, Filtro::class.java)
                Log.d("holaaaaaaa", filtrosAplicados.toString())
                var listaFactura = it

                //Aplicamos los filtros de fecha
                listaFactura = comprobarFechaFiltrado(filtrosAplicados.fechaDesde, filtrosAplicados.fechaHasta, listaFactura)
                Log.d("FiltroFecha", listaFactura.toString())
                //Aplicamos los filtros de importe
                Log.d("FiltroImporte", listaFactura.toString())
                listaFactura = comprobarImporteFiltrado(filtrosAplicados.importe, listaFactura)
                //Aplicamos los filtros de estado de las checkBoxes
                listaFactura = comprobarEstadoPagoFiltrado(filtrosAplicados.estado, listaFactura)
                Log.d("FiltroEstado", listaFactura.toString())

                //Mostramos por pantalla la lista filtrada
                facturaAdapter.setListaFacturas(listaFactura)
            }

            //Para el valor máximo de la lista
            valorMax = calcularMaximo(it)
        })
    }

    private fun initViewModel() {
        binding.rvFacturas.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            inicializarAdapter()
            adapter = facturaAdapter
        }
    }

    // Para los filtros de la fecha
    private fun comprobarFechaFiltrado(fechaDesdeStr: String, fechaHastaStr: String, listaFacturas: List<Factura>): List<Factura> {
        // Creo una segunda lista para devolverla despues con los datos filtrados
        val listaFiltradaPorFecha = ArrayList<Factura>()

        //En caso de que se haya modificado la fecha en la pantalla de fitrado
        if (fechaDesdeStr != getString(R.string.dia_mes_ano) && fechaHastaStr != getString(R.string.dia_mes_ano)) {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            val fechaDesde: Date? = sdf.parse(fechaDesdeStr)
            val fechaHasta: Date? = sdf.parse(fechaHastaStr)

            //Añado a la lista lista filtrada las fechas comprendidas entre el rango de dias desde y hasta
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
    }

    //Para filtrar por importe
    private fun comprobarImporteFiltrado(importe: Double, listaFactura: List<Factura>):List<Factura>{
        // Creo una segunda lista para devolverla despues con los datos filtrados
        val listaFiltradaPorImporte = ArrayList<Factura>()
        // Si el importe de la factura es menor o igual que el seleccionado se añade a la lista
        for (factura in listaFactura){
            if (factura.importeOrdenacion!! <= importe){
                listaFiltradaPorImporte.add(factura)
            }
        }
        return listaFiltradaPorImporte
    }

    //Para filtrar por las checkboxes (estado de pago)
    private fun comprobarEstadoPagoFiltrado(estado:HashMap<String, Boolean>,
                                            listaFactura: List<Factura>):List<Factura>{
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
            && !chBoxPlanPago) {
            return listaFactura
        }
        // Comprobamos el estado de cada checkbox con respecto al JSON, no con respecto a los Strings/Constantes
        for (factura in listaFactura){
            val estadoFactura = factura.descEstado
            val estaPagada = estadoFactura == "Pagada"
            val estaAnulada = estadoFactura == "Anuladas"
            val estaCuotaFija = estadoFactura == "cuotaFija"
            val estaPendientePago = estadoFactura == "Pendiente de pago"
            val estaPlanPago = estadoFactura =="Plan de Pago"

            // Comprobamos si el estado de la factura coincide con alguna checkbox seleccionada
            if ((estaPagada && chBoxPagadas) || (estaAnulada && chBoxAnuladas) ||
                (estaCuotaFija && chBoxCuotaFija) || (estaPendientePago && chBoxPendientesPago) ||
                (estaPlanPago && chBoxPlanPago)) {
                //Si coincide, la añadimos a la lista filtrada por importe
                listaFiltradaPorEstado.add(factura)
            }
        }
        return listaFiltradaPorEstado
    }

    private fun setToolbar() {
        // Configuro la toolbar genérica
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Modifico el título de la barra de herramientas (toolbar)
        supportActionBar?.title = "Facturas"
    }


    private fun calcularMaximo(listaFactura: List<Factura>): Double {
        var importeMaximo = 0.0
        for (factura in listaFactura) {
            val facturaActual = factura.importeOrdenacion
            if(importeMaximo < facturaActual!!) importeMaximo = facturaActual
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