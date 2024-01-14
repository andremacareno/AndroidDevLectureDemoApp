package com.example.steamnetworth.ui

import com.example.steamnetworth.models.Country
import com.example.steamnetworth.models.Game
import com.example.steamnetworth.models.MoneyAmount
import com.example.steamnetworth.models.UserInfo

internal sealed interface SteamNetWorthScreenState {

    data object Loading : SteamNetWorthScreenState
    data object Error : SteamNetWorthScreenState
    data class Content(
        val userInfo: UserInfo,
        val netWorth: MoneyAmount,
        val games: List<Game>,
        val country: Country
    ) : SteamNetWorthScreenState
}