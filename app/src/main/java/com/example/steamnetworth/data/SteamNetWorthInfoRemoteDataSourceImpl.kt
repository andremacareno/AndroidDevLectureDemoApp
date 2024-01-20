package com.example.steamnetworth.data

import com.example.steamnetworth.API_KEY
import com.example.steamnetworth.STEAM_ID
import com.example.steamnetworth.models.Game
import com.example.steamnetworth.models.GamePrice
import com.example.steamnetworth.models.MoneyAmount
import com.example.steamnetworth.models.NetWorthData
import com.example.steamnetworth.utils.SteamNetWorthAppDispatchers
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.math.BigDecimal

internal class SteamNetWorthInfoRemoteDataSourceImpl(
    private val httpClient: HttpClient,
    private val dispatchers: SteamNetWorthAppDispatchers
) : SteamNetWorthInfoRemoteDataSource {

    private companion object {

        const val ENDPOINT_OWNED_GAMES =
            "http://api.steampowered.com/IPlayerService/GetOwnedGames/v0001"
        const val ENDPOINT_GAME_INFO =
            "http://store.steampowered.com/api/appdetails"
        const val ENDPOINT_GAME_ICON =
            "http://media.steampowered.com/steamcommunity/public/images/apps"
    }

    override suspend fun getNetWorthData(countryCode: String): NetWorthData {
        return withContext(dispatchers.io) {
            val ownedGamesUrl =
                "$ENDPOINT_OWNED_GAMES/?key=$API_KEY&steamid=$STEAM_ID&include_appinfo=true&format=json"
            val ownedGamesResponse = httpClient.get(ownedGamesUrl)
            val ownedGamesObj = JSONObject(ownedGamesResponse.body<String>())
            val gamesArray = ownedGamesObj
                .getJSONObject("response")
                .getJSONArray("games")
            val appIds = (0..<gamesArray.length())
                .joinToString { gamesArray.getJSONObject(it).getString("appid") }
            val gamesInfoUrl = ENDPOINT_GAME_INFO
            val gamesInfoResponse = httpClient.post(gamesInfoUrl) {
                contentType(ContentType.Application.FormUrlEncoded)
                setBody("appids=$appIds&filters=price_overview&cc=$countryCode")
            }
            val gamesInfoJsonObj = JSONObject(gamesInfoResponse.body<String>())
            var netWorthInCents = 0L
            var netWorthCurrency = ""
            val games = (0..<gamesArray.length())
                .map { index ->
                    val gameObj = gamesArray.getJSONObject(index)
                    val appId = gameObj.getString("appid")
                    val name = gameObj.getString("name")
                    val iconHash = gameObj.optString("img_icon_url").orEmpty()
                    val priceObj = if (gamesInfoJsonObj.has(appId)) {
                        gamesInfoJsonObj.getJSONObject(appId)
                    } else {
                        null
                    }
                    val price = priceObj?.let {
                        val success = priceObj.getBoolean("success")
                        val dataObj = if (priceObj.has("data")) {
                            priceObj.optJSONObject("data")?.getJSONObject("price_overview")
                        } else {
                            null
                        }
                        when {
                            !success -> GamePrice.NotAvailable
                            success && dataObj == null -> GamePrice.Free
                            else -> {
                                requireNotNull(dataObj)
                                val currency = dataObj.getString("currency")
                                if (netWorthCurrency.isEmpty()) {
                                    netWorthCurrency = currency
                                }
                                val cents = dataObj.getLong("final")
                                netWorthInCents += cents
                                val price = BigDecimal.valueOf(cents, 2)
                                GamePrice.PriceTag(MoneyAmount(price, currency))
                            }
                        }
                    } ?: GamePrice.NotAvailable
                    Game(
                        name = name,
                        iconUrl = "$ENDPOINT_GAME_ICON/$appId/$iconHash.jpg",
                        price = price
                    )
                }
            return@withContext NetWorthData(
                MoneyAmount(
                    BigDecimal.valueOf(netWorthInCents, 2),
                    netWorthCurrency
                ),
                games
            )
        }
    }
}