package com.example.proyectofinalbarralatina.dataclass

data class Ingrediente(
    var id: Int,
    val nombre: String,
    val descripcion: String,
    val cantidadInventario: Int,
    val unidadMedida: String
)
