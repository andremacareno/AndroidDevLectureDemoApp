package com.example.steamnetworth.domain

import com.example.steamnetworth.models.UserInfo

internal interface SteamUserInfoRepository {

    suspend fun getUserInfo(): UserInfo
}