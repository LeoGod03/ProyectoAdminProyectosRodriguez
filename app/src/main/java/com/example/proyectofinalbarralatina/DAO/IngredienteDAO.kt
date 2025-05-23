package com.example.proyectofinalbarralatina.DAO

import android.content.ContentValues
import com.example.proyectofinalbarralatina.dataclass.Ingrediente

class IngredienteDAO(private val dbHelper: DatabaseHelper) {

    // Insertar un nuevo ingrediente
    fun agregarIngrediente(ingrediente: Ingrediente) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("nombre", ingrediente.nombre)
            put("descripcion", ingrediente.descripcion)
            put("cantidad_inventario", ingrediente.cantidadInventario)
            put("unidad_medida", ingrediente.unidadMedida)
        }
        val resultado = db.insert("ingredientes", null, values)
        db.close()
       ingrediente.id = resultado.toInt()
    }

    //Obtener todos los ingredientes
    fun obtenerIngredientes(): List<Ingrediente> {
        val listaIngredientes = mutableListOf<Ingrediente>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ingredientes", null)

        while (cursor.moveToNext()) {
            val ingrediente = Ingrediente(
                cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                cursor.getString(cursor.getColumnIndexOrThrow("descripcion")),
                cursor.getInt(cursor.getColumnIndexOrThrow("cantidad_inventario")),
                cursor.getString(cursor.getColumnIndexOrThrow("unidad_medida"))
            )
            listaIngredientes.add(ingrediente)
        }

        cursor.close()
        db.close()
        return listaIngredientes
    }

    fun obtenerIngredientePorId(idIngrediente: Int): Ingrediente? {
        val db = dbHelper.readableDatabase
        var ingrediente: Ingrediente? = null

        val cursor = db.rawQuery("SELECT * FROM ingredientes WHERE id = ?", arrayOf(idIngrediente.toString()))
        if (cursor.moveToFirst()) {
            ingrediente = Ingrediente(
                cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                cursor.getString(cursor.getColumnIndexOrThrow("descripcion")),
                cursor.getInt(cursor.getColumnIndexOrThrow("cantidad_inventario")),
                cursor.getString(cursor.getColumnIndexOrThrow("unidad_medida"))
            )
        }

        cursor.close()
        db.close()
        return ingrediente
    }

    // Actualizar un ingrediente
    fun actualizarIngrediente(ingrediente: Ingrediente): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("nombre", ingrediente.nombre)
            put("descripcion", ingrediente.descripcion)
            put("cantidad_inventario", ingrediente.cantidadInventario)
            put("unidad_medida", ingrediente.unidadMedida)
        }
        val resultado = db.update("ingredientes", values, "id=?", arrayOf(ingrediente.id.toString()))
        db.close()
        return resultado // Devuelve el número de filas actualizadas
    }

    // Eliminar un ingrediente
    fun eliminarIngrediente(id: Int): Int {
        val db = dbHelper.writableDatabase
        val resultado = db.delete("ingredientes", "id=?", arrayOf(id.toString()))
        db.close()
        return resultado // Devuelve el número de filas eliminadas
    }
}