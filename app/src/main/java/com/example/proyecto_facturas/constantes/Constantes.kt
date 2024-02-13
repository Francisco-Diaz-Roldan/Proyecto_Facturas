package com.example.proyecto_facturas.constantes

class Constantes {//Esto son valores de variables
    /** Companion object que contiene las constantes.*/
    companion object {

        // Constantes que almacenan los valores de las CheckBoxes
        /** Clave utilizada para el CheckBox asociado al estado 'Pagadas'.*/
        const val PAGADAS = "Pagadas"

        /** Clave utilizada para el CheckBox asociado al estado 'Anuladas'.*/
        const val ANULADAS = "Anuladas"

        /** Clave utilizada para el CheckBox asociado al estado 'Cuota fija'.*/
        const val CUOTA_FIJA = "Cuota fija"

        /** Clave utilizada para el CheckBox asociado al estado 'Pendiente de pago'.*/
        const val PENDIENTES_DE_PAGO = "Pendiente de pago"

        /** Clave utilizada para el CheckBox asociado al estado 'Plan de pago'.*/
        const val PLAN_DE_PAGO = "Plan de pago"

        // Constantes relativas al Intent
        /** Clave asociada al valor máximo de las facturas.*/
        const val VALOR_MAX = "valorMax"

        /** Clave asociada a los datos filtrados.*/
        const val DATOS_FILTRADOS = "datosFiltrados"

        /** Clave para la lista filtrada.*/
        const val LISTA_FILTRADA = "lista_filtrada"

        /**  Clave para el estado del filtro.*/
        const val ESTADO_FILTRO = "ESTADO_FILTRO"

        /** Clave para el estado del switch.*/
        const val ESTADO_SWITCH = "ESTADO_SWITCH"

        //Constantes relativas a las Preferencias Compartidas
        /** Clave para las preferencias de filtrado.*/
        const val PREFERENCIAS_FILTRADO = "preferencias_filtrado"

        /** Clave para las preferencias de la aplicación.*/
        const val PREFERENCIAS_APP = "preferencias_app"

        //Constante relativas a la API
        /** Clave asociada a la URL base para las solicitudes a la API.*/
        const val BASE_URL = "https://viewnextandroid.wiremockapi.cloud/"

        /** Clave utilizada para tomar el valor 'ficticio' al seleccionar los datos por la API.*/
        const val FICTICIO = "ficticio"

        /** Clave utilizada para tomar el valor 'real' al seleccionar los datos por la API.*/
        const val REAL = "real"

        //Constante relativa al formato de la fecha
        /** Clave para el formato de fecha "dd/MM/yyyy".*/
        const val DD_MM_YYYY = "dd/MM/yyyy"


    }
}
