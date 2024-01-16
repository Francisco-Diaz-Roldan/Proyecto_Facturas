package com.example.proyecto_facturas.adapter

import com.example.proyecto_facturas.model.Factura

class FacturaProvider { //TODO hacer con retrofit

    companion object {
        var listaFacturas = mutableListOf(
            Factura(0,"Pendiente de pago", 1.56, "07/02/2019"),
            Factura(1,"Pagada", 25.14, "05/02/2019"),
            Factura(2,"Pagada", 22.69, "08/01/2019"),
            Factura(3,"Pendiente de pago", 12.84, "07/12/2018"),
            Factura(4,"Pagada", 35.16, "16/11/2018"),
            Factura(5,"Pagada", 18.27, "05/10/2018"),
            Factura(6,"Pendiente de pago", 61.17, "05/09/2018"),
            Factura(7,"Pagada", 37.18, "07/08/2018")

        )
    }
}