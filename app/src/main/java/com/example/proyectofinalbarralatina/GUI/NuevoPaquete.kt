package com.example.proyectofinalbarralatina.GUI

import android.R
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectofinalbarralatina.DAO.DatabaseHelper
import com.example.proyectofinalbarralatina.DAO.PaqueteDAO
import com.example.proyectofinalbarralatina.DAO.ProductoDAO
import com.example.proyectofinalbarralatina.databinding.NuevoPaqueteBinding
import com.example.proyectofinalbarralatina.dataclass.Paquete
import com.example.proyectofinalbarralatina.dataclass.PaqueteProducto

class NuevoPaquete: AppCompatActivity(){
    private lateinit var binding: NuevoPaqueteBinding
    private lateinit var paqueteDAO: PaqueteDAO
    private lateinit var productoDAO: ProductoDAO

    private val productosPaquete = mutableListOf<PaqueteProducto>()
    private var idPaquete: Int? = null
    private var modo: String? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = NuevoPaqueteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        modo = intent.getStringExtra("modo")
        idPaquete = intent.getIntExtra("idPaquete", -1)



        binding.btnReegresar5 .setOnClickListener {
            startActivity(Intent(this, OpcionesProductoPaquete::class.java))
        }
        // Inicializar la base de datos y DAOs
        val dbHelper = DatabaseHelper(this)
        paqueteDAO = PaqueteDAO(dbHelper)
        productoDAO = ProductoDAO(dbHelper)

        if (modo == "editar" && idPaquete != -1) {
            cargarDatosPaquete(idPaquete!!)
        }

        // Cargar lista de productos en el spinner
        cargarSpinnerProductos()

        // Configurar botones
        binding.btnAgregarProducto.setOnClickListener { agregarProductoAPaquete() }
        binding.btnGuardarPaquete.setOnClickListener { guardarPaquete() }
    }
    private fun cargarDatosPaquete(id: Int) {
        val paquete = paqueteDAO.obtenerPaquete(id)

        if (paquete != null) {
            binding.etNombrePaquete.setText(paquete.nombre)
            binding.etDescripcionPaquete.setText(paquete.descripcion)
            binding.etPrecioPaquete.setText(paquete.precio.toString())

            productosPaquete.clear()
            productosPaquete.addAll(paquete.productos)
            actualizarListaProductosPaquete()
        }
    }
    private fun cargarSpinnerProductos() {
        val productos = productoDAO.obtenerTodosLosProductos()
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, productos.map { "${it.id} - ${it.nombre}" })
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerProductos.adapter = adapter
    }
    private fun agregarProductoAPaquete() {
        val itemSeleccionado = binding.spinnerProductos.selectedItem.toString()
        val idProducto = itemSeleccionado.split(" - ")[0].toIntOrNull() ?: return
        val cantidad = binding.etCantidadProducto.text.toString().toIntOrNull() ?: return

        productosPaquete.add(PaqueteProducto(0, idProducto, cantidad))
        Toast.makeText(this, "Producto agregado al paquete", Toast.LENGTH_SHORT).show()
        actualizarListaProductosPaquete()
    }
    private fun guardarPaquete() {
        val nombre = binding.etNombrePaquete.text.toString()
        val descripcion = binding.etDescripcionPaquete.text.toString()
        val precio = binding.etPrecioPaquete.text.toString().toDoubleOrNull() ?: return
        val paquete = Paquete(-1, nombre, descripcion, precio, productosPaquete)
        if (modo == "editar" && idPaquete != -1) {
            paquete.id = idPaquete!!
            paqueteDAO.actualizarPaquete(paquete)
            Toast.makeText(this, "Paquete actualizado con ID: $idPaquete", Toast.LENGTH_SHORT).show()
            return
        }
        paqueteDAO.agregarPaquete(paquete)
        Toast.makeText(this, "Paquete guardado con ID: ${paquete.id}", Toast.LENGTH_SHORT).show()
        limpiarCampos()
    }
    private fun actualizarListaProductosPaquete() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, productosPaquete.map { "ID ${it.idProducto} - Cantidad ${it.cantidad}" })
        binding.lvProductosPaquete.adapter = adapter
    }
    private fun limpiarCampos() {
        binding.etNombrePaquete.setText("")
        binding.etDescripcionPaquete.setText("")
        binding.etCantidadProducto.setText("")
        binding.etPrecioPaquete.setText("")
        productosPaquete.clear()
        actualizarListaProductosPaquete()
    }
}