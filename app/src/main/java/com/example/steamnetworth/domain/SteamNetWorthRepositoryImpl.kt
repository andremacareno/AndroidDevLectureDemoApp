package com.example.steamnetworth.domain

import com.example.steamnetworth.data.SteamNetWorthInfoRemoteDataSource
import com.example.steamnetworth.models.NetWorthData

internal class SteamNetWorthRepositoryImpl(
    private val netWorthInfoRemoteDataSource: SteamNetWorthInfoRemoteDataSource
) : SteamNetWorthRepository {

    override suspend fun getNetWorth(countryCode: String): NetWorthData {
        return netWorthInfoRemoteDataSource.getNetWorthData(countryCode)
    }
}