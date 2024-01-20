package com.example.steamnetworth.domain

import com.example.steamnetworth.models.NetWorthData

internal interface SteamNetWorthRepository {

    suspend fun getNetWorth(countryCode: String): NetWorthData
}