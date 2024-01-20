package com.example.steamnetworth.domain

import com.example.steamnetworth.models.SteamNetWorthScreenData

internal class SteamNetWorthScreenDataLoadingUseCaseImpl(
    private val netWorthRepository: SteamNetWorthRepository,
    private val userInfoRepository: SteamUserInfoRepository
) : SteamNetWorthScreenDataLoadingUseCase {

    override suspend fun execute(countryCode: String): SteamNetWorthScreenData {
        val userInfo = userInfoRepository.getUserInfo()
        val netWorth = netWorthRepository.getNetWorth(countryCode)
        return SteamNetWorthScreenData(userInfo, netWorth)
    }

}