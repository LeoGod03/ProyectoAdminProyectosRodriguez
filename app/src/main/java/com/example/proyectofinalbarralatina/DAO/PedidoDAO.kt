package com.example.proyectofinalbarralatina.DAO

import android.content.ContentValues
import com.example.proyectofinalbarralatina.dataclass.Pedido
import com.example.proyectofinalbarralatina.dataclass.PedidoPaquete
import com.example.proyectofinalbarralatina.dataclass.PedidoProducto

class PedidoDAO(private val dbHelper: DatabaseHelper) {

    fun agregarPedido(pedido: Pedido) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("fecha", pedido.fecha)
            put("total", pedido.total)
        }

        val idPedido = db.insert("pedidos", null, values)

        if (idPedido != -1L) { // Si el pedido se creó correctamente
            pedido.productos.forEach { agregarProductoAPedido(idPedido.toInt(), it.idProducto, it.cantidad) }
            pedido.paquetes.forEach { agregarPaqueteAPedido(idPedido.toInt(), it.idPaquete, it.cantidad) }
        }

        pedido.id = idPedido.toInt()
    }

    fun agregarProductoAPedido(idPedido: Int, idProducto: Int, cantidad: Int) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("id_pedido", idPedido)
            put("id_producto", idProducto)
            put("cantidad", cantidad)
        }
        db.insert("pedido_producto", null, values)
    }

    fun agregarPaqueteAPedido(idPedido: Int, idPaquete: Int, cantidad: Int) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("id_pedido", idPedido)
            put("id_paquete", idPaquete)
            put("cantidad", cantidad)
        }
        db.insert("pedido_paquete", null, values)
    }

    fun obtenerPedidos(): List<Pedido> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM pedidos", null)
        val pedidos = mutableListOf<Pedido>()

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha"))
            val total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"))

            val productos = obtenerProductosDePedido(id)
            val paquetes = obtenerPaquetesDePedido(id)

            pedidos.add(Pedido(id, fecha, total, productos, paquetes))
        }
        cursor.close()
        return pedidos
    }

    private fun obtenerProductosDePedido(idPedido: Int): List<PedidoProducto> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM pedido_producto WHERE id_pedido = ?", arrayOf(idPedido.toString()))
        val productos = mutableListOf<PedidoProducto>()

        while (cursor.moveToNext()) {
            val idProducto = cursor.getInt(cursor.getColumnIndexOrThrow("id_producto"))
            val cantidad = cursor.getInt(cursor.getColumnIndexOrThrow("cantidad"))
            productos.add(PedidoProducto(idPedido, idProducto, cantidad))
        }
        cursor.close()
        return productos
    }

    private fun obtenerPaquetesDePedido(idPedido: Int): List<PedidoPaquete> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM pedido_paquete WHERE id_pedido = ?", arrayOf(idPedido.toString()))
        val paquetes = mutableListOf<PedidoPaquete>()

        while (cursor.moveToNext()) {
            val idPaquete = cursor.getInt(cursor.getColumnIndexOrThrow("id_paquete"))
            val cantidad = cursor.getInt(cursor.getColumnIndexOrThrow("cantidad"))
            paquetes.add(PedidoPaquete(idPedido, idPaquete, cantidad))
        }
        cursor.close()
        return paquetes
    }

    fun eliminarPedido(idPedido: Int) {
        val db = dbHelper.writableDatabase
        db.delete("pedido_producto", "id_pedido = ?", arrayOf(idPedido.toString()))
        db.delete("pedido_paquete", "id_pedido = ?", arrayOf(idPedido.toString()))
        db.delete("pedidos", "id = ?", arrayOf(idPedido.toString()))
    }
    fun actualizarPedidoCompleto(pedido: Pedido) {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            // Actualiza la información general del pedido
            val values = ContentValues().apply {
                put("fecha", pedido.fecha)
                put("total", pedido.total)
            }
            db.update("pedidos", values, "id = ?", arrayOf(pedido.id.toString()))

            // Actualiza los productos del pedido
            db.delete("pedido_producto", "id_pedido = ?", arrayOf(pedido.id.toString()))
            pedido.productos.forEach { agregarProductoAPedido(pedido.id, it.idProducto, it.cantidad) }

            // Actualiza los paquetes del pedido
            db.delete("pedido_paquete", "id_pedido = ?", arrayOf(pedido.id.toString()))
            pedido.paquetes.forEach { agregarPaqueteAPedido(pedido.id, it.idPaquete, it.cantidad) }

            db.setTransactionSuccessful() // Marca la transacción como exitosa
        } finally {
            db.endTransaction() // Finaliza la operación
        }
    }
}