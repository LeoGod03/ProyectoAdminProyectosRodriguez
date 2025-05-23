package com.example.proyectofinalbarralatina.GUI

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectofinalbarralatina.databinding.ListaProductosPaquetesBinding

class ListaProductosPaquetes : AppCompatActivity() {

    private lateinit var binding: ListaProductosPaquetesBinding
    private var usuarioTipo: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ListaProductosPaquetesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtener el tipo de usuario del Intent
        usuarioTipo = intent.getStringExtra("usuario_tipo")

        binding.btnRegresar7.setOnClickListener {
            regresarSegunUsuario()
        }
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
}