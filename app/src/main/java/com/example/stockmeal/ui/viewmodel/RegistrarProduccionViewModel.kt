package com.example.stockmeal.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.stockmeal.StockMealAplicacion
import com.example.stockmeal.datos.repositorios.ProduccionRepository
import com.example.stockmeal.datos.repositorios.ProductoRepository
import com.example.stockmeal.modelos.Produccion
import com.example.stockmeal.modelos.ProduccionRequest
import com.example.stockmeal.modelos.Producto
import com.example.stockmeal.ui.state.AppUIState
import kotlinx.serialization.SerializationException
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class RegistrarProduccionFormState(
    val idProductoSeleccionado: Int? = null,
    val cantidad: String = "1",
    val fecha: String = fechaHoy(),
    val mensajeError: String? = null
)

class RegistrarProduccionViewModel(
    private val produccionRepository: ProduccionRepository,
    private val productoRepository: ProductoRepository
) : ViewModel() {

    var platosState by mutableStateOf<AppUIState<List<Producto>>>(AppUIState.Cargando)
        private set

    var formState by mutableStateOf(RegistrarProduccionFormState())
        private set

    var registroState by mutableStateOf<AppUIState<Produccion>?>(null)
        private set

    init {
        obtenerPlatos()
    }

    fun obtenerPlatos() {
        viewModelScope.launch {
            platosState = AppUIState.Cargando
            try {
                val platos = productoRepository.obtenerPlatos()
                platosState = AppUIState.Exito(platos)
            } catch (e: Exception) {
                e.printStackTrace()
                platosState = AppUIState.Error("Error cargando los platos")
            }
        }
    }

    fun seleccionarProducto(idProducto: Int) {
        formState = formState.copy(
            idProductoSeleccionado = idProducto,
            mensajeError = null
        )
    }

    fun actualizarCantidad(cantidad: String) {
        formState = formState.copy(
            cantidad = cantidad.filter { it.isDigit() },
            mensajeError = null
        )
    }

    fun incrementarCantidad() {
        val cantidadActual = formState.cantidad.toIntOrNull() ?: 1
        formState = formState.copy(
            cantidad = (cantidadActual + 1).toString(),
            mensajeError = null
        )
    }

    fun decrementarCantidad() {
        val cantidadActual = formState.cantidad.toIntOrNull() ?: 1
        formState = formState.copy(
            cantidad = (cantidadActual - 1).coerceAtLeast(1).toString(),
            mensajeError = null
        )
    }

    fun registrarProduccion() {
        val idProducto = formState.idProductoSeleccionado
        val cantidad = formState.cantidad.toIntOrNull()

        if (idProducto == null) {
            formState = formState.copy(mensajeError = "Selecciona un plato")
            return
        }

        if (cantidad == null || cantidad <= 0) {
            formState = formState.copy(mensajeError = "Introduce una cantidad valida")
            return
        }

        if (formState.fecha.isBlank()) {
            formState = formState.copy(mensajeError = "Introduce una fecha")
            return
        }

        viewModelScope.launch {
            registroState = AppUIState.Cargando
            try {
                val produccion = produccionRepository.registrarProduccion(
                    ProduccionRequest(
                        idProducto = idProducto,
                        cantidad = cantidad,
                        fecha = formState.fecha
                    )
                )

                registroState = AppUIState.Exito(produccion)
                formState = RegistrarProduccionFormState(fecha = formState.fecha)
            } catch (e: Exception) {
                e.printStackTrace()
                registroState = AppUIState.Error(mensajeErrorRegistro(e))
            }
        }
    }

    fun limpiarResultadoRegistro() {
        registroState = null
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val aplicacion = (this[APPLICATION_KEY] as StockMealAplicacion)
                val produccionRepository = aplicacion.contenedorStockMeal.produccionRepository
                val productoRepository = aplicacion.contenedorStockMeal.productoRepository
                RegistrarProduccionViewModel(
                    produccionRepository = produccionRepository,
                    productoRepository = productoRepository
                )
            }
        }
    }
}

private fun mensajeErrorRegistro(e: Exception): String {
    return when (e) {
        is HttpException -> {
            val errorBackend = e.response()?.errorBody()?.string()
            if (errorBackend.isNullOrBlank()) {
                "Error registrando la produccion: HTTP ${e.code()}"
            } else {
                "Error registrando la produccion: HTTP ${e.code()} - $errorBackend"
            }
        }

        is IOException -> "Error registrando la produccion: no se pudo conectar con el servidor"
        is SerializationException -> "Error registrando la produccion: respuesta o request JSON no valido"
        else -> "Error registrando la produccion: ${e.message ?: e::class.simpleName}"
    }
}

private fun fechaHoy(): String {
    return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
}
