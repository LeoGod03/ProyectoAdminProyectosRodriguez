package com.example.proyectofinalbarralatina.GUI

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectofinalbarralatina.databinding.OpcionesInventarioBinding

class OpcionesInventario: AppCompatActivity() {
    private lateinit var binding: OpcionesInventarioBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = OpcionesInventarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnReegresar4.setOnClickListener {
            startActivity(Intent(this, AdminActivity::class.java))
            finish()
        }
        binding.btnImgIngredientes.setOnClickListener {
            startActivity(Intent(this, IngredientesInventario::class.java))
            finish()
        }
        binding.btnImgUtensilios.setOnClickListener {
            startActivity(Intent(this, UtensiliosInventario::class.java))
            finish()
        }

    }
}