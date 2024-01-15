package com.example.steamnetworth

import com.example.steamnetworth.models.Country

internal class CountriesRepository() {

    fun getCountries(): List<Country> {

        return listOf(
            Country(name = "Россия", isoCountryCode = "ru"),
            Country(name = "Украина", isoCountryCode = "ua"),
            Country(name = "Казахстан", isoCountryCode = "kz"),
            Country(name = "Турция", isoCountryCode = "tr"),
            Country(name = "Аргентина", isoCountryCode = "ar")
        )
    }
}