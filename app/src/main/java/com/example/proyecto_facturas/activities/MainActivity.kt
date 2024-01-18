package com.example.proyecto_facturas.activities

import android.content.Intent
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
import com.example.proyecto_facturas.R
import com.example.proyecto_facturas.adapter.FacturaAdapter
import com.example.proyecto_facturas.databinding.ActivityMainBinding
import com.example.proyecto_facturas.data.rom.Factura
import com.example.proyecto_facturas.viewmodel.FacturaViewModel
import dagger.hilt.android.AndroidEntryPoint


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
        // Inicializo RetrofitService
        // retrofitServiceInterface = RetrofitServiceInterface.instance()

        /*
        val facturaViewModel = ViewModelProvider(this, FacturaViewModelFactory(application, retrofitService))
            .get(FacturaViewModel::class.java)
        facturaViewModel.getAllFacturas.observe(this, Observer { factura ->
            adapter.setData(factura)
        })
        listaFactura = facturaViewModel



        //Inicializo el Provider
        //listaFactura = FacturaProvider.listaFacturas

        // Inicializo el adaptador
        adapter = FacturaAdapter(listaFactura) { factura ->
            onItemSelected(factura)
        }

        // Configuro el RecyclerView con el adaptador
        binding.rvFacturas.layoutManager = LinearLayoutManager(this)
        binding.rvFacturas.adapter = FacturaAdapter(listaFactura) { factura -> onItemSelected(factura) }

        // Configuro el ViewModel
*/
        initViewModel() //Inicializo la configuración del RecyclerView
        initMainViewModel()



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

    private fun initMainViewModel() {
        val viewModel = ViewModelProvider(this).get(FacturaViewModel::class.java)
        viewModel.getAllRepositoryList().observe(this, Observer<List<Factura>> {
            //TODO Usar un preferencias compartidas para almacenar los datos filtrados

            facturaAdapter.setData(it)
            facturaAdapter.notifyDataSetChanged()

            if (it.isEmpty()) {
                viewModel.llamarApi()
                Log.d("Datos", it.toString())
            }
        })
        valorMax = calcularMaximo()
    }

    private fun initViewModel() {
        binding.rvFacturas.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            inicializarAdapter()
            adapter = facturaAdapter
        }
    }

    private fun setToolbar() {
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