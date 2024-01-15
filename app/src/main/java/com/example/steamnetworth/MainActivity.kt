package com.example.steamnetworth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.steamnetworth.models.Country
import com.example.steamnetworth.models.Game
import com.example.steamnetworth.models.GamePrice
import com.example.steamnetworth.models.MoneyAmount
import com.example.steamnetworth.models.UserInfo
import com.example.steamnetworth.ui.SteamNetWorthScreen
import com.example.steamnetworth.ui.SteamNetWorthScreenState
import com.example.steamnetworth.ui.theme.SteamNetWorthTheme
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.math.BigDecimal

class MainActivity : ComponentActivity() {

    private companion object {
        const val ENDPOINT_USER =
            "http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002"
        const val ENDPOINT_OWNED_GAMES =
            "http://api.steampowered.com/IPlayerService/GetOwnedGames/v0001"
        const val ENDPOINT_GAME_INFO =
            "http://store.steampowered.com/api/appdetails"
        const val ENDPOINT_GAME_ICON =
            "http://media.steampowered.com/steamcommunity/public/images/apps"
    }

    private val countriesViewModel: SteamNetWorthViewModel by viewModels(
        factoryProducer = {
            object : ViewModelProvider.Factory {

                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SteamNetWorthViewModel(Countries.RU) as T
                }
            }
        }
    )

    private val countriesRepository = CountriesRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SteamNetWorthTheme {
                CompositionLocalProvider(
                    LocalHttpClient provides SteamNetWorthApp.getInstance().httpClient
                ) {
                    var state by remember {
                        mutableStateOf<SteamNetWorthScreenState>(
                            SteamNetWorthScreenState.Loading
                        )
                    }
                    var selectedCountry =
                        countriesViewModel.activeCountry.collectAsState(initial = Countries.RU)
                    val scope = rememberCoroutineScope()
                    val client = rememberUpdatedState(newValue = LocalHttpClient.current)
                    SteamNetWorthScreen(
                        state = state,
                        countries = countriesRepository.getCountries(),
                        onCountryClick = {
                            countriesViewModel.notifyCountryUpdated(it)
                            scope.launch {
                                loadData(client.value, selectedCountry.value)
                                    .collect { state = it }
                            }
                        }
                    )
                    LaunchedEffect(Unit) {
                        loadData(client.value, selectedCountry.value)
                            .collect { state = it }
                    }
                }
            }
        }
    }

    private suspend fun loadData(
        client: HttpClient,
        country: Country
    ): Flow<SteamNetWorthScreenState> = flow {
        emit(SteamNetWorthScreenState.Loading)
        val userInfo = getUserInfo(client)!!
        val (games, netWorth) = getOwnedGamesAndNetWorth(client, country)!!
        emit(SteamNetWorthScreenState.Content(userInfo, netWorth, games, country))
    }.catch {
        it.printStackTrace()
        emit(SteamNetWorthScreenState.Error)
    }

    private suspend fun getUserInfo(client: HttpClient): UserInfo? {
        try {
            val url = "$ENDPOINT_USER/?key=$API_KEY&steamids=$STEAM_ID&format=json"
            val response = client.get(url)
            val jsonObj = JSONObject(response.body<String>())
            val userObj = jsonObj
                .getJSONObject("response")
                .getJSONArray("players")
                .getJSONObject(0)
            val displayName = userObj.getString("personaname")
            val avatarFull = userObj.getString("avatarfull")
            return UserInfo(name = displayName, avatarUrl = avatarFull)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            e.printStackTrace()
            return null
        }
    }

    private suspend fun getOwnedGamesAndNetWorth(
        client: HttpClient,
        country: Country
    ): Pair<List<Game>, MoneyAmount>? {
        try {
            val ownedGamesUrl =
                "$ENDPOINT_OWNED_GAMES/?key=$API_KEY&steamid=$STEAM_ID&include_appinfo=true&format=json"
            val ownedGamesResponse = client.get(ownedGamesUrl)
            val ownedGamesObj = JSONObject(ownedGamesResponse.body<String>())
            val gamesArray = ownedGamesObj
                .getJSONObject("response")
                .getJSONArray("games")
            val appIds = (0..<gamesArray.length())
                .joinToString { gamesArray.getJSONObject(it).getString("appid") }
            val gamesInfoUrl = ENDPOINT_GAME_INFO
            val gamesInfoResponse = client.post(gamesInfoUrl) {
                contentType(ContentType.Application.FormUrlEncoded)
                setBody("appids=$appIds&filters=price_overview&cc=${country.isoCountryCode}")
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
            return games to MoneyAmount(
                BigDecimal.valueOf(netWorthInCents, 2),
                netWorthCurrency
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            e.printStackTrace()
            return null
        }
    }
}