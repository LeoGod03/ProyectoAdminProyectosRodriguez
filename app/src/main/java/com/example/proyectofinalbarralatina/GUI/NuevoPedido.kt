package com.example.proyectofinalbarralatina.GUI

import android.R
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectofinalbarralatina.databinding.RealizarPedidoBinding
import android.content.Intent
import android.speech.tts.TextToSpeech
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.proyectofinalbarralatina.DAO.DatabaseHelper
import com.example.proyectofinalbarralatina.DAO.PaqueteDAO
import com.example.proyectofinalbarralatina.DAO.PedidoDAO
import com.example.proyectofinalbarralatina.DAO.ProductoDAO
import com.example.proyectofinalbarralatina.dataclass.Pedido
import com.example.proyectofinalbarralatina.dataclass.PedidoPaquete
import com.example.proyectofinalbarralatina.dataclass.PedidoProducto
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NuevoPedido : AppCompatActivity() {
    private lateinit var binding: RealizarPedidoBinding
    private lateinit var pedidoDAO: PedidoDAO
    private lateinit var productoDAO: ProductoDAO
    private lateinit var paqueteDAO: PaqueteDAO

    private val productosPedido = mutableListOf<PedidoProducto>()
    private val paquetesPedido = mutableListOf<PedidoPaquete>()

    private var idPedido: Int? = null
    private var modo: String? = null

    private lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RealizarPedidoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegresar6.setOnClickListener {
            startActivity(Intent(this, OpcionesPedido::class.java))
            finish()
        }
        modo = intent.getStringExtra("modo")
        idPedido = intent.getIntExtra("idPedido", -1)

        // Inicializar la base de datos y DAOs
        val dbHelper = DatabaseHelper(this)
        pedidoDAO = PedidoDAO(dbHelper)
        productoDAO = ProductoDAO(dbHelper)
        paqueteDAO = PaqueteDAO(dbHelper)

        // Cargar listas de productos y paquetes
        cargarSpinnerProductos()
        cargarSpinnerPaquetes()
        actualizarListaPedido()

        if (modo == "editar" && idPedido != -1) {
            cargarDatosPedido(idPedido!!)
        }

        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.language = Locale.getDefault() // Configura el idioma
            }
        }

        binding.btnAgregarProductos.setOnClickListener { agregarProductoAPedido() }
        binding.btnAgregarPaquete.setOnClickListener { agregarPaqueteAPedido() }
        binding.btnConfirmarPedido.setOnClickListener { confirmarPedido() }

    }

    private fun cargarSpinnerProductos() {
        val productos = productoDAO.obtenerTodosLosProductos()
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, productos.map { "${it.id} - ${it.nombre}" })
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerProductos.adapter = adapter
    }
    private fun cargarDatosPedido(idPedido: Int) {
        val pedido = pedidoDAO.obtenerPedido(idPedido)

        if (pedido != null) {
            binding.tvTituloPedido.text = "Editar Pedido ID: ${pedido.id}"
            binding.tvTotalPagar.text = "Total a Pagar: $${pedido.total}"

            productosPedido.clear()
            productosPedido.addAll(pedido.productos)
            actualizarListaPedido()

            paquetesPedido.clear()
            paquetesPedido.addAll(pedido.paquetes)
            actualizarListaPedido()
        } else {
            Toast.makeText(this, "Pedido no encontrado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cargarSpinnerPaquetes() {
        val paquetes = paqueteDAO.obtenerTodosLosPaquetes()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, paquetes.map { "${it.id} - ${it.nombre}" })
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPaquetes.adapter = adapter
    }
    private fun agregarProductoAPedido() {
        val itemSeleccionado = binding.spinnerProductos.selectedItem.toString()
        val idProducto = itemSeleccionado.split(" - ")[0].toIntOrNull() ?: return
        val cantidad = binding.etCantidadProducto.text.toString().toIntOrNull() ?: return

        productosPedido.add(PedidoProducto(0, idProducto, cantidad))
        Toast.makeText(this, "Producto agregado al pedido", Toast.LENGTH_SHORT).show()
        actualizarListaPedido()
    }
    private fun agregarPaqueteAPedido() {
        val itemSeleccionado = binding.spinnerPaquetes.selectedItem.toString()
        val idPaquete = itemSeleccionado.split(" - ")[0].toIntOrNull() ?: return
        val cantidad = binding.etCantidadPaquete.text.toString().toIntOrNull() ?: return

        paquetesPedido.add(PedidoPaquete(0, idPaquete, cantidad))
        Toast.makeText(this, "Paquete agregado al pedido", Toast.LENGTH_SHORT).show()
        actualizarListaPedido()
    }
    private fun confirmarPedido() {
        val fecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val total = calcularTotalPedido()
        val pedido = Pedido(-1, fecha, total, productosPedido, paquetesPedido)

        if (modo == "editar" && idPedido != -1) {
            pedido.id = idPedido!!
            pedidoDAO.actualizarPedido(pedido)
            Toast.makeText(this, "Pedido actualizado", Toast.LENGTH_SHORT).show()
            limpiarCampos()
            return
        }
        val idPedido = pedidoDAO.agregarPedido(pedido)
        val mensaje = "Pedido aceptado con ID ${idPedido} con monto de: ${total} pesos"
        tts.speak(mensaje, TextToSpeech.QUEUE_FLUSH, null, null)

        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
        limpiarCampos()
    }
    private fun actualizarListaPedido() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, productosPedido.map { "Producto ID ${it.idProducto} - Cantidad ${it.cantidad}" } +
                paquetesPedido.map { "Paquete ID ${it.idPaquete} - Cantidad ${it.cantidad}" })
        binding.lvPedido.adapter = adapter

        binding.tvTotalPagar.text = "Total a Pagar: $${calcularTotalPedido()}"
    }
    private fun limpiarCampos() {
        binding.etCantidadProducto.setText("")
        binding.etCantidadPaquete.setText("")
        productosPedido.clear()
        paquetesPedido.clear()
        actualizarListaPedido()
    }
    private fun calcularTotalPedido(): Double {
        val totalProductos = productosPedido.sumOf {
            productoDAO.obtenerProducto(it.idProducto)?.precio ?: 0.0 * it.cantidad
        }
        val totalPaquetes = paquetesPedido.sumOf {
            paqueteDAO.obtenerPaquete(it.idPaquete)?.precio ?: 0.0 * it.cantidad
        }
        return totalProductos + totalPaquetes
    }

    override fun onDestroy() {
        super.onDestroy()
        tts.stop()
        tts.shutdown()
    }
}