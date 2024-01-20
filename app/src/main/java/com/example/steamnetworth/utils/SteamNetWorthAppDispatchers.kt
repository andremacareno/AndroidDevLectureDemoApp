package com.example.steamnetworth.utils

import kotlinx.coroutines.CoroutineDispatcher

internal interface SteamNetWorthAppDispatchers {

    val io: CoroutineDispatcher
}