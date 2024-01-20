package com.example.steamnetworth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.steamnetworth.domain.SteamNetWorthScreenDataLoadingUseCase
import com.example.steamnetworth.models.Country
import com.example.steamnetworth.ui.SteamNetWorthScreenState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class SteamNetWorthViewModel(
    initialCountry: Country,
    private val loadingUseCase: SteamNetWorthScreenDataLoadingUseCase
) : ViewModel() {

    private val _activeCountry = MutableStateFlow(initialCountry)
    val activeCountry: StateFlow<Country> = _activeCountry.asStateFlow()

    private val _state = MutableStateFlow<SteamNetWorthScreenState>(
        SteamNetWorthScreenState.Loading
    )
    val state: StateFlow<SteamNetWorthScreenState> = _state.asStateFlow()

    fun notifyCountryUpdated(country: Country) {
        _activeCountry.value = country
    }

    fun loadData(country: Country) {
        viewModelScope.launch {
            try {
                _state.value = SteamNetWorthScreenState.Loading
                val data = loadingUseCase.execute(country.isoCountryCode)
                _state.value = SteamNetWorthScreenState.Content(
                    userInfo = data.userInfo,
                    netWorth = data.gameInfo.netWorth,
                    games = data.gameInfo.games,
                    country = country
                )
            } catch (e: Throwable) {
                _state.value = SteamNetWorthScreenState.Error
            }
        }
    }
}