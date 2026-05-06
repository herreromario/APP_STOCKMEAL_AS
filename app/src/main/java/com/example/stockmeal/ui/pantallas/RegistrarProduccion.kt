package com.example.stockmeal.ui.pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.stockmeal.modelos.Producto
import com.example.stockmeal.ui.state.AppUIState
import com.example.stockmeal.ui.viewmodel.RegistrarProduccionViewModel

private const val ID_PRODUCTO_SELECCIONADO = "idProductoSeleccionado"
const val PRODUCCION_REGISTRADA = "produccionRegistrada"

@Composable
fun PantallaRegistrarProduccion(
    navController: NavController,
    viewModel: RegistrarProduccionViewModel = viewModel(
        factory = RegistrarProduccionViewModel.Factory
    )
) {
    val platosState = viewModel.platosState
    val registroState = viewModel.registroState
    val formState = viewModel.formState
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    val idProductoDevuelto by savedStateHandle
        ?.getStateFlow(ID_PRODUCTO_SELECCIONADO, -1)
        ?.collectAsState()
        ?: remember { mutableStateOf(-1) }

    LaunchedEffect(idProductoDevuelto) {
        if (idProductoDevuelto != -1) {
            viewModel.seleccionarProducto(idProductoDevuelto)
            savedStateHandle?.set(ID_PRODUCTO_SELECCIONADO, -1)
        }
    }

    LaunchedEffect(registroState) {
        if (registroState is AppUIState.Exito) {
            navController.previousBackStackEntry
                ?.savedStateHandle
                ?.set(PRODUCCION_REGISTRADA, true)
            navController.popBackStack()
        }
    }

    when (platosState) {
        is AppUIState.Error -> PantallaError()
        is AppUIState.Cargando -> PantallaCargando()
        is AppUIState.Exito -> PantallaRegistrarProduccionExito(
            platos = platosState.datos,
            idProductoSeleccionado = formState.idProductoSeleccionado,
            cantidad = formState.cantidad,
            fecha = formState.fecha,
            mensajeError = formState.mensajeError,
            registrando = registroState is AppUIState.Cargando,
            errorRegistro = (registroState as? AppUIState.Error)?.mensaje,
            onAbrirSelectorPlatos = {
                navController.navigate("SeleccionarPlatoProduccion")
            },
            onIncrementarCantidad = viewModel::incrementarCantidad,
            onDecrementarCantidad = viewModel::decrementarCantidad,
            onRegistrar = viewModel::registrarProduccion
        )
    }
}

@Composable
fun PantallaRegistrarProduccionExito(
    platos: List<Producto>,
    idProductoSeleccionado: Int?,
    cantidad: String,
    fecha: String,
    mensajeError: String?,
    registrando: Boolean,
    errorRegistro: String?,
    onAbrirSelectorPlatos: () -> Unit,
    onIncrementarCantidad: () -> Unit,
    onDecrementarCantidad: () -> Unit,
    onRegistrar: () -> Unit
) {
    val platoSeleccionado = platos.firstOrNull { it.idProducto == idProductoSeleccionado }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Nueva produccion",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(12.dp)
        )

        TarjetaSeleccionPlato(
            platoSeleccionado = platoSeleccionado,
            onClick = onAbrirSelectorPlatos
        )

        SelectorCantidadProduccion(
            cantidad = cantidad,
            onIncrementarCantidad = onIncrementarCantidad,
            onDecrementarCantidad = onDecrementarCantidad
        )

        Text(
            text = "Fecha de hoy: $fecha",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        if (mensajeError != null || errorRegistro != null) {
            Text(
                text = mensajeError ?: errorRegistro.orEmpty(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }

        Button(
            onClick = onRegistrar,
            enabled = !registrando,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            if (registrando) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Registrar produccion")
            }
        }
    }
}

@Composable
fun TarjetaSeleccionPlato(
    platoSeleccionado: Producto?,
    onClick: () -> Unit
) {
    TarjetaBase(
        onClick = onClick,
        leading = {
            Icon(
                imageVector = Icons.Filled.Restaurant,
                contentDescription = null,
                tint = Color(0xFF2E7D32),
                modifier = Modifier.size(30.dp)
            )
        },
        contentLeft = {
            Text(
                text = platoSeleccionado?.nombre ?: "Seleccionar plato",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        },
        contentRight = {
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    )
}

@Composable
fun SelectorCantidadProduccion(
    cantidad: String,
    onIncrementarCantidad: () -> Unit,
    onDecrementarCantidad: () -> Unit
) {
    TarjetaBase(
        contentLeft = {
            Text(
                text = "Cantidad",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        },
        contentRight = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(onClick = onDecrementarCantidad) {
                    Icon(
                        imageVector = Icons.Filled.Remove,
                        contentDescription = null
                    )
                }

                Text(
                    text = cantidad,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )

                IconButton(onClick = onIncrementarCantidad) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null
                    )
                }
            }
        }
    )
}

@Composable
fun PantallaSeleccionarPlatoProduccion(
    navController: NavController,
    viewModel: RegistrarProduccionViewModel = viewModel(
        factory = RegistrarProduccionViewModel.Factory
    )
) {
    when (val platosState = viewModel.platosState) {
        is AppUIState.Error -> PantallaError()
        is AppUIState.Cargando -> PantallaCargando()
        is AppUIState.Exito -> ListaPlatosProduccion(
            platos = platosState.datos,
            onSeleccionarPlato = { idProducto ->
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set(ID_PRODUCTO_SELECCIONADO, idProducto)
                navController.popBackStack()
            }
        )
    }
}

@Composable
fun ListaPlatosProduccion(
    platos: List<Producto>,
    onSeleccionarPlato: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Text(
            text = "Selecciona un plato",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(12.dp)
        )

        LazyColumn {
            items(platos) { plato ->
                TarjetaPlatoProduccion(
                    plato = plato,
                    onClick = { onSeleccionarPlato(plato.idProducto) }
                )
            }
        }
    }
}

@Composable
fun TarjetaPlatoProduccion(
    plato: Producto,
    onClick: () -> Unit
) {
    TarjetaBase(
        onClick = onClick,
        leading = {
            Icon(
                imageVector = Icons.Filled.Restaurant,
                contentDescription = null,
                tint = Color(0xFF2E7D32),
                modifier = Modifier.size(30.dp)
            )
        },
        contentLeft = {
            Text(
                text = plato.nombre,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        },
        contentRight = {
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    )
}
