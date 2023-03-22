package com.example.myuca.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myuca.connection.EstudianteManager
import com.example.myuca.ui.theme.MyUCATheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsertView(navController: NavController) {
    var nombres by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var carrera by remember { mutableStateOf("") }
    var año by remember { mutableStateOf("") }

    val composableScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    fun clearInput() {
        nombres = ""
        apellidos = ""
        carrera = ""
        año = ""
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AppBar("Añadir estudiante", scrollBehavior, navController)
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            OutlinedTextField(value = nombres, onValueChange = { nombres = it }, label = {
                Text(text = "Nombres")
            })
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = apellidos, onValueChange = { apellidos = it }, label = {
                Text(text = "Apellidos")
            })
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = carrera, onValueChange = { carrera = it }, label = {
                Text(text = "Carrera")
            })
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = año,
                onValueChange = { año = it },
                label = {
                    Text(text = "Año")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(32.dp))

            Row {
                FilledTonalButton(onClick = {
                    clearInput()
                }) {
                    Text("Limpiar")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = {
                    composableScope.launch(Dispatchers.IO) {
                        val (code, response) = EstudianteManager.insertEstudiante(
                            nombres,
                            apellidos,
                            carrera,
                            año.toInt()
                        )
                        if (code == 200) {
                            snackbarHostState.showSnackbar("El estudiante fue añadido")
                            clearInput()
                        } else if (code == 400) {
                            snackbarHostState.showSnackbar(
                                "No se pudo añadir el estudiante: ${response}",
                                withDismissAction = true, duration = SnackbarDuration.Indefinite
                            )
                        } else {
                            snackbarHostState.showSnackbar("Ocurrió un error inesperado del servidor")
                        }
                    }
                }) {
                    Text("Añadir")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InsertPreview() {
    MyUCATheme {
        InsertView(navController = rememberNavController())
    }
}
