package com.example.proyectofinalbarralatina.GUI

import android.R
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectofinalbarralatina.DAO.DatabaseHelper
import com.example.proyectofinalbarralatina.DAO.IngredienteDAO
import com.example.proyectofinalbarralatina.databinding.IngredientesInventarioBinding
import com.example.proyectofinalbarralatina.dataclass.Ingrediente

class IngredientesInventario: AppCompatActivity()  {
    private lateinit var binding: IngredientesInventarioBinding
    private lateinit var ingredienteDAO: IngredienteDAO
    private val unidadesMedida = listOf("kg", "litros", "gramos", "mililitros", "unidades")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = IngredientesInventarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, unidadesMedida)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spnUnidadesMedida.adapter = adapter

        binding.btnRegresar.setOnClickListener {
            startActivity(Intent(this, OpcionesInventario::class.java))
            finish()
        }
        binding.btnBorrarCampos.setOnClickListener { limpiarCampos() }
        // Inicializar la base de datos y DAO
        val dbHelper = DatabaseHelper(this)
        ingredienteDAO = IngredienteDAO(dbHelper)

        binding.lvIngredientes.setOnItemClickListener { parent, view, position, id ->
            val itemSeleccionado = parent.getItemAtPosition(position) as String
            val idIngrediente = itemSeleccionado.split(" - ")[0].toIntOrNull() // Extraer ID

            if (idIngrediente != null) {
                buscarIngredientePorId(idIngrediente)
            } else {
                Toast.makeText(this, "Error al obtener el ID", Toast.LENGTH_SHORT).show()
            }
        }
        // Configurar botones
        binding.btnAgregarIngrediente.setOnClickListener { agregarIngrediente() }
        binding.btnActualizarIngrediente.setOnClickListener { actualizarIngrediente() }
        binding.btnEliminarIngrediente.setOnClickListener { eliminarIngrediente() }

        // Cargar la lista de ingredientes al iniciar
        cargarListaIngredientes()
    }
    private fun agregarIngrediente() {
        val nombre = binding.etNombreIngrediente.text.toString()
        val descripcion = binding.etDescripcionIngrediente.text.toString()
        val cantidad = binding.etCantidadIngrediente.text.toString().toIntOrNull() ?: 0
        val unidad = binding.spnUnidadesMedida.selectedItem.toString()

        if (nombre.isNotEmpty() && unidad.isNotEmpty()) {
            val ingrediente = Ingrediente(-1, nombre, descripcion, cantidad, unidad)
            ingredienteDAO.agregarIngrediente(ingrediente)
            Toast.makeText(this, "Ingrediente agregado", Toast.LENGTH_SHORT).show()
            cargarListaIngredientes() // Recargar la lista
            limpiarCampos()
        } else {
            Toast.makeText(this, "Faltan datos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun actualizarIngrediente() {
        val id = binding.etNombreIngrediente.tag as? Int ?: return
        val nombre = binding.etNombreIngrediente.text.toString()
        val descripcion = binding.etDescripcionIngrediente.text.toString()
        val cantidad = binding.etCantidadIngrediente.text.toString().toIntOrNull() ?: 0
        val unidad = binding.spnUnidadesMedida.selectedItem.toString()
        val ingrediente = Ingrediente(id, nombre, descripcion, cantidad, unidad)
        ingredienteDAO.actualizarIngrediente(ingrediente)
        Toast.makeText(this, "Ingrediente actualizado", Toast.LENGTH_SHORT).show()
        cargarListaIngredientes()
    }

    private fun buscarIngrediente() {
        val idBuscado = binding.etNombreIngrediente.text.toString().toIntOrNull() ?: return
        val ingrediente = ingredienteDAO.obtenerIngredientePorId(idBuscado)

        if (ingrediente != null) {
            binding.etNombreIngrediente.setText(ingrediente.nombre)
            binding.etDescripcionIngrediente.setText(ingrediente.descripcion)
            binding.etCantidadIngrediente.setText(ingrediente.cantidadInventario.toString())

            // Guardar ID en un tag para futuras actualizaciones
            binding.etNombreIngrediente.tag = ingrediente.id
        } else {
            Toast.makeText(this, "Ingrediente no encontrado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun eliminarIngrediente() {
        val idEliminar = binding.etNombreIngrediente.tag as? Int ?: return

        ingredienteDAO.eliminarIngrediente(idEliminar)
        Toast.makeText(this, "Ingrediente eliminado", Toast.LENGTH_SHORT).show()
        cargarListaIngredientes()
    }

    private fun cargarListaIngredientes() {
        val ingredientes = ingredienteDAO.obtenerIngredientes()
        val adapter = ArrayAdapter(this, R.layout.simple_list_item_1, ingredientes.map { "${it.id} - ${it.nombre } - ${it.cantidadInventario} -${it.descripcion}" })
        binding.lvIngredientes.adapter = adapter
    }

    private fun buscarIngredientePorId(idIngrediente: Int) {
        val ingrediente = ingredienteDAO.obtenerIngredientePorId(idIngrediente)

        if (ingrediente != null) {
            binding.etNombreIngrediente.setText(ingrediente.nombre)
            binding.etDescripcionIngrediente.setText(ingrediente.descripcion)
            binding.etCantidadIngrediente.setText(ingrediente.cantidadInventario.toString())
            binding.spnUnidadesMedida.setSelection(obtenerPosicionSpinner(ingrediente.unidadMedida))

            // Guardar el ID en un tag para futuras actualizaciones
            binding.etNombreIngrediente.tag = ingrediente.id
        } else {
            Toast.makeText(this, "Ingrediente no encontrado", Toast.LENGTH_SHORT).show()
        }
    }
    private fun obtenerPosicionSpinner(unidad: String): Int {
        val unidadesMedida = listOf("kg", "litros", "gramos", "mililitros", "unidades")
        return unidadesMedida.indexOf(unidad)
    }
    private fun limpiarCampos() {
        binding.etNombreIngrediente.setText("")
        binding.etDescripcionIngrediente.setText("")
        binding.etCantidadIngrediente.setText("")
        binding.spnUnidadesMedida.setSelection(0) // Resetea el Spinner a la primera opción
        binding.etNombreIngrediente.tag = null // Limpia el ID guardado para evitar actualizaciones incorrectas
    }
}