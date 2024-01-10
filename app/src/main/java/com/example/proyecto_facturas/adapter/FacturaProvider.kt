package com.example.proyecto_facturas.adapter

import com.example.proyecto_facturas.domain.Factura

class FacturaProvider {
    //TODO hacer con retrofit

companion object {
    var listaFacturas = mutableListOf(
        Factura("Pendiente de pago", 1.56, "07/02/2019"),
        Factura("Pagada", 25.14, "05/02/2019"),
        Factura("Pagada", 22.69, "08/01/2019"),
        Factura("Pendiente de pago", 12.84, "07/12/2018"),
        Factura("Pagada", 35.16, "16/11/2018"),
        Factura("Pagada", 18.27, "05/10/2018"),
        Factura("Pendiente de pago", 61.17, "05/09/2018"),
        Factura("Pagada", 37.18,  "07/08/2018")

    )
}

}