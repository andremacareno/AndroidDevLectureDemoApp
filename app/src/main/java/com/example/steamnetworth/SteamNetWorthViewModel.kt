package com.example.steamnetworth

import androidx.lifecycle.ViewModel
import com.example.steamnetworth.models.Country
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class SteamNetWorthViewModel(
    initialCountry: Country
) : ViewModel() {

    private val _activeCountry = MutableStateFlow(initialCountry)
    val activeCountry: Flow<Country> = _activeCountry.asStateFlow()

    fun notifyCountryUpdated(country: Country) {
        _activeCountry.value = country
    }
}