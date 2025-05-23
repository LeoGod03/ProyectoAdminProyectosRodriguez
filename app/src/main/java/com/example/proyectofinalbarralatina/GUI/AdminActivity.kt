package com.example.proyectofinalbarralatina.GUI

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectofinalbarralatina.databinding.MenuAdministradorBinding
class AdminActivity: AppCompatActivity() {
    private lateinit var binding: MenuAdministradorBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MenuAdministradorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnCerrarSesion.setOnClickListener {
            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        binding.btnImgPedidos.setOnClickListener {
            val intent = Intent(this, ListaPedidos::class.java)
            intent.putExtra("usuario_tipo", "admin") // O "encargado"
            startActivity(intent)
            finish()
        }
        binding.btnImgInventario.setOnClickListener {
            val intent = Intent(this, OpcionesInventario::class.java)
            startActivity(intent)
            finish()
        }
        binding.btnImgProductoPaquete.setOnClickListener {
            val intent = Intent(this, OpcionesProductoPaquete::class.java)
            intent.putExtra("usuario_tipo", "admin") // O "encargado"
            startActivity(intent)
            finish()
        }
    }
}