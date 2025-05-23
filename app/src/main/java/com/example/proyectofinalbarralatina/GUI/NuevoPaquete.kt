package com.example.proyectofinalbarralatina.GUI

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectofinalbarralatina.databinding.NuevoPaqueteBinding

class NuevoPaquete: AppCompatActivity(){
    private lateinit var binding: NuevoPaqueteBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = NuevoPaqueteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnReegresar5 .setOnClickListener {
            startActivity(Intent(this, OpcionesProductoPaquete::class.java))
        }
    }
}