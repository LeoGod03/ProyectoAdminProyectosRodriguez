package com.example.proyectofinalbarralatina.dataclass

data class Paquete(
    var id: Int,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val productos: List<PaqueteProducto>
)

