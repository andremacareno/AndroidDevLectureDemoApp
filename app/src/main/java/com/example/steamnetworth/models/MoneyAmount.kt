package com.example.steamnetworth.models

import java.math.BigDecimal

internal data class MoneyAmount(
    val value: BigDecimal,
    val currency: String
)

internal fun MoneyAmount.formatAsString(): String = "$value $currency"