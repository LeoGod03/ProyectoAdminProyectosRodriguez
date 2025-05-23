package com.example.proyectofinalbarralatina.DAO

import android.content.ContentValues
import com.example.proyectofinalbarralatina.dataclass.Producto
import com.example.proyectofinalbarralatina.dataclass.ProductoIngrediente
import com.example.proyectofinalbarralatina.dataclass.ProductoUtensilio

class ProductoDAO(private val dbHelper: DatabaseHelper) {

    fun agregarProducto(producto: Producto) {
        val db = dbHelper.writableDatabase
        var resultado: Long = -1

        try {
            db.beginTransaction()

            //Insertar el producto en la tabla productos
            val valuesProducto = ContentValues().apply {
                put("nombre", producto.nombre)
                put("descripcion", producto.descripcion)
                put("precio", producto.precio)
            }
            resultado = db.insert("productos", null, valuesProducto)

            // Si el producto se creó correctamente, se añaden ingredientes y utensilios
            if (resultado.toInt() != -1) {
                producto.id = resultado.toInt()

                // Insertar ingredientes en la tabla producto_ingredientes
                for (ingrediente in producto.ingredientes) {
                    val valuesIngrediente = ContentValues().apply {
                        put("id_producto", producto.id)
                        put("id_ingrediente", ingrediente.idIngrediente)
                        put("cantidad", ingrediente.cantidad)
                    }
                    ingrediente.idProducto = producto.id
                    db.insert("producto_ingredientes", null, valuesIngrediente)
                }

                //Insertar utensilios en la tabla producto_utensilios
                for (utensilio in producto.utensilios) {
                    val valuesUtensilio = ContentValues().apply {
                        put("id_producto", producto.id)
                        put("id_utensilio", utensilio.idUtensilio)
                        put("cantidad", utensilio.cantidad)
                    }
                    utensilio.idProducto = producto.id
                    db.insert("producto_utensilios", null, valuesUtensilio)
                }

                db.setTransactionSuccessful() // Confirma la transacción si todo salió bien
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.endTransaction()
            db.close()
        }

    }

    fun actualizarProducto(producto: Producto): Int {
        val db = dbHelper.writableDatabase
        var resultado = 0

        try {
            db.beginTransaction()

            // Actualizar la tabla productos
            val valuesProducto = ContentValues().apply {
                put("nombre", producto.nombre)
                put("descripcion", producto.descripcion)
                put("precio", producto.precio)
            }
            resultado = db.update("productos", valuesProducto, "id=?", arrayOf(producto.id.toString()))

            if (resultado > 0) {
                // Eliminar ingredientes y utensilios anteriores
                db.delete("producto_ingredientes", "id_producto=?", arrayOf(producto.id.toString()))
                db.delete("producto_utensilios", "id_producto=?", arrayOf(producto.id.toString()))

                // Insertar nuevos ingredientes
                for (ingrediente in producto.ingredientes) {
                    val valuesIngrediente = ContentValues().apply {
                        put("id_producto", producto.id)
                        put("id_ingrediente", ingrediente.idIngrediente)
                        put("cantidad", ingrediente.cantidad)
                    }
                    ingrediente.idProducto = producto.id
                    db.insert("producto_ingredientes", null, valuesIngrediente)
                }

                // Insertar nuevos utensilios
                for (utensilio in producto.utensilios) {
                    val valuesUtensilio = ContentValues().apply {
                        put("id_producto", producto.id)
                        put("id_utensilio", utensilio.idUtensilio)
                        put("cantidad", utensilio.cantidad)
                    }
                    utensilio.idProducto = producto.id
                    db.insert("producto_utensilios", null, valuesUtensilio)
                }

                db.setTransactionSuccessful()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.endTransaction()
            db.close()
        }

        return resultado // Devuelve el número de filas afectadas
    }

    fun obtenerProducto(idProducto: Int): Producto? {
        val db = dbHelper.readableDatabase
        var producto: Producto? = null

        val cursor = db.rawQuery("SELECT * FROM productos WHERE id = ?", arrayOf(idProducto.toString()))
        if (cursor.moveToFirst()) {
            producto = Producto(
                cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                cursor.getString(cursor.getColumnIndexOrThrow("descripcion")),
                cursor.getDouble(cursor.getColumnIndexOrThrow("precio")),
                obtenerIngredientesDeProducto(idProducto),
                obtenerUtensiliosDeProducto(idProducto)
            )
        }

        cursor.close()
        db.close()
        return producto
    }

    fun obtenerTodosLosProductos(): List<Producto> {
        val listaProductos = mutableListOf<Producto>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM productos", null)

        while (cursor.moveToNext()) {
            listaProductos.add(
                Producto(
                    cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                    cursor.getString(cursor.getColumnIndexOrThrow("descripcion")),
                    cursor.getDouble(cursor.getColumnIndexOrThrow("precio")),
                    obtenerIngredientesDeProducto(cursor.getInt(cursor.getColumnIndexOrThrow("id"))),
                    obtenerUtensiliosDeProducto(cursor.getInt(cursor.getColumnIndexOrThrow("id")))
                )
            )
        }

        cursor.close()
        db.close()
        return listaProductos
    }

    fun obtenerIngredientesDeProducto(idProducto: Int): List<ProductoIngrediente> {
        val lista = mutableListOf<ProductoIngrediente>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM producto_ingredientes WHERE id_producto = ?", arrayOf(idProducto.toString()))

        while (cursor.moveToNext()) {
            lista.add(
                ProductoIngrediente(
                    cursor.getInt(cursor.getColumnIndexOrThrow("id_producto")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("id_ingrediente")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("cantidad"))
                )
            )
        }

        cursor.close()
        db.close()
        return lista
    }

    //Obtener utensilios de un producto
    fun obtenerUtensiliosDeProducto(idProducto: Int): List<ProductoUtensilio> {
        val lista = mutableListOf<ProductoUtensilio>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM producto_utensilios WHERE id_producto = ?", arrayOf(idProducto.toString()))

        while (cursor.moveToNext()) {
            lista.add(
                ProductoUtensilio(
                    cursor.getInt(cursor.getColumnIndexOrThrow("id_producto")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("id_utensilio")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("cantidad"))
                )
            )
        }

        cursor.close()
        db.close()
        return lista
    }

    fun eliminarProducto(idProducto: Int): Int {
        val db = dbHelper.writableDatabase
        var resultado = 0

        try {
            db.beginTransaction()

            // Eliminar ingredientes y utensilios asociados
            db.delete("producto_ingredientes", "id_producto=?", arrayOf(idProducto.toString()))
            db.delete("producto_utensilios", "id_producto=?", arrayOf(idProducto.toString()))

            // Eliminar el producto
            resultado = db.delete("productos", "id=?", arrayOf(idProducto.toString()))

            db.setTransactionSuccessful()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.endTransaction()
            db.close()
        }

        return resultado
    }

}