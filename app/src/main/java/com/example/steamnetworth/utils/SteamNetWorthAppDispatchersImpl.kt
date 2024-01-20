package com.example.steamnetworth.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

internal object SteamNetWorthAppDispatchersImpl : SteamNetWorthAppDispatchers {

    override val io: CoroutineDispatcher = Dispatchers.IO
}