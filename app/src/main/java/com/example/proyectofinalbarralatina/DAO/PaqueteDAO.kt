package com.example.proyectofinalbarralatina.DAO

import android.content.ContentValues
import com.example.proyectofinalbarralatina.dataclass.Paquete
import com.example.proyectofinalbarralatina.dataclass.PaqueteProducto

class PaqueteDAO(private val dbHelper: DatabaseHelper) {

    // Insertar un paquete con sus productos
    fun agregarPaquete(paquete: Paquete) {
        val db = dbHelper.writableDatabase
        var resultado: Long = -1

        try {
            db.beginTransaction()

            // 🔹 Insertar el paquete
            val valuesPaquete = ContentValues().apply {
                put("nombre", paquete.nombre)
                put("descripcion", paquete.descripcion)
                put("precio", paquete.precio)
            }
            resultado = db.insert("paquetes", null, valuesPaquete)

            if (resultado.toInt() != -1) {
                paquete.id = resultado.toInt()

                // 🔹 Insertar productos en la tabla paquete_productos
                for (producto in paquete.productos) {
                    val valuesProducto = ContentValues().apply {
                        put("id_paquete", paquete.id)
                        put("id_producto", producto.idProducto)
                        put("cantidad", producto.cantidad)
                    }
                    producto.idPaquete = paquete.id
                    db.insert("paquete_productos", null, valuesProducto)
                }

                db.setTransactionSuccessful()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.endTransaction()
            db.close()
        }

    }

    // Obtener un paquete con sus productos
    fun obtenerPaquete(idPaquete: Int): Paquete? {
        val db = dbHelper.readableDatabase
        var paquete: Paquete? = null

        val cursor = db.rawQuery("SELECT * FROM paquetes WHERE id = ?", arrayOf(idPaquete.toString()))
        if (cursor.moveToFirst()) {
            paquete = Paquete(
                cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                cursor.getString(cursor.getColumnIndexOrThrow("descripcion")),
                cursor.getDouble(cursor.getColumnIndexOrThrow("precio")),
                obtenerProductosDePaquete(idPaquete)
            )
        }

        cursor.close()
        db.close()
        return paquete
    }

    fun obtenerTodosLosPaquetes(): List<Paquete> {
        val listaPaquetes = mutableListOf<Paquete>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM paquetes", null)

        while (cursor.moveToNext()) {
            listaPaquetes.add(
                Paquete(
                    cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                    cursor.getString(cursor.getColumnIndexOrThrow("descripcion")),
                    cursor.getDouble(cursor.getColumnIndexOrThrow("precio")),
                    obtenerProductosDePaquete(cursor.getInt(cursor.getColumnIndexOrThrow("id")))
                )
            )
        }

        cursor.close()
        db.close()
        return listaPaquetes
    }

    // Actualizar un paquete y sus productos
    fun actualizarPaquete(paquete: Paquete): Int {
        val db = dbHelper.writableDatabase
        var resultado = 0

        try {
            db.beginTransaction()

            // Actualizar el paquete
            val valuesPaquete = ContentValues().apply {
                put("nombre", paquete.nombre)
                put("descripcion", paquete.descripcion)
                put("precio", paquete.precio)
            }
            resultado = db.update("paquetes", valuesPaquete, "id=?", arrayOf(paquete.id.toString()))

            if (resultado > 0) {
                // Eliminar productos anteriores del paquete
                db.delete("paquete_productos", "id_paquete=?", arrayOf(paquete.id.toString()))

                // Insertar nuevos productos
                for (producto in paquete.productos) {
                    val valuesProducto = ContentValues().apply {
                        put("id_paquete", paquete.id)
                        put("id_producto", producto.idProducto)
                        put("cantidad", producto.cantidad)
                    }
                    producto.idPaquete = paquete.id
                    db.insert("paquete_productos", null, valuesProducto)
                }

                db.setTransactionSuccessful()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.endTransaction()
            db.close()
        }

        return resultado
    }

    // Eliminar un paquete y sus productos asociados
    fun eliminarPaquete(idPaquete: Int): Int {
        val db = dbHelper.writableDatabase
        var resultado = 0

        try {
            db.beginTransaction()

            // Eliminar productos del paquete
            db.delete("paquete_productos", "id_paquete=?", arrayOf(idPaquete.toString()))

            // Eliminar el paquete
            resultado = db.delete("paquetes", "id=?", arrayOf(idPaquete.toString()))

            db.setTransactionSuccessful()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.endTransaction()
            db.close()
        }

        return resultado
    }

    // Obtener los productos de un paquete
    fun obtenerProductosDePaquete(idPaquete: Int): List<PaqueteProducto> {
        val lista = mutableListOf<PaqueteProducto>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM paquete_productos WHERE id_paquete = ?", arrayOf(idPaquete.toString()))

        while (cursor.moveToNext()) {
            lista.add(
                PaqueteProducto(
                    cursor.getInt(cursor.getColumnIndexOrThrow("id_paquete")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("id_producto")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("cantidad"))
                )
            )
        }

        cursor.close()
        db.close()
        return lista
    }
}