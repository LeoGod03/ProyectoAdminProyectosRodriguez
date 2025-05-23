package com.example.proyectofinalbarralatina.GUI

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectofinalbarralatina.databinding.MenuEncargadoBinding

class EncargadoActivity: AppCompatActivity() {

    private lateinit var binding: MenuEncargadoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MenuEncargadoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCerrarSesion2.setOnClickListener {
            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.btnImgPedido.setOnClickListener {
            startActivity(Intent(this, OpcionesPedido::class.java))
            finish()
        }

        binding.btnImgProductoPaquete.setOnClickListener{
            val intent = Intent(this, ListaProductosPaquetes::class.java)
            intent.putExtra("usuario_tipo", "encargado") // O "encargado"
            startActivity(intent)
            finish()
        }
    }
}