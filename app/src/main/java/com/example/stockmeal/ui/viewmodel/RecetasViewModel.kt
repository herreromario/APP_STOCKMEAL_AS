package com.example.stockmeal.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.stockmeal.StockMealAplicacion
import com.example.stockmeal.datos.repositorios.RecetaRepository
import com.example.stockmeal.modelos.Receta
import com.example.stockmeal.modelos.RecetaDetalle
import com.example.stockmeal.ui.state.AppUIState
import kotlinx.coroutines.launch

class RecetasViewModel (
    private val recetaRepository: RecetaRepository
): ViewModel(){

    var recetasState by mutableStateOf<AppUIState<List<Receta>>>(AppUIState.Cargando)
        private set

    var recetaDetalleState by mutableStateOf<AppUIState<RecetaDetalle>>(AppUIState.Cargando)
        private set

    init {
        obtenerRecetas()
    }

    fun obtenerRecetas() {
        viewModelScope.launch {
            recetasState = AppUIState.Cargando
            try {
                val listaRecetas = recetaRepository.obtenerRecetas()
                recetasState = AppUIState.Exito(listaRecetas)
            } catch (e: Exception) {
                recetasState = AppUIState.Error("Error cargando las recetas")
            }
        }
    }

    fun obtenerRecetaDetalle(
        idReceta: Int
    ) {
        viewModelScope.launch {
            recetaDetalleState = AppUIState.Cargando
            try {
                val recetaDetalle = recetaRepository.obtenerRecetaPorId(idReceta)
                recetaDetalleState = AppUIState.Exito(recetaDetalle)
            } catch (e: Exception) {
                e.printStackTrace()
                recetaDetalleState = AppUIState.Error("Error cargando la receta seleccionada")
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val aplicacion = (this[APPLICATION_KEY] as StockMealAplicacion)
                val recetaRepository = aplicacion.contenedorStockMeal.recetaRepository
                RecetasViewModel(
                    recetaRepository = recetaRepository
                )
            }
        }
    }
}