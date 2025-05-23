package com.example.proyectofinalbarralatina.GUI

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectofinalbarralatina.databinding.OpcionesProductoPaqueteBinding

class OpcionesProductoPaquete: AppCompatActivity() {

    private lateinit var binding: OpcionesProductoPaqueteBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = OpcionesProductoPaqueteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegresar2.setOnClickListener {
            startActivity(Intent(this, AdminActivity::class.java))
            finish()
        }
        binding.btnImgListaProductosPaquetes.setOnClickListener{
            val intent = Intent(this, ListaProductosPaquetes::class.java)
            intent.putExtra("usuario_tipo", "admin") // O "encargado"
            startActivity(intent)
            finish()
        }

        binding.btnImgProductos.setOnClickListener{
            startActivity(Intent(this, NuevoPorducto::class.java))
            finish()
        }
        binding.btnImgPaquetes.setOnClickListener{
            startActivity(Intent(this, NuevoPaquete::class.java))
            finish()
        }

    }

}