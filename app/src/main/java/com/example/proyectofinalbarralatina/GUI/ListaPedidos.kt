package com.example.proyectofinalbarralatina.GUI

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectofinalbarralatina.databinding.ListaPedidosBinding

class ListaPedidos: AppCompatActivity()  {
    private lateinit var binding: ListaPedidosBinding
    private var usuarioTipo: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ListaPedidosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Obtener el tipo de usuario del Intent
        usuarioTipo = intent.getStringExtra("usuario_tipo")
        binding.btnRegresar8.setOnClickListener {
            regresarSegunUsuario()
        }

    }

    private fun regresarSegunUsuario() {
        val intent = when (usuarioTipo) {
            "admin" -> Intent(this, AdminActivity::class.java)
            "encargado" -> Intent(this, OpcionesPedido::class.java)
            else -> Intent(this, MainActivity::class.java) // Por defecto
        }
        startActivity(intent)
        finish() // Cierra esta actividad
    }
}