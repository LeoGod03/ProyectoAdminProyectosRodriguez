package com.example.proyectofinalbarralatina.dataclass

data class Pedido(
    var id: Int,
    val fecha: String,
    val total: Double,
    val productos: List<PedidoProducto>,
    val paquetes: List<PedidoPaquete>
)
