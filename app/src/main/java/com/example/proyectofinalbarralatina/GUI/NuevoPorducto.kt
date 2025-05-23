package com.example.proyectofinalbarralatina.GUI

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectofinalbarralatina.databinding.NuevoProductoBinding

class NuevoPorducto: AppCompatActivity() {
    private lateinit var binding: NuevoProductoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = NuevoProductoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnReegresar4.setOnClickListener {
            startActivity(Intent(this, OpcionesProductoPaquete::class.java))
            finish()
        }
    }
}