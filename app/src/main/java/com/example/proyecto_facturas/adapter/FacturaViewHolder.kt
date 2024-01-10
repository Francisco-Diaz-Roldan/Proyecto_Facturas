package com.example.proyecto_facturas.adapter

import android.annotation.SuppressLint
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_facturas.R
import com.example.proyecto_facturas.databinding.ListaFacturasBinding
import com.example.proyecto_facturas.domain.Factura
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class FacturaViewHolder(view: View) : RecyclerView.ViewHolder(view) { //TODO hacer con retrofit
    private val binding = ListaFacturasBinding.bind(view)
    private lateinit var factura: Factura

    @SuppressLint("SetTextI18n")
    fun render(item: Factura, onClickListener: (Factura) -> Unit) {
        binding.tvEstadoFactura.text = item.descEstado
        if (item.descEstado == "Pendiente de pago") {
            binding.tvEstadoFactura.setTextColor(
                ContextCompat.getColor(
                    itemView.context,
                    R.color.red
                )
            )
        } else {
            binding.tvEstadoFactura.text = ""
        }
        binding.tvFechaFactura.text = formatearFecha(item.fecha)
        binding.tvPrecioFactura.text = item.importeOrdenacion.toString() + " €"
        itemView.setOnClickListener {
            onClickListener(item)
        }
        factura = item
    }

    private fun formatearFecha(fechaString: String): String {
        try {
            val formatoEntrada = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val fecha = formatoEntrada.parse(fechaString)

            // Formateo la fecha a "dd MMM yyyy" en español
            val formatoSalida = SimpleDateFormat(
                "dd MMM yyyy", Locale(
                    "es",
                    "ES"
                )
            )
            return formatoSalida.format(fecha!!)
        } catch (e: ParseException) {
            e.printStackTrace()
            return fechaString // Devuelve la fecha original en caso de error
        }
    }
}

