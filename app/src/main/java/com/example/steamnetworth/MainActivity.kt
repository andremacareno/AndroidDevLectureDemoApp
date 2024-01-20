package com.example.steamnetworth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.steamnetworth.data.SteamNetWorthInfoRemoteDataSourceImpl
import com.example.steamnetworth.data.SteamUserInfoRemoteDataSourceImpl
import com.example.steamnetworth.domain.SteamNetWorthRepositoryImpl
import com.example.steamnetworth.domain.SteamNetWorthScreenDataLoadingUseCaseImpl
import com.example.steamnetworth.domain.SteamUserInfoRepositoryImpl
import com.example.steamnetworth.ui.SteamNetWorthScreen
import com.example.steamnetworth.ui.theme.SteamNetWorthTheme
import com.example.steamnetworth.utils.SteamNetWorthAppDispatchersImpl

class MainActivity : ComponentActivity() {

    private val viewModel: SteamNetWorthViewModel by viewModels(
        factoryProducer = {
            object : ViewModelProvider.Factory {

                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SteamNetWorthViewModel(
                        initialCountry = Countries.RU,
                        loadingUseCase = SteamNetWorthScreenDataLoadingUseCaseImpl(
                            netWorthRepository = SteamNetWorthRepositoryImpl(
                                netWorthInfoRemoteDataSource = SteamNetWorthInfoRemoteDataSourceImpl(
                                    httpClient = SteamNetWorthApp.getInstance().httpClient,
                                    dispatchers = SteamNetWorthAppDispatchersImpl
                                ),
                            ),
                            userInfoRepository = SteamUserInfoRepositoryImpl(
                                remoteDataSource = SteamUserInfoRemoteDataSourceImpl(
                                    httpClient = SteamNetWorthApp.getInstance().httpClient,
                                    dispatchers = SteamNetWorthAppDispatchersImpl
                                )
                            )
                        ),
                        countriesRepository = CountriesRepository()
                    ) as T
                }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SteamNetWorthTheme {
                val state =
                    viewModel.state.collectAsState()
                SteamNetWorthScreen(
                    state = state.value,
                    onCountryClick = { viewModel.notifyCountryUpdated(it) },
                    onRetryClick = { viewModel.loadData() }
                )
                LaunchedEffect(Unit) {
                    viewModel.loadData()
                }
            }
        }
    }
}