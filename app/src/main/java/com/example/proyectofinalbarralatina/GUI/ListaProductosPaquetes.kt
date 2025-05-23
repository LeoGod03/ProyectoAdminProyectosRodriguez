package com.example.proyectofinalbarralatina.GUI

import android.R
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectofinalbarralatina.DAO.DatabaseHelper
import com.example.proyectofinalbarralatina.DAO.PaqueteDAO
import com.example.proyectofinalbarralatina.DAO.ProductoDAO
import com.example.proyectofinalbarralatina.databinding.ListaProductosPaquetesBinding

class ListaProductosPaquetes : AppCompatActivity() {

    private lateinit var binding: ListaProductosPaquetesBinding
    private var usuarioTipo: String? = null
    private var modoActual: String = "producto"
    private lateinit var paqueteDAO: PaqueteDAO
    private lateinit var productoDAO: ProductoDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ListaProductosPaquetesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtener el tipo de usuario del Intent
        usuarioTipo = intent.getStringExtra("usuario_tipo")

        val dbHelper = DatabaseHelper(this)
        paqueteDAO = PaqueteDAO(dbHelper)
        productoDAO = ProductoDAO(dbHelper)

        binding.btnRegresar7.setOnClickListener {
            regresarSegunUsuario()
        }
        binding.btnProductosVer.setOnClickListener {
            modoActual = "producto"
            cargarLista()
        }

        binding.btnPaquetesVer.setOnClickListener {
            modoActual = "paquete"
            cargarLista()
        }
        binding.listaProductos.setOnItemClickListener { parent, view, position, id ->
            val itemSeleccionado = parent.getItemAtPosition(position) as String
            val idSeleccionado = itemSeleccionado.split(" - ")[0].toIntOrNull()
            binding.tvItemSeleccionado.text = idSeleccionado.toString()
        }

        binding.btnEditarProduPaque.setOnClickListener {
            val idSeleccionado = binding.tvItemSeleccionado.text.toString().toIntOrNull() ?: return@setOnClickListener
            Log.d("DEBUG", "ID Seleccionado: $idSeleccionado")
            val intent = if (modoActual == "producto") {
                Intent(this, NuevoProducto::class.java).apply {
                    putExtra("modo", "editar")
                    putExtra("idProducto", idSeleccionado)
                }
            } else {
                Intent(this, NuevoPaquete::class.java).apply {
                    putExtra("modo", "editar")
                    putExtra("idPaquete", idSeleccionado)
                }
            }

            startActivity(intent)
        }

        binding.btnDetalles.setOnClickListener {
            val idSeleccionado = binding.tvItemSeleccionado.text.toString().toIntOrNull() ?: return@setOnClickListener
            var detalles = ""

            if (modoActual == "producto") {
                val producto = productoDAO.obtenerProducto(idSeleccionado)
                detalles = "Producto: ${producto?.nombre}\nDescripción: ${producto?.descripcion}\nPrecio: ${producto?.precio}"
            } else {
                val paquete = paqueteDAO.obtenerPaquete(idSeleccionado)
                detalles = "Paquete: ${paquete?.nombre}\nDescripción: ${paquete?.descripcion}\nPrecio: ${paquete?.precio}"
            }

            AlertDialog.Builder(this)
                .setTitle("Detalles")
                .setMessage(detalles)
                .setPositiveButton("Cerrar", null)
                .show()
        }

        binding.btnEliminarProduPaque.setOnClickListener {
            val idSeleccionado = binding.tvItemSeleccionado.text.toString().toIntOrNull() ?: return@setOnClickListener

            AlertDialog.Builder(this)
                .setTitle("Eliminar")
                .setMessage("¿Estás seguro de que quieres eliminar este ${if (modoActual == "producto") "producto" else "paquete"}?")
                .setPositiveButton("Sí") { _, _ -> eliminarElemento(idSeleccionado) }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        if (usuarioTipo != "admin") {
            binding.btnEditarProduPaque.isEnabled = false
            binding.btnEliminarProduPaque.isEnabled = false
        }


        cargarLista()
    }
    private fun cargarLista() {
        val adapter = if (modoActual == "producto") {
            val productos = productoDAO.obtenerTodosLosProductos()
            ArrayAdapter(this, R.layout.simple_list_item_1, productos.map { "${it.id} - ${it.nombre} - ${it.precio}" })
        } else {
            val paquetes = paqueteDAO.obtenerTodosLosPaquetes()
            ArrayAdapter(this, android.R.layout.simple_list_item_1, paquetes.map { "${it.id} - ${it.nombre} - ${it.precio}" })
        }

        binding.listaProductos.adapter = adapter
    }

    private fun regresarSegunUsuario() {
        val intent = when (usuarioTipo) {
            "admin" -> Intent(this, OpcionesProductoPaquete::class.java)
            "encargado" -> Intent(this, EncargadoActivity::class.java)
            else -> Intent(this, MainActivity::class.java) // Por defecto
        }
        startActivity(intent)
        finish() // Cierra esta actividad
    }

    private fun eliminarElemento(id: Int) {
        if (modoActual == "producto") {
            productoDAO.eliminarProducto(id)
        } else {
            paqueteDAO.eliminarPaquete(id)
        }

        Toast.makeText(this, "Elemento eliminado", Toast.LENGTH_SHORT).show()
        cargarLista()
    }


}