package com.example.myuca

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myuca.navigation.AppBar
import com.example.myuca.navigation.HomeView
import com.example.myuca.navigation.InsertView
import com.example.myuca.navigation.ModifyView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = "home"
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable("home") {
            HomeView(navController)
        }
        composable("insert") {
            InsertView(navController)
        }
        composable(
            "modify/{id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.IntType
                }
            )
        ) {
            val id = it.arguments?.getInt("id")
            if (id != null) {
                ModifyView(navController, id)
            } else {
                Column {
                    AppBar("Modificar estudiante", navController = navController)
                    Text(
                        "No se ingresó un valor para el ID del estudiante",
                        modifier = Modifier.padding(32.dp)
                    )
                }
            }
        }
    }
}
