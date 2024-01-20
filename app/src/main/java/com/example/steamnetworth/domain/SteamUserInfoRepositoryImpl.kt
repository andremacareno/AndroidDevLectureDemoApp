package com.example.steamnetworth.domain

import com.example.steamnetworth.data.SteamUserInfoRemoteDataSource
import com.example.steamnetworth.models.UserInfo

internal class SteamUserInfoRepositoryImpl(
    private val remoteDataSource: SteamUserInfoRemoteDataSource
) : SteamUserInfoRepository {

    override suspend fun getUserInfo(): UserInfo {
        return remoteDataSource.getUserInfo()
    }

}