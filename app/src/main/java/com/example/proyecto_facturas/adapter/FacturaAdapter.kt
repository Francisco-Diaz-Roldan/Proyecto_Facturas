package com.example.proyecto_facturas.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_facturas.R
import com.example.proyecto_facturas.model.Factura

class FacturaAdapter(
    private var facturaLista: List<Factura>?=null,
    private val onClickListener: (Factura) -> Unit) : RecyclerView.Adapter<FacturaViewHolder>() {
    //TODO hacer el adapter y el retrofit
    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FacturaViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return FacturaViewHolder(
            layoutInflater.inflate(
                R.layout.lista_facturas,
                parent,
                false
            )
        )

    }

    override fun getItemCount(): Int {
        return facturaLista?.size ?: 0
    }

    override fun onBindViewHolder(holder: FacturaViewHolder, position: Int) {
        val item = facturaLista?.get(position)
        if (item != null) holder.render(item, onClickListener)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(factura: List<Factura>?){
        this.facturaLista = factura
        notifyDataSetChanged()
    }
}