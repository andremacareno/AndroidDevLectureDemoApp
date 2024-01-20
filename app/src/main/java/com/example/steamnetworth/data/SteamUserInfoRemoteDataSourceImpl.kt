package com.example.steamnetworth.data

import com.example.steamnetworth.API_KEY
import com.example.steamnetworth.STEAM_ID
import com.example.steamnetworth.models.UserInfo
import com.example.steamnetworth.utils.SteamNetWorthAppDispatchers
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.withContext
import org.json.JSONObject

internal class SteamUserInfoRemoteDataSourceImpl(
    private val httpClient: HttpClient,
    private val dispatchers: SteamNetWorthAppDispatchers
) : SteamUserInfoRemoteDataSource {

    private companion object {
        const val ENDPOINT_USER = "http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002"
    }

    override suspend fun getUserInfo(): UserInfo {
        return withContext(dispatchers.io) {
            val url = "$ENDPOINT_USER/?key=$API_KEY&steamids=$STEAM_ID&format=json"
            val response = httpClient.get(url)
            val jsonObj = JSONObject(response.body<String>())
            val userObj = jsonObj
                .getJSONObject("response")
                .getJSONArray("players")
                .getJSONObject(0)
            val displayName = userObj.getString("personaname")
            val avatarFull = userObj.getString("avatarfull")
            return@withContext UserInfo(name = displayName, avatarUrl = avatarFull)
        }
    }
}