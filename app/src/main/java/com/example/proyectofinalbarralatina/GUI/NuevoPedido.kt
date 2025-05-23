package com.example.proyectofinalbarralatina.GUI

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectofinalbarralatina.databinding.RealizarPedidoBinding
import android.content.Intent

class NuevoPedido : AppCompatActivity() {
    private lateinit var binding: RealizarPedidoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RealizarPedidoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegresar6.setOnClickListener {
            startActivity(Intent(this, OpcionesPedido::class.java))
            finish()
        }
    }
}