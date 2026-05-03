package com.example.stockmeal.modelos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Ingrediente (
    @SerialName("nombre")
    val nombre: String,
    @SerialName("cantidad")
    val cantidad: Int,
    @SerialName("unidad")
    val unidad: String
)