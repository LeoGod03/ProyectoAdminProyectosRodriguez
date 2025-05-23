package com.example.proyectofinalbarralatina.GUI

import android.R
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectofinalbarralatina.DAO.DatabaseHelper
import com.example.proyectofinalbarralatina.DAO.PedidoDAO
import com.example.proyectofinalbarralatina.databinding.ListaPedidosBinding

class ListaPedidos: AppCompatActivity()  {
    private lateinit var binding: ListaPedidosBinding
    private var usuarioTipo: String? = null
    private lateinit var pedidoDAO: PedidoDAO

    private var modoActual = "pendiente" // Indica si se muestran pedidos pendientes o entregados
    private var idPedidoSeleccionado: Int? = null // del pedido selecciona
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ListaPedidosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Obtener el tipo de usuario del Intent
        usuarioTipo = intent.getStringExtra("usuario_tipo")
        binding.btnRegresar8.setOnClickListener {
            regresarSegunUsuario()
        }
        // Inicializar base de datos
        val dbHelper = DatabaseHelper(this)
        pedidoDAO = PedidoDAO(dbHelper)

        // Cargar lista de pedidos
        cargarListaPedidos()

        // Configurar botones
        binding.btnActualizarPedido.setOnClickListener { actualizarPedidoSeleccionado() }
        binding.btnEliminarPedido.setOnClickListener { eliminarPedidoSeleccionado() }
        binding.btnDetallesPedido.setOnClickListener { mostrarDetallesPedido() }
        binding.btnEntregarPedido.setOnClickListener { entregarPedido() }


    }
    private fun cargarListaPedidos() {
        val pedidos = pedidoDAO.obtenerPedidos()
        val adapter = ArrayAdapter(this, R.layout.simple_list_item_1, pedidos.map { "ID ${it.id} - Total: $${it.total}" })
        binding.lvPedidos.adapter = adapter

        binding.lvPedidos.setOnItemClickListener { parent, _, position, _ ->
            val itemSeleccionado = parent.getItemAtPosition(position) as String
            idPedidoSeleccionado = itemSeleccionado.split(" - ")[0].replace("ID ", "").toIntOrNull()
            binding.tvPedidoSeleccionado.text = itemSeleccionado
        }
    }
    private fun actualizarPedidoSeleccionado() {
        idPedidoSeleccionado?.let { id ->
            val intent = Intent(this, NuevoPedido::class.java).apply {
                putExtra("modo", "editar")
                putExtra("idPedido", id)
            }
            startActivity(intent)
        } ?: Toast.makeText(this, "Selecciona un pedido para actualizar", Toast.LENGTH_SHORT).show()
    }
    private fun eliminarPedidoSeleccionado() {
        idPedidoSeleccionado?.let { id ->
            AlertDialog.Builder(this)
                .setTitle("Eliminar Pedido")
                .setMessage("¿Estás seguro de que quieres eliminar este pedido?")
                .setPositiveButton("Sí") { _, _ ->
                    pedidoDAO.eliminarPedido(id)
                    cargarListaPedidos()
                    Toast.makeText(this, "Pedido eliminado", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        } ?: Toast.makeText(this, "Selecciona un pedido para eliminar", Toast.LENGTH_SHORT).show()
    }
    private fun entregarPedido() {
        idPedidoSeleccionado?.let { id ->
            AlertDialog.Builder(this)
                .setTitle("Entregar Pedido")
                .setMessage("¿Marcar este pedido como entregado?")
                .setPositiveButton("Sí") { _, _ ->
                    pedidoDAO.eliminarPedido(id)
                    cargarListaPedidos()
                    Toast.makeText(this, "Pedido entregado", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        } ?: Toast.makeText(this, "Selecciona un pedido para entregar", Toast.LENGTH_SHORT).show()
    }
    private fun mostrarDetallesPedido() {
        idPedidoSeleccionado?.let { id ->
            val pedido = pedidoDAO.obtenerPedido(id)
            val detalles = """
            Pedido ID: ${pedido?.id}
            Fecha: ${pedido?.fecha}
            Total: $${pedido?.total}
            Productos: ${pedido?.productos?.joinToString(", ") { "ID ${it.idProducto} - Cantidad ${it.cantidad}" }}
            Paquetes: ${pedido?.paquetes?.joinToString(", ") { "ID ${it.idPaquete} - Cantidad ${it.cantidad}" }}
        """.trimIndent()

            AlertDialog.Builder(this)
                .setTitle("Detalles del Pedido")
                .setMessage(detalles)
                .setPositiveButton("Cerrar", null)
                .show()
        } ?: Toast.makeText(this, "Selecciona un pedido para ver detalles", Toast.LENGTH_SHORT).show()
    }

    private fun regresarSegunUsuario() {
        val intent = when (usuarioTipo) {
            "admin" -> Intent(this, AdminActivity::class.java)
            "encargado" -> Intent(this, OpcionesPedido::class.java)
            else -> Intent(this, MainActivity::class.java)
        }
        startActivity(intent)
        finish() // Cierra esta actividad
    }
}