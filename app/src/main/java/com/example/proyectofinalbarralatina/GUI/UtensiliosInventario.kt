package com.example.proyectofinalbarralatina.GUI

import android.R
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectofinalbarralatina.databinding.InventarioUtensiliosBinding
import android.content.Intent
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.proyectofinalbarralatina.DAO.UtensilioDAO
import com.example.proyectofinalbarralatina.DAO.DatabaseHelper
import com.example.proyectofinalbarralatina.dataclass.Utensilio

class UtensiliosInventario: AppCompatActivity()  {

    private lateinit var binding: InventarioUtensiliosBinding
    private lateinit var utensilioDAO: UtensilioDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = InventarioUtensiliosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegresar.setOnClickListener {
            startActivity(Intent(this, OpcionesInventario::class.java))
            finish()
        }

        // Inicializar la base de datos y DAO
        val dbHelper = DatabaseHelper(this)
        utensilioDAO = UtensilioDAO(dbHelper)

        // Configurar botones
        binding.btnAgregarUtensilio.setOnClickListener { agregarUtensilio() }
        binding.btnActualizarUtensilio.setOnClickListener { actualizarUtensilio() }
        binding.btnEliminarUtensilio.setOnClickListener { eliminarUtensilio() }

        binding.btnBorrarCampos.setOnClickListener { limpiarCampos() }

        // Cargar lista de utensilios
        cargarListaUtensilios()

    }
    private fun agregarUtensilio() {
        val nombre = binding.etNombreUtensilio.text.toString()
        val descripcion = binding.etDescripcionUtensilio.text.toString()
        val cantidad = binding.etCantidadUtensilio.text.toString().toIntOrNull() ?: 0

        if (nombre.isNotEmpty()) {
            val utensilio = Utensilio(-1, nombre, descripcion, cantidad)
            utensilioDAO.agregarUtensilio(utensilio)
            Toast.makeText(this, "Utensilio agregado", Toast.LENGTH_SHORT).show()
            cargarListaUtensilios()
            limpiarCampos()
        } else {
            Toast.makeText(this, "Faltan datos", Toast.LENGTH_SHORT).show()
        }
    }
    private fun actualizarUtensilio() {
        val id = binding.etNombreUtensilio.tag as? Int ?: return
        val nombre = binding.etNombreUtensilio.text.toString()
        val descripcion = binding.etDescripcionUtensilio.text.toString()
        val cantidad = binding.etCantidadUtensilio.text.toString().toIntOrNull() ?: 0
        val utensilio = Utensilio(id, nombre, descripcion, cantidad)
        utensilioDAO.actualizarUtensilio(utensilio)
        Toast.makeText(this, "Utensilio actualizado", Toast.LENGTH_SHORT).show()
        cargarListaUtensilios()
    }
    private fun eliminarUtensilio() {
        val idEliminar = binding.etNombreUtensilio.tag as? Int ?: return

        utensilioDAO.eliminarUtensilio(idEliminar)
        Toast.makeText(this, "Utensilio eliminado", Toast.LENGTH_SHORT).show()
        cargarListaUtensilios()
    }

    private fun cargarListaUtensilios() {
        val utensilios = utensilioDAO.obtenerUtensilios()
        val adapter = ArrayAdapter(this, R.layout.simple_list_item_1, utensilios.map { "${it.id} - ${it.nombre} - ${it.cantidadInventario} - ${it.descripcion}" })
        binding.lvUtensilios.adapter = adapter

        // Detectar clic en un utensilio para buscarlo por ID
        binding.lvUtensilios.setOnItemClickListener { parent, view, position, id ->
            val itemSeleccionado = parent.getItemAtPosition(position) as String
            val idUtensilio = itemSeleccionado.split(" - ")[0].toIntOrNull() // Extraer ID

            if (idUtensilio != null) {
                buscarUtensilioPorId(idUtensilio)
            } else {
                Toast.makeText(this, "Error al obtener el ID", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun buscarUtensilioPorId(idUtensilio: Int) {
        val utensilio = utensilioDAO.obtenerUtensilioPorId(idUtensilio)

        if (utensilio != null) {
            binding.etNombreUtensilio.setText(utensilio.nombre)
            binding.etDescripcionUtensilio.setText(utensilio.descripcion)
            binding.etCantidadUtensilio.setText(utensilio.cantidadInventario.toString())

            // Guardar ID en un tag para futuras actualizaciones
            binding.etNombreUtensilio.tag = utensilio.id
        } else {
            Toast.makeText(this, "Utensilio no encontrado", Toast.LENGTH_SHORT).show()
        }
    }
    private fun limpiarCampos() {
        binding.etNombreUtensilio.setText("")
        binding.etDescripcionUtensilio.setText("")
        binding.etCantidadUtensilio.setText("")
        binding.etNombreUtensilio.tag = null // Limpia el ID guardado para evitar actualizaciones incorrectas
    }

}