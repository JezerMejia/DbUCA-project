package com.example.myuca.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.beust.klaxon.Klaxon
import com.example.myuca.connection.Estudiante
import com.example.myuca.connection.EstudianteManager
import com.example.myuca.ui.theme.MyUCATheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Data(val data: List<Estudiante>)

enum class ConnectionState {
    WAITING,
    OK,
    ERROR,
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeView(navController: NavHostController, estudiantesList: List<Estudiante> = listOf()) {
    val composableScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    var estudiantes = remember { mutableStateListOf<Estudiante>() }
    var dbConnected = remember { mutableStateOf(ConnectionState.WAITING) }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                estudiantes.addAll(estudiantesList)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    SideEffect {
        composableScope.launch(Dispatchers.IO) {
            val code: Number
            val response: String

            try {
                val output = EstudianteManager.getEstudiantes()
                code = output.first
                response = output.second
            } catch (e: Exception) {
                dbConnected.value = ConnectionState.ERROR
                println(e)
                return@launch
            }

            if (code != 200) {
                dbConnected.value = ConnectionState.ERROR
                return@launch
            }
            val data = Klaxon()
                .parse<Data>(response)
            println("Data: ${data?.data}")
            if (data != null) {
                estudiantes.clear()
                estudiantes.addAll(data.data)
            }
            dbConnected.value = ConnectionState.OK
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AppBar("Estudiantes", scrollBehavior)
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate("insert")
            }) {
                Icon(Icons.Filled.Add, "Añadir estudiante")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->

        if (dbConnected.value == ConnectionState.WAITING) {
            Column(
                Modifier
                    .padding(innerPadding)
                    .padding(32.dp, 12.dp)
            ) {
                Text("Cargando datos de estudiantes...")
            }
        } else if (estudiantes.size > 0 && dbConnected.value == ConnectionState.OK) {
            LazyColumn(
                contentPadding = innerPadding,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(
                    items = estudiantes,
                    key = { est -> est.id }
                ) { estudiante ->
                    val currentItem by rememberUpdatedState(estudiante)

                    var dismissState: DismissState = rememberDismissState()

                    if (dismissState.currentValue == DismissValue.DismissedToStart) {
                        AlertDialog(
                            onDismissRequest = {
                                composableScope.launch(Dispatchers.IO) {
                                    dismissState.reset()
                                }
                            },
                            icon = { Icon(Icons.Filled.Delete, contentDescription = null) },
                            title = { Text("Eliminar estudiante") },
                            text = { Text("¿Está seguro/a de eliminar el estudiante \"${currentItem.getFullName()}?\"") },
                            dismissButton = {
                                TextButton(onClick = {
                                    composableScope.launch(Dispatchers.IO) {
                                        dismissState.reset()
                                    }
                                }) {
                                    Text("Cancelar")
                                }
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        composableScope.launch(Dispatchers.IO) {
                                            val (code, response) = EstudianteManager.deleteEstudiante(
                                                currentItem.id
                                            )
                                            println(response)
                                            if (code != 200) {
                                                dismissState.reset()
                                                snackbarHostState.showSnackbar(
                                                    "El estudiante no se pudo eliminar con éxito",
                                                    withDismissAction = true,
                                                    duration = SnackbarDuration.Indefinite
                                                )
                                                return@launch
                                            }
                                            estudiantes.remove(currentItem)
                                        }
                                    }
                                ) {
                                    Text("Eliminar")
                                }
                            }
                        )
                    }

                    SwipeToDismiss(
                        state = dismissState,
                        modifier = Modifier.animateItemPlacement(),
                        directions = setOf(DismissDirection.EndToStart),
                        background = {
                            val color by animateColorAsState(
                                when (dismissState.targetValue) {
                                    DismissValue.DismissedToStart -> Color.Red
                                    else -> Color.LightGray
                                }
                            )
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .background(color)
                                    .padding(horizontal = 20.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Localized description",
                                )
                            }
                        },
                        dismissContent = {
                            ListItem(
                                modifier = Modifier.clickable {
                                    navController.navigate("modify/${currentItem.id}")
                                },
                                headlineContent = { Text(currentItem.getFullName()) },
                                supportingContent = { Text("${currentItem.carrera} - ${currentItem.año}") },
                            )
                        }
                    )
                }
            }
        } else if (dbConnected.value == ConnectionState.OK) {
            Column(
                Modifier
                    .padding(innerPadding)
                    .padding(32.dp, 12.dp)
            ) {
                Text("No hay estudiantes registrados")
            }
        } else {
            Column(
                Modifier
                    .padding(innerPadding)
                    .padding(32.dp, 12.dp)
            ) {
                Text("Error: No se pudo obtener los datos del servidor.")
                Text("Es posible que la base de datos no esté activa o haya ocurrido un error interno en el servidor.")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    MyUCATheme {
        HomeView(navController = rememberNavController())
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreviewWithData() {
    val estudiantes = listOf(
        Estudiante(1, "Juan", "Pérez", "ISI", 3),
        Estudiante(2, "Alberto", "Chávez", "Inglés", 2),
    )
    MyUCATheme {
        HomeView(navController = rememberNavController(), estudiantes)
    }
}
