package com.example.steamnetworth.models

internal data class Game(
    val name: String,
    val iconUrl: String,
    val price: GamePrice
)