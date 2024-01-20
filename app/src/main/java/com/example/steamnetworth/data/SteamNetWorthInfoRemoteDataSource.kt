package com.example.steamnetworth.data

import com.example.steamnetworth.models.NetWorthData

internal interface SteamNetWorthInfoRemoteDataSource {

    suspend fun getNetWorthData(countryCode: String) : NetWorthData
}