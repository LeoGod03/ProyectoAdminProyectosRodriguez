package com.example.proyectofinalbarralatina.GUI

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectofinalbarralatina.databinding.InventarioUtensiliosBinding
import android.content.Intent

class UtensiliosInventario: AppCompatActivity()  {

    private lateinit var binding: InventarioUtensiliosBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = InventarioUtensiliosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegresar.setOnClickListener {
            startActivity(Intent(this, OpcionesInventario::class.java))
        }
    }

}