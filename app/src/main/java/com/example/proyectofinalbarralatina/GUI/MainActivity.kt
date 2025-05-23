package com.example.proyectofinalbarralatina.GUI

import android.os.Bundle
import android.widget.Toast
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectofinalbarralatina.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.btnSesion.setOnClickListener {
            verificarCredenciales()
        }
    }

    private fun verificarCredenciales() {
        val usuario = binding.etUsuario.text.toString()
        val password = binding.etpassPassword.text.toString()

        when {
            usuario == "admin" && password == "1234" -> {
                //redirigir a pantalla de Administrador
                startActivity(Intent(this, AdminActivity::class.java))
                finish()
            }
            usuario == "encargado" && password == "5678" -> {
                startActivity(Intent(this, EncargadoActivity::class.java))
                finish()
            }
            else -> {
                Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
            }
        }
    }

}