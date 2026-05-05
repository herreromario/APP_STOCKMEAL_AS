package com.example.stockmeal.ui.pantallas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stockmeal.R
import com.example.stockmeal.modelos.Receta
import com.example.stockmeal.ui.state.AppUIState
import com.example.stockmeal.ui.viewmodel.RecetasViewModel
import kotlin.Int
import kotlin.Unit

@Composable
fun PantallaRecetas(
    viewModel: RecetasViewModel = viewModel(factory = RecetasViewModel.Factory),
    onDetallesReceta: (Int) -> Unit
){
    val recetasState = viewModel.recetasState

    when(recetasState){

        is AppUIState.Error -> PantallaError()
        is AppUIState.Cargando -> PantallaCargando()
        is AppUIState.Exito -> PantallaRecetasExito(
            listaRecetas = recetasState.datos,
            onDetallesReceta = onDetallesReceta
        )
    }
}

@Composable
fun PantallaRecetasExito(
    listaRecetas: List<Receta>,
    onDetallesReceta: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Text(
            text = stringResource(R.string.recetas),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(12.dp)
        )

        LazyColumn {
            items(listaRecetas) { receta ->
                TarjetaReceta(
                    receta,
                    onDetallesReceta = onDetallesReceta
                )
            }
        }
    }
}

@Composable
fun TarjetaReceta(
    receta: Receta,
    onDetallesReceta: (Int) -> Unit
) {
    TarjetaBase(

        onClick = { onDetallesReceta(receta.idReceta) },

        // ICONO
        leading = {
            Icon(
                imageVector = Icons.Filled.Book,
                contentDescription = null,
                tint = Color(0xFF1565C0),
                modifier = Modifier.size(30.dp)
            )
        },

        // TEXTO IZQUIERDA
        contentLeft = {

            // TÍTULO
            Text(
                text = receta.nombre,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            // ETIQUETA (PRÓXIMA FEATURE: "Cocinado por última vez el dd-MM-AAAA)
            Text(
                text = "${receta.numIngredientes} ingredientes",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },


        // TEXTO DERECHA
        contentRight = {

            // TÍTULO
            Text(
                text = receta.numIngredientes.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1565C0)
            )

            // ETIQUETA
            Text(
                text = "ING",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    )
}