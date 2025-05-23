package com.example.proyectofinalbarralatina.GUI

import android.R
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectofinalbarralatina.DAO.DatabaseHelper
import com.example.proyectofinalbarralatina.DAO.IngredienteDAO
import com.example.proyectofinalbarralatina.DAO.ProductoDAO
import com.example.proyectofinalbarralatina.DAO.UtensilioDAO
import com.example.proyectofinalbarralatina.databinding.NuevoProductoBinding
import com.example.proyectofinalbarralatina.dataclass.Producto
import com.example.proyectofinalbarralatina.dataclass.ProductoIngrediente
import com.example.proyectofinalbarralatina.dataclass.ProductoUtensilio

class NuevoProducto: AppCompatActivity() {
    private lateinit var binding: NuevoProductoBinding
    private lateinit var productoDAO: ProductoDAO
    private lateinit var ingredienteDAO: IngredienteDAO
    private lateinit var utensilioDAO: UtensilioDAO
    private val ingredientesProducto = mutableListOf<ProductoIngrediente>()
    private val utensiliosProducto = mutableListOf<ProductoUtensilio>()
    private var idProducto: Int? = null
    private var modo: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = NuevoProductoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnReegresar4.setOnClickListener {
            startActivity(Intent(this, OpcionesProductoPaquete::class.java))
            finish()
        }
        modo = intent.getStringExtra("modo")
        idProducto = intent.getIntExtra("idProducto", -1)



        // Inicializar la base de datos y DAOs
        val dbHelper = DatabaseHelper(this)
        productoDAO = ProductoDAO(dbHelper)
        ingredienteDAO = IngredienteDAO(dbHelper)
        utensilioDAO = UtensilioDAO(dbHelper)

        if (modo == "editar" && idProducto != -1) {
            cargarDatosProducto(idProducto!!)
        }

        // Cargar listas de ingredientes y utensilios
        cargarSpinnerIngredientes()
        cargarSpinnerUtensilios()

        // Configurar botones
        binding.btnAgregarIngrediente.setOnClickListener { agregarIngredienteAProducto() }
        binding.btnAgregarUtensilio.setOnClickListener { agregarUtensilioAProducto() }
        binding.btnGuardarProducto.setOnClickListener { guardarProducto() }

    }
    private fun cargarDatosProducto(id: Int) {
        val producto = productoDAO.obtenerProducto(id)

        if (producto != null) {
            binding.tvNuevoProducto.text = "Editar Producto"
            binding.etNombreProducto.setText(producto.nombre)
            binding.etDescripcionProducto.setText(producto.descripcion)
            binding.etPrecioProducto.setText(producto.precio.toString())

            ingredientesProducto.clear()
            ingredientesProducto.addAll(producto.ingredientes)
            actualizarListaIngredientesProducto()

            utensiliosProducto.clear()
            utensiliosProducto.addAll(producto.utensilios)
            actualizarListaUtensiliosProducto()
        }
    }
    private fun cargarSpinnerIngredientes() {
        val ingredientes = ingredienteDAO.obtenerIngredientes()
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, ingredientes.map { "${it.id} - ${it.nombre}" })
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerIngredientes.adapter = adapter
    }
    private fun cargarSpinnerUtensilios() {
        val utensilios = utensilioDAO.obtenerUtensilios()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, utensilios.map { "${it.id} - ${it.nombre}" })
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerUtensilios.adapter = adapter
    }

    private fun agregarIngredienteAProducto() {
        val itemSeleccionado = binding.spinnerIngredientes.selectedItem.toString()
        val idIngrediente = itemSeleccionado.split(" - ")[0].toIntOrNull() ?: return
        val cantidad = binding.etCantidadIngrediente.text.toString().toIntOrNull() ?: return

        ingredientesProducto.add(ProductoIngrediente(0, idIngrediente, cantidad))
        Toast.makeText(this, "Ingrediente agregado", Toast.LENGTH_SHORT).show()
        actualizarListaIngredientesProducto()
    }

    private fun agregarUtensilioAProducto() {
        val itemSeleccionado = binding.spinnerUtensilios.selectedItem.toString()
        val idUtensilio = itemSeleccionado.split(" - ")[0].toIntOrNull() ?: return
        val cantidad = binding.etCantidadUtensilio.text.toString().toIntOrNull() ?: return

        utensiliosProducto.add(ProductoUtensilio(0, idUtensilio, cantidad))
        Toast.makeText(this, "Utensilio agregado", Toast.LENGTH_SHORT).show()
        actualizarListaUtensiliosProducto()
    }
    private fun guardarProducto() {
        val nombre = binding.etNombreProducto.text.toString()
        val descripcion = binding.etDescripcionProducto.text.toString()
        val precio = binding.etPrecioProducto.text.toString().toDoubleOrNull() ?: return
        val producto = Producto(-1, nombre, descripcion, precio, ingredientesProducto, utensiliosProducto)
        if (modo == "editar" && idProducto != -1) {
            producto.id = idProducto!!
            productoDAO.actualizarProducto(producto)
            Toast.makeText(this, "Producto actualizado con ID: $idProducto", Toast.LENGTH_SHORT).show()
            return
        }
        productoDAO.agregarProducto(producto)
        Toast.makeText(this, "Producto guardado con ID: $producto.id", Toast.LENGTH_SHORT).show()
        limpiarCampos()
    }

    private fun actualizarListaIngredientesProducto() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, ingredientesProducto.map { "ID ${it.idIngrediente} - Cantidad ${it.cantidad}" })
        binding.lvIngredientesProducto.adapter = adapter
    }

    private fun actualizarListaUtensiliosProducto() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, utensiliosProducto.map { "ID ${it.idUtensilio} - Cantidad ${it.cantidad}" })
        binding.lvUtensiliosProducto.adapter = adapter
    }
    private fun limpiarCampos() {
        binding.etNombreProducto.setText("")
        binding.etDescripcionProducto.setText("")
        binding.etCantidadIngrediente.setText("")
        binding.etCantidadUtensilio.setText("")
        binding.etPrecioProducto.setText("")
        ingredientesProducto.clear()
        utensiliosProducto.clear()
        actualizarListaIngredientesProducto()
        actualizarListaUtensiliosProducto()
    }
}