package com.example.steamnetworth.models

internal data class NetWorthData(
    val netWorth: MoneyAmount,
    val games: List<Game>
)