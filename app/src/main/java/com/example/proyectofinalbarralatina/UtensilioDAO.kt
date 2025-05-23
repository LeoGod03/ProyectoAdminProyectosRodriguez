package com.example.proyectofinalbarralatina

import android.content.ContentValues
import com.example.proyectofinalbarralatina.dataclass.Utensilio

class UtensilioDAO(private val dbHelper: DatabaseHelper) {

    //Insertar un nuevo utensilio
    fun agregarUtensilio(utensilio: Utensilio) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("nombre", utensilio.nombre)
            put("descripcion", utensilio.descripcion)
            put("cantidad_inventario", utensilio.cantidadInventario)
        }
        val resultado = db.insert("utensilios", null, values)
        db.close()
        utensilio.id = resultado.toInt() // Devuelve el ID del utensilio insertado
    }

    //  Obtener todos los utensilios
    fun obtenerUtensilios(): List<Utensilio> {
        val listaUtensilios = mutableListOf<Utensilio>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM utensilios", null)

        while (cursor.moveToNext()) {
            val utensilio = Utensilio(
                cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                cursor.getString(cursor.getColumnIndexOrThrow("descripcion")),
                cursor.getInt(cursor.getColumnIndexOrThrow("cantidad_inventario"))
            )
            listaUtensilios.add(utensilio)
        }

        cursor.close()
        db.close()
        return listaUtensilios
    }

    fun obtenerUtensilioPorId(idUtensilio: Int): Utensilio? {
        val db = dbHelper.readableDatabase
        var utensilio: Utensilio? = null

        val cursor = db.rawQuery("SELECT * FROM utensilios WHERE id = ?", arrayOf(idUtensilio.toString()))
        if (cursor.moveToFirst()) {
            utensilio = Utensilio(
                cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                cursor.getString(cursor.getColumnIndexOrThrow("descripcion")),
                cursor.getInt(cursor.getColumnIndexOrThrow("cantidad_inventario"))
            )
        }

        cursor.close()
        db.close()
        return utensilio
    }

    // Actualizar un utensilio
    fun actualizarUtensilio(utensilio: Utensilio): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("nombre", utensilio.nombre)
            put("descripcion", utensilio.descripcion)
            put("cantidad_inventario", utensilio.cantidadInventario)
        }
        val resultado = db.update("utensilios", values, "id=?", arrayOf(utensilio.id.toString()))
        db.close()
        return resultado // Devuelve el número de filas actualizadas
    }

    // Eliminar un utensilio
    fun eliminarUtensilio(id: Int): Int {
        val db = dbHelper.writableDatabase
        val resultado = db.delete("utensilios", "id=?", arrayOf(id.toString()))
        db.close()
        return resultado // Devuelve el número de filas eliminadas
    }
}