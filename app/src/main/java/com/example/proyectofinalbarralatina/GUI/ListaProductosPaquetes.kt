package com.example.proyectofinalbarralatina.GUI

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.SearchView
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

    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var listaCompleta: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ListaProductosPaquetesBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        binding.searchVBuSqueda.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrBlank()) {
                    adapter.clear()
                    adapter.addAll(listaCompleta)
                } else {
                    val filtrado = listaCompleta.filter { it.contains(newText, ignoreCase = true) }
                    adapter.clear()
                    adapter.addAll(filtrado)
                }
                return true
            }
        })


        cargarLista()
    }
    private fun cargarLista() {

        val productos = productoDAO.obtenerTodosLosProductos()
        listaCompleta = productos.map { "${it.id} - ${it.nombre} - ${it.precio}" }

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaCompleta)
        binding.listaProductos.adapter = adapter
    }

    private fun regresarSegunUsuario() {
        val intent = when (usuarioTipo) {
            "admin" -> Intent(this, OpcionesProductoPaquete::class.java)
            "encargado" -> Intent(this, EncargadoActivity::class.java)
            else -> Intent(this, MainActivity::class.java)
        }
        startActivity(intent)
        finish()
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