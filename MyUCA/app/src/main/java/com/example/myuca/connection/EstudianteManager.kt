package com.example.myuca.connection

import android.net.Uri
import java.net.URL

class Estudiante(
    var id: Int,
    var nombres: String,
    var apellidos: String,
    var carrera: String,
    var año: Int
) {

    constructor() : this((Int.MIN_VALUE..Int.MAX_VALUE).random(), "", "", "", 1)

    fun getFullName(): String {
        return "$nombres $apellidos".trim()
    }

    override fun toString(): String {
        return "{ id: ${id}, nombres: ${nombres}, apellidos: ${apellidos}, carrera: ${carrera}, año: ${año} }"
    }
}

class EstudianteManager {

    companion object {
        fun getEstudiantes(id: Number? = null): Pair<Number, String> {
            var path = "getEstudiante.php"

            val uriBuilder = Uri.parse(path).buildUpon()
            if (id != null)
                uriBuilder.appendQueryParameter("id", id.toString())

            val conn = Connection(uriBuilder.build().toString())

            return conn.httpGet()
        }

        fun insertEstudiante(
            nombres: String,
            apellidos: String,
            carrera: String,
            año: Number
        ): Pair<Number, String> {
            val formData = listOf(
                "nombres" to nombres,
                "apellidos" to apellidos,
                "carrera" to carrera,
                "año" to año
            )
            val path = "insertEstudiante.php"
            val conn = Connection(path)

            return conn.httpPostMultipart(formData)
        }

        fun updateEstudiante(
            id: Number,
            nombres: String,
            apellidos: String,
            carrera: String,
            año: Number
        ): Pair<Number, String> {
            val formData = listOf(
                "id" to id,
                "nombres" to nombres,
                "apellidos" to apellidos,
                "carrera" to carrera,
                "año" to año
            )
            val path = "updateEstudiante.php"
            val conn = Connection(path)

            return conn.httpPostMultipart(formData)
        }

        fun deleteEstudiante(id: Number): Pair<Number, String> {
            val path = "deleteEstudiante.php"

            val uriBuilder = Uri.parse(path).buildUpon()
            uriBuilder.appendQueryParameter("id", id.toString())

            val conn = Connection(uriBuilder.build().toString())

            return conn.httpDelete()
        }
    }
}