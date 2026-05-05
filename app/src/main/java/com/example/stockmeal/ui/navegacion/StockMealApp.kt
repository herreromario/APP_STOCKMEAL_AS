package com.example.stockmeal.ui.navegacion

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Warehouse
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.stockmeal.R
import com.example.stockmeal.modelos.BottomItem
import com.example.stockmeal.ui.pantallas.PantallaDashboard
import com.example.stockmeal.ui.pantallas.PantallaRecetaDetalle
import com.example.stockmeal.ui.pantallas.PantallaRecetas
import com.example.stockmeal.ui.pantallas.PantallaRegistrarProduccion
import com.example.stockmeal.ui.pantallas.Stock
import com.example.stockmeal.ui.viewmodel.RecetasViewModel

enum class Pantallas(@StringRes val titulo: Int) {
    Dashboard(R.string.dashboard),
    RegistrarProduccion(R.string.registrar_produccion),
    Recetas(R.string.recetas),
    RecetaDetalle(R.string.detalles_de_la_receta),
    Stock(R.string.stock)
}

val bottomItems = listOf(
    BottomItem(
        titulo = Pantallas.Dashboard.titulo,
        ruta = Pantallas.Dashboard.name,
        iconoSeleccionado = Icons.Filled.Dashboard,
        iconoNoSeleccionado = Icons.Outlined.Dashboard
    ),
    BottomItem(
        titulo = Pantallas.Recetas.titulo,
        ruta = Pantallas.Recetas.name,
        iconoSeleccionado = Icons.AutoMirrored.Filled.MenuBook,
        iconoNoSeleccionado = Icons.AutoMirrored.Outlined.MenuBook
    ),
    BottomItem(
        titulo = Pantallas.Stock.titulo,
        ruta = Pantallas.Stock.name,
        iconoSeleccionado = Icons.Filled.Warehouse,
        iconoNoSeleccionado = Icons.Outlined.Warehouse
    )
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StockMealApp() {

    val navController = rememberNavController()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = backStackEntry?.destination?.route ?: Pantallas.Dashboard.name
    val rutaBase = rutaActual.substringBefore("/")
    val pantallaActual = Pantallas.valueOf(rutaBase)

    Scaffold(

        topBar = {
            AppTopBar(
                pantallaActual = pantallaActual,
                navController = navController
            )
        },

        bottomBar = {
            NavigationBar {
                bottomItems.forEach { item ->

                    val selected = rutaActual == item.ruta

                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(item.ruta) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector =
                                    if (selected) item.iconoSeleccionado
                                    else item.iconoNoSeleccionado,
                                contentDescription = null
                            )
                        },
                        label = {
                            Text(stringResource(item.titulo))
                        }
                    )
                }
            }
        }

    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = Pantallas.Dashboard.name,
            modifier = Modifier.padding(padding)
        ) {

            composable(Pantallas.Dashboard.name) {
                PantallaDashboard(
                    onVerAlertas = {
                        navController.navigate(Pantallas.Stock.name) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }


            composable(Pantallas.RegistrarProduccion.name) {
                PantallaRegistrarProduccion(navController)
            }

            composable(Pantallas.Recetas.name) {
                PantallaRecetas(
                    onDetallesReceta = { id ->
                        navController.navigate("${Pantallas.RecetaDetalle.name}/$id")
                    }
                )
            }

            composable("${Pantallas.RecetaDetalle.name}/{id}") { backStackEntry ->

                val id = backStackEntry.arguments
                    ?.getString("id")
                    ?.toIntOrNull()

                if (id != null) {
                    PantallaRecetaDetalle(idReceta = id)
                }
            }

            composable(Pantallas.Stock.name) {
                Stock(navController)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    pantallaActual: Pantallas,
    navController: NavController
) {
    CenterAlignedTopAppBar(
        title = {
            Text(stringResource(pantallaActual.titulo))
        }
    )
}



