package com.example.stockmeal.ui.pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stockmeal.modelos.Ingrediente
import com.example.stockmeal.modelos.RecetaDetalle
import com.example.stockmeal.ui.state.AppUIState
import com.example.stockmeal.ui.viewmodel.RecetasViewModel

@Composable
fun PantallaRecetaDetalle (
    idReceta: Int,
    viewModel: RecetasViewModel = viewModel(factory = RecetasViewModel.Factory)
) {

    val recetaDetalleState = viewModel.recetaDetalleState

    LaunchedEffect(idReceta) {
        viewModel.obtenerRecetaDetalle(idReceta)
    }

    when (recetaDetalleState) {
        is AppUIState.Cargando -> PantallaCargando()
        is AppUIState.Error -> PantallaError()
        is AppUIState.Exito -> PantallaRecetaDetalleExito(
            recetaDetalle = recetaDetalleState.datos
        )
    }
}

@Composable
fun PantallaRecetaDetalleExito(
    recetaDetalle: RecetaDetalle
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {

        HeaderReceta(recetaDetalle)

        Spacer(modifier = Modifier.height(12.dp))

        ListaIngredientesTicket(recetaDetalle.ingredientes)

        Spacer(modifier = Modifier.weight(1f))

    }
}

@Composable
fun HeaderReceta(
    receta: RecetaDetalle
) {

    TarjetaBase(

        leading = {
            Icon(
                imageVector = Icons.Default.Book,
                contentDescription = null,
                tint = Color(0xFF1565C0),
                modifier = Modifier.size(30.dp)
            )
        },

        contentLeft = {
            Text(
                text = receta.nombre,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "${receta.ingredientes.size} ingredientes",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },

        contentRight = {
            Text(
                text = receta.ingredientes.size.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1565C0)
            )

            Text("ING")
        }
    )
}

@Composable
fun ListaIngredientesTicket(
    ingredientes: List<Ingrediente>
) {

    Column(modifier = Modifier.padding(horizontal = 12.dp)) {

        Text(
            text = "Ingredientes",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        LazyColumn {
            items(ingredientes) { ingrediente ->
                ItemIngredienteTicket(
                    nombre = ingrediente.nombre,
                    cantidad = ingrediente.cantidad,
                    unidad = ingrediente.unidad
                )
            }
        }
    }
}

@Composable
fun ItemIngredienteTicket(
    nombre: String,
    cantidad: Double? = null,
    unidad: String
) {

    Column {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = nombre,
                style = MaterialTheme.typography.bodyLarge
            )

            fun formatCantidad(cantidad: Double): String {
                return if (cantidad % 1.0 == 0.0) {
                    cantidad.toInt().toString()
                } else {
                    cantidad.toString()
                }
            }

            if (cantidad != null) {
                Text(
                    text = "${formatCantidad(cantidad)} $unidad",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Línea separadora (clave estética ticket)
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant,
            thickness = 0.5.dp
        )
    }
}

