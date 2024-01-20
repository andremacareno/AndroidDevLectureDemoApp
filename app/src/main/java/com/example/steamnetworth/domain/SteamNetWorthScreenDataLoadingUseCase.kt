package com.example.steamnetworth.domain

import com.example.steamnetworth.models.SteamNetWorthScreenData

internal interface SteamNetWorthScreenDataLoadingUseCase {

    suspend fun execute(countryCode: String): SteamNetWorthScreenData
}