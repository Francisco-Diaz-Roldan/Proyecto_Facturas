package com.example.proyecto_facturas.adapter

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_facturas.R
import com.example.proyecto_facturas.databinding.ListaFacturasBinding
import com.example.proyecto_facturas.domain.Factura

class FacturaViewHolder(view: View):RecyclerView.ViewHolder(view){ //TODO hacer con retrofit
    val binding = ListaFacturasBinding.bind(view)
    private lateinit var factura: Factura
    fun render(item: Factura, onClickListener:(Factura) -> Unit){
        binding.tvEstadoFactura.text = item.descEstado
        if (item.descEstado=="Pendiente de pago"){
            binding.tvEstadoFactura.setTextColor(ContextCompat.getColor(itemView.context,
                R.color.red))
        }else{
            binding.tvEstadoFactura.text = ""
        }
        binding.tvFechaFactura.text = item.fecha
        binding.tvPrecioFactura.text= item.importeOrdenacion.toString()  + " €"
        itemView.setOnClickListener{
            onClickListener(item)
        }
        factura = item
    }
}

