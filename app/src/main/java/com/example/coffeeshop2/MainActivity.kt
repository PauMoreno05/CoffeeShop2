package com.example.coffeeshop2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.coffeeshop2.ui.theme.CoffeeShop2Theme
import java.net.URLDecoder
import java.net.URLEncoder
import kotlin.random.Random

sealed class Pantalla(val ruta: String) {
    object ListaCafeterias : Pantalla("coffeeshops_list")
    object DetalleCafeteria : Pantalla("coffeeshop_detail/{shopTitle}") {
        fun crearRuta(tituloCafeteria: String) = "coffeeshop_detail/${URLEncoder.encode(tituloCafeteria, "UTF-8")}"
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CoffeeShop2Theme {
                AppCafeterias()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppCafeterias() {
    val controladorNavegacion = rememberNavController()
    val backStackEntry by controladorNavegacion.currentBackStackEntryAsState()
    val rutaActual = backStackEntry?.destination?.route
    val cafeterias = remember { listaCafeterias }

    val mostrarBotonFlotanteDetalle = remember { mutableStateOf(false) }

    val esPantallaDetalle = rutaActual?.startsWith(Pantalla.DetalleCafeteria.ruta.substringBefore("/{")) ?: false
    
    Scaffold(
        topBar = {
            if (rutaActual == Pantalla.ListaCafeterias.ruta) {
                TopAppBar(
                    title = { Text("CoffeeShops", color = MarronOscuro) },
                    navigationIcon = {
                        IconButton(onClick = { /* Acción de menú */ }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menú", tint = MarronOscuro)
                        }
                    },
                    actions = { MenuOpciones(colorContenido = MarronOscuro) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = FondoRosa)
                )
            } else if (esPantallaDetalle) {
                val tituloCafeteriaCodificado = backStackEntry?.arguments?.getString("shopTitle")
                val tituloCafeteria = tituloCafeteriaCodificado?.let { URLDecoder.decode(it, "UTF-8") } ?: ""
                
                TopAppBar(
                    title = { Text(tituloCafeteria, maxLines = 1, overflow = TextOverflow.Ellipsis, color = MarronOscuro) },
                    navigationIcon = {
                        IconButton(onClick = { controladorNavegacion.popBackStack() }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás", tint = MarronOscuro)
                        }
                    },
                    actions = { MenuOpciones(colorContenido = MarronOscuro) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = FondoRosa)
                )
            }
        },
        floatingActionButton = {
            if (esPantallaDetalle) {
                AnimatedVisibility(
                    visible = mostrarBotonFlotanteDetalle.value,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    FloatingActionButton(
                        onClick = { /* Acción de añadir comentario */ },
                        containerColor = ColorTarjetaResena,
                        contentColor = Color.White,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Add new comment", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp))
                    }
                }
            }
        },
        floatingActionButtonPosition = if (esPantallaDetalle) FabPosition.Center else FabPosition.End
    ) { valoresRelleno ->
        NavHost(
            navController = controladorNavegacion,
            startDestination = Pantalla.ListaCafeterias.ruta,
            modifier = Modifier.padding(valoresRelleno)
        ) {
            composable(Pantalla.ListaCafeterias.ruta) {
                PantallaListaCafeterias(
                    cafeterias = cafeterias,
                    alNavegarADetalle = { tituloCafeteria ->
                        controladorNavegacion.navigate(Pantalla.DetalleCafeteria.crearRuta(tituloCafeteria))
                    }
                )
            }
            composable(
                route = Pantalla.DetalleCafeteria.ruta,
                arguments = listOf(navArgument("shopTitle") { type = NavType.StringType })
            ) { entradaPila ->
                val tituloCafeteriaCodificado = entradaPila.arguments?.getString("shopTitle")
                val tituloCafeteria = tituloCafeteriaCodificado?.let { URLDecoder.decode(it, "UTF-8") }

                val cafeteria = cafeterias.firstOrNull { it.titulo == tituloCafeteria }

                if (cafeteria != null) {
                    PantallaDetalleCafeteria(
                        cafeteria = cafeteria,
                        onClickAtras = { controladorNavegacion.popBackStack() },
                        setMostrarFab = { mostrarBotonFlotanteDetalle.value = it }
                    )
                } else {
                    Text("Error: Cafetería no encontrada.", modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}

@Composable
fun PantallaListaCafeterias(
    cafeterias: List<Cafeteria>,
    alNavegarADetalle: (String) -> Unit
) {
    val estadoDesplazamiento = rememberLazyListState()

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        state = estadoDesplazamiento,
        modifier = Modifier.fillMaxSize()
    ) {
        items(cafeterias) { cafeteria ->
            TarjetaCafeteriaUI(
                cafeteria = cafeteria,
                onClick = { alNavegarADetalle(cafeteria.titulo) }
            )
        }
    }
}

@Composable
fun TarjetaCafeteriaUI(cafeteria: Cafeteria, onClick: () -> Unit) {
    var valoracionActual by remember { mutableStateOf(0.0f) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            Image(
                painter = painterResource(id = cafeteria.imagenRecurso),
                contentDescription = cafeteria.titulo,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(FondoRosa)
                    .padding(horizontal = 16.dp, vertical = 20.dp)
            ) {
                Text(
                    text = cafeteria.titulo,
                    fontSize = 32.sp,
                    fontFamily = FuenteCursiva,
                    color = MarronOscuro,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))

                BarraEstrellas(
                    valoracion = valoracionActual,
                    alCambiarValoracion = { nuevaValoracion -> valoracionActual = nuevaValoracion }
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = cafeteria.subtitulo,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MarronOscuro
                )
            }

            Divider(color = ColorDivisor, thickness = 1.dp)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(FondoRosa)
                    .height(48.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                TextButton(
                    onClick = { /* Acción de reserva */ },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = RojoReservar
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(text = "RESERVE", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}


@Composable
fun BarraEstrellas(valoracion: Float, alCambiarValoracion: (Float) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        repeat(5) { indice ->
            val valorEstrella = indice + 1
            val estaSeleccionada = valorEstrella <= valoracion.toInt()

            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = "Estrella $valorEstrella",
                tint = if (estaSeleccionada) AmarilloEstrella else Color.Gray,
                modifier = Modifier
                    .size(36.dp)
                    .clickable { alCambiarValoracion(valorEstrella.toFloat()) }
                    .padding(4.dp)
            )
        }
    }
}

@Composable
fun MenuOpciones(colorContenido: Color) {
    var expandido by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.wrapContentSize(Alignment.TopEnd)
    ) {
        IconButton(onClick = { expandido = true }) {
            Icon(
                Icons.Filled.MoreVert,
                contentDescription = "Más opciones",
                tint = colorContenido
            )
        }
        DropdownMenu(
            expanded = expandido,
            onDismissRequest = { expandido = false }
        ) {
            DropdownMenuItem(
                text = { Text("Compartir", color = MarronOscuro) },
                onClick = { expandido = false },
                leadingIcon = {
                    Icon(Icons.Filled.Share, contentDescription = "Compartir icono", tint = MarronOscuro)
                }
            )
            DropdownMenuItem(
                text = { Text("Album", color = MarronOscuro) },
                onClick = { expandido = false },
                leadingIcon = {
                    Icon(Icons.Filled.Lock, contentDescription = "Album icono", tint = MarronOscuro)
                }
            )
        }
    }
}

@Composable
fun PantallaDetalleCafeteria(
    cafeteria: Cafeteria,
    onClickAtras: () -> Unit,
    setMostrarFab: (Boolean) -> Unit
) {
    val estadoDesplazamiento = rememberLazyStaggeredGridState()

    val mostrarBotonFlotante by remember {
        derivedStateOf {
            estadoDesplazamiento.firstVisibleItemIndex == 0
        }
    }

    LaunchedEffect(mostrarBotonFlotante) {
        setMostrarFab(mostrarBotonFlotante)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        EncabezadoDetalle(cafeteria = cafeteria)

        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            verticalItemSpacing = 8.dp,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            state = estadoDesplazamiento,
            modifier = Modifier.fillMaxSize()
        ) {
            items(todosComentarios) { comentario ->
                TarjetaComentario(comentario)
            }
        }
    }
}

@Composable
fun EncabezadoDetalle(cafeteria: Cafeteria) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = cafeteria.titulo,
            fontSize = 36.sp,
            fontFamily = FuenteCursiva,
            color = MarronOscuro,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun TarjetaComentario(comentario: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = ColorTarjetaResena),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.padding(8.dp)) {
            Text(
                text = comentario,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Black,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CoffeeShopsListScreenPreview() {
    CoffeeShop2Theme {
        PantallaListaCafeterias(cafeterias = listaCafeterias, alNavegarADetalle = {})
    }
}
