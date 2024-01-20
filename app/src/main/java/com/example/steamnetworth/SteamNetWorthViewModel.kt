package com.example.steamnetworth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.steamnetworth.domain.SteamNetWorthScreenDataLoadingUseCase
import com.example.steamnetworth.models.Country
import com.example.steamnetworth.ui.SteamNetWorthScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class SteamNetWorthViewModel(
    initialCountry: Country,
    private val loadingUseCase: SteamNetWorthScreenDataLoadingUseCase,
    private val countriesRepository: CountriesRepository
) : ViewModel() {

    private val activeCountry = MutableStateFlow(initialCountry)

    private val _state = MutableStateFlow<SteamNetWorthScreenState>(
        SteamNetWorthScreenState.Loading
    )
    val state: StateFlow<SteamNetWorthScreenState> = _state.asStateFlow()

    fun notifyCountryUpdated(country: Country) {
        activeCountry.value = country
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            try {
                _state.value = SteamNetWorthScreenState.Loading
                val country = activeCountry.value
                val data = loadingUseCase.execute(country.isoCountryCode)
                val countries = countriesRepository.getCountries()
                _state.value = SteamNetWorthScreenState.Content(
                    userInfo = data.userInfo,
                    netWorth = data.gameInfo.netWorth,
                    games = data.gameInfo.games,
                    selectedCountry = country,
                    countries = countries
                )
            } catch (e: Throwable) {
                _state.value = SteamNetWorthScreenState.Error
            }
        }
    }
}