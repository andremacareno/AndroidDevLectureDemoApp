package com.example.steamnetworth.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.steamnetworth.Countries
import com.example.steamnetworth.models.Country
import com.example.steamnetworth.ui.theme.SteamDarkColors

@Composable
internal fun CountrySelectionContent(
    countries: List<Country>,
    onCountryClick: (Country) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .navigationBarsPadding()
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        items(countries) { country ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clickable { onCountryClick(country) }
            ) {
                val emojiToCountry = when (country) {
                    Countries.RU -> "\uD83C\uDDF7\uD83C\uDDFA"
                    Countries.UA -> "\uD83C\uDDFA\uD83C\uDDE6"
                    Countries.TR -> "\uD83C\uDDF9\uD83C\uDDF7"
                    Countries.KZ -> "\uD83C\uDDF0\uD83C\uDDFF"
                    Countries.AR -> "\uD83C\uDDE6\uD83C\uDDF7"
                    else -> ""
                }
                Text(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .wrapContentHeight()
                        .align(Alignment.CenterStart),
                    text = "$emojiToCountry ${country.name}",
                    color = SteamDarkColors.textPrimary,
                    fontSize = 14.sp
                )
                if (country != countries.last()) {
                    Divider(
                        modifier = Modifier.fillMaxWidth().align(Alignment.BottomStart),
                        color = SteamDarkColors.accentSecondary
                    )
                }
            }
        }
    }
}