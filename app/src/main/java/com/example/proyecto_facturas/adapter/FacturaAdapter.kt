package com.example.proyecto_facturas.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_facturas.R
import com.example.proyecto_facturas.domain.Factura

class FacturaAdapter (private val facturaLista:List<Factura>, private val onClickListener: (Factura)
-> Unit): RecyclerView.Adapter<FacturaViewHolder>() {
    //TODO hacer el adapter y el retrofit
    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FacturaViewHolder {
    val  layoutInflater = LayoutInflater.from(parent.context)
        return FacturaViewHolder(
            layoutInflater.inflate(
                R.layout.lista_facturas,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
       return facturaLista.size
    }

    override fun onBindViewHolder(holder: FacturaViewHolder, position: Int) {
        val item = facturaLista[position]
        holder.render(item,onClickListener)
    }
}