package com.example.proyecto_facturas.adapter

import android.view.ContextMenu
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_facturas.databinding.ListaFacturasBinding
import com.example.proyecto_facturas.domain.Factura

class FacturaViewHolder(view: View):RecyclerView.ViewHolder(view){
    //TODO hacer con retrofit
    val binding = ListaFacturasBinding.bind(view)
    private lateinit var factura: Factura
    fun render(item: Factura, onClickListener:(Factura) -> Unit){
        binding.tvEstadoFactura.text = item.descEstado
        binding.tvFechaFactura.text = item.fecha
        binding.tvPrecioFactura.text = item.importeOrdenacion.toString()
        itemView.setOnClickListener{
            onClickListener(item)
        }
        factura = item
    }
}

