package com.example.steamnetworth.data

import com.example.steamnetworth.models.UserInfo

internal class SteamUserInfoLocalDataSourceImpl : SteamUserInfoLocalDataSource {

    @Volatile
    private var savedUserInfo: UserInfo? = null

    override suspend fun getUserInfo(): UserInfo? {
        return savedUserInfo
    }

    override suspend fun saveUserInfo(userInfo: UserInfo) {
        savedUserInfo = userInfo
    }


}