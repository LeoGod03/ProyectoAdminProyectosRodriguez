package com.example.proyectofinalbarralatina.dataclass

data class Producto(
    var id: Int,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val ingredientes: List<ProductoIngrediente>,
    val utensilios: List<ProductoUtensilio>
)

