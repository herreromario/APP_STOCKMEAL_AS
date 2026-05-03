package com.example.stockmeal.ui.pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.stockmeal.modelos.Produccion
import com.example.stockmeal.ui.navegacion.Pantallas
import com.example.stockmeal.ui.state.AppUIState
import com.example.stockmeal.ui.viewmodel.DashboardViewModel

@Composable
fun PantallaDashboard(
    navController: NavController,
    viewModel: DashboardViewModel = viewModel(factory = DashboardViewModel.Factory)
) {
    val state = viewModel.state

    when (state) {

        is AppUIState.Error -> PantallaError()
        is AppUIState.Cargando -> PantallaCargando()
        is AppUIState.Exito -> PantallaDashboardExito(
            listaProduccion = state.datos,
            alertas = viewModel.numeroAlertas,
            navController = navController
        )
    }
}

@Composable
fun PantallaDashboardExito(
    listaProduccion: List<Produccion>,
    alertas: Int,
    navController: NavController
) {

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .weight(3f)
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    "Producción de hoy",
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            if (listaProduccion.isEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("No se han encontrado registros")
                }
            } else {
                LazyColumn {
                    items(listaProduccion) { produccion ->
                        TarjetaProduccion(produccion)
                    }
                }
            }
        }
    }
}

//@Composable
//fun TarjetaProduccion(
//    produccion: Produccion
//) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(12.dp)
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(12.dp),
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Text(produccion.plato,
//                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
//            )
//
//            Text(produccion.cantidad.toString(),
//                fontSize = MaterialTheme.typography.titleLarge.fontSize,
//                fontWeight = FontWeight.Bold
//            )
//        }
//    }
//}

@Composable
fun TarjetaProduccion(produccion: Produccion) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            // IZQUIERDA
            Column {
                Text(
                    text = produccion.plato,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "Platos frios",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // DERECHA (número grande)
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = produccion.cantidad.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32) // verde estilo producción
                )

                if (produccion.cantidad < 1) {
                    Text(
                        text = "UNIDADES",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(
                        text = "UNIDAD",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
