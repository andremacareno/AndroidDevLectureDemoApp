package com.example.steamnetworth.data

import com.example.steamnetworth.models.UserInfo

internal interface SteamUserInfoLocalDataSource {

    suspend fun getUserInfo(): UserInfo?

    suspend fun saveUserInfo(userInfo: UserInfo)
}