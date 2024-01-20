package com.example.steamnetworth.data

import com.example.steamnetworth.models.UserInfo

internal interface SteamUserInfoRemoteDataSource {

    suspend fun getUserInfo(): UserInfo
}