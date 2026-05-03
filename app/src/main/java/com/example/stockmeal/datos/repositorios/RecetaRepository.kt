package com.example.stockmeal.datos.repositorios

import com.example.stockmeal.conexion.RecetaAPI
import com.example.stockmeal.modelos.Receta

interface RecetaRepository {
    suspend fun obtenerRecetas(): List<Receta>
    suspend fun obtenerRecetaPorId(idReceta: Int): Receta
}

class ConexionRecetaRepository(
    private val recetaAPI: RecetaAPI
): RecetaRepository {
    override suspend fun obtenerRecetas(): List<Receta> = recetaAPI.obtenerRecetas()
    override suspend fun obtenerRecetaPorId(idReceta: Int): Receta = recetaAPI.obtenerRecetaPorId(idReceta)
}
