package com.example.proyecto_facturas

/*la clase Filtro actúa como un modelo de datos que define la estructura de los filtros aplicados,*/
class Filtro(
    var fechaHasta: String,
    var fechaDesde: String,
    var importe: Double,
    var estado: HashMap<String, Boolean>
)
