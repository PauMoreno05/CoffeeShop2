package com.example.coffeeshop2

data class Cafeteria(
    val titulo: String,
    val subtitulo: String,
    val imagenRecurso: Int,
    val valoracionFija: Float
)

val listaCafeterias = listOf(
    Cafeteria("Antico Caffè Greco", "St. Italy, Rome", R.drawable.images, 4.0f),
    Cafeteria("Coffee Room", "St. Germany, Berlin", R.drawable.images1, 3.5f),
    Cafeteria("Coffee Ibiza", "St. Colón, Madrid", R.drawable.images2, 4.5f),
    Cafeteria("Pudding Coffee Shop", "St. Diagonal, Barcelona", R.drawable.images3, 5.0f),
    Cafeteria("L'Express", "St. Picadilly Circus, London", R.drawable.images4, 4.2f),
    Cafeteria("Coffee Corner", "St. Ángel Guimerá, Valencia", R.drawable.images5, 3.8f),
    Cafeteria("Sweet Cup", "St.Kinkerstraat, Amsterdam", R.drawable.images6, 4.7f)
)

val todosComentarios = listOf(
    "Muy bueno",
    "Buen ambiente y buen servicio. Lo recomiendo.",
    "Repetiremos. Gran selección de tartas y cafés.",
    "Puntos negativos: el servicio es muy lento y los precios son un poco elevados.",
    "Céntrica y acogedora. Volveremos seguro",
    "La comida estaba deliciosa y bastante bien de precio, mucha variedad de platos.\nEl personal muy amable, nos permitieron ver todo el establecimiento.",
    "Excelente. Destacable la extensa carta de cafés",
    "En días festivos demasiado tiempo de espera. Los camareros/as no dan abasto. No lo recomiendo. No volveré",
    "Todo lo que he probado en la cafetería está riquísimo, dulce o salado.\nLa vajilla muy bonita todo de diseño que en el entorno del bar queda ideal.",
    "La ambientacion muy buena, pero en la planta de arriba un poco escasa.",
    "Muy bueno",
    "Excelente. Destacable la extensa carta de cafés",
    "En días festivos demasiado tiempo de espera. Los camareros/as no dan abasto. No lo recomiendo. No volveré",
    "Buen ambiente y buen servicio. Lo recomiendo."
)