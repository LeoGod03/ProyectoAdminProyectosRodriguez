package com.example.proyectofinalbarralatina.GUI

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectofinalbarralatina.databinding.IngredientesInventarioBinding

class IngredientesInventario: AppCompatActivity()  {
    private lateinit var binding: IngredientesInventarioBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = IngredientesInventarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegresar.setOnClickListener {
            startActivity(Intent(this, OpcionesInventario::class.java))
            finish()
        }
    }
}