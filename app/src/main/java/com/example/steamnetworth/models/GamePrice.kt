package com.example.steamnetworth.models

internal sealed interface GamePrice {

    data object NotAvailable : GamePrice
    data object Free : GamePrice
    data class PriceTag(val price: MoneyAmount) : GamePrice
}