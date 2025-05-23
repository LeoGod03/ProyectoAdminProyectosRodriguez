package com.example.proyectofinalbarralatina.GUI

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectofinalbarralatina.databinding.OpcionesPedidoBinding
import android.content.Intent
class OpcionesPedido: AppCompatActivity(){
    private lateinit var binding: OpcionesPedidoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = OpcionesPedidoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegresar3.setOnClickListener {
            startActivity(Intent(this, EncargadoActivity::class.java))
            finish()
        }
        binding.btnImgPedidos.setOnClickListener {
            val intent = Intent(this, ListaPedidos::class.java)
            intent.putExtra("usuario_tipo", "encargado") // O "encargado"
            startActivity(intent)
            finish()
        }
        binding.btnImgNuevoPedido.setOnClickListener {
            startActivity(Intent(this, NuevoPedido::class.java))
            finish()
        }
    }

}
