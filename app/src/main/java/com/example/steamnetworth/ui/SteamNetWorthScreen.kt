package com.example.steamnetworth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.steamnetworth.R
import com.example.steamnetworth.models.Country
import com.example.steamnetworth.models.Game
import com.example.steamnetworth.models.GamePrice
import com.example.steamnetworth.models.MoneyAmount
import com.example.steamnetworth.models.UserInfo
import com.example.steamnetworth.models.formatAsString
import com.example.steamnetworth.ui.theme.SteamDarkColors
import com.example.steamnetworth.ui.theme.SteamNetWorthTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SteamNetWorthScreen(
    state: SteamNetWorthScreenState,
    countries: List<Country>,
    onCountryClick: (Country) -> Unit,
    onRetryClick: () -> Unit
) {
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    val bottomSheetScope = rememberCoroutineScope()

    SteamNetWorthTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(SteamDarkColors.background)
                .padding(top = 16.dp)
        ) {
            when (state) {
                is SteamNetWorthScreenState.Content -> SteamNetWorthContent(
                    state
                ) {
                    bottomSheetScope.launch {
                        openBottomSheet = true
                    }
                }

                SteamNetWorthScreenState.Error -> SteamNetWorthError(
                    onRetryClick = onRetryClick
                )
                SteamNetWorthScreenState.Loading -> SteamNetWorthLoading()
            }
            if (openBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = { openBottomSheet = false },
                    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                    containerColor = SteamDarkColors.background
                ) {
                    CountrySelectionContent(
                        countries = countries,
                        onCountryClick = { country ->
                            openBottomSheet = false
                            onCountryClick(country)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SteamNetWorthLoading() {
    Box(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(
            color = SteamDarkColors.accentSecondary,
            trackColor = SteamDarkColors.accent,
            modifier = Modifier
                .size(64.dp)
                .align(Alignment.Center)
        )
    }
}

@Composable
private fun SteamNetWorthError(onRetryClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .fillMaxWidth()
                .wrapContentHeight()
                .align(Alignment.Center)
        ) {
            Text(
                text = stringResource(R.string.error_loading),
                color = SteamDarkColors.textPrimary,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Button(
                content = {
                    Text(
                        text = stringResource(R.string.retry)
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = SteamDarkColors.accent,
                    contentColor = SteamDarkColors.textPrimaryOnLight
                ),
                onClick = onRetryClick,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun SteamNetWorthContent(
    content: SteamNetWorthScreenState.Content,
    onCountryChangeClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        ContentHeader(
            info = content.userInfo,
            netWorth = content.netWorth,
            country = content.country,
            onCountryChangeClick = onCountryChangeClick
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(content.games) {
                GameItem(
                    game = it,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                )
                Divider(
                    modifier = Modifier.fillMaxWidth(),
                    color = SteamDarkColors.accentSecondary
                )
            }
        }
    }
}

@Composable
private fun ContentHeader(
    info: UserInfo,
    netWorth: MoneyAmount,
    country: Country,
    onCountryChangeClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        AsyncImage(
            model = info.avatarUrl,
            contentDescription = "Profile picture",
            modifier = Modifier
                .padding(start = 16.dp)
                .size(180.dp)
                .align(Alignment.CenterStart)
                .clip(CircleShape)
        )
        Column(
            modifier = Modifier
                .padding(start = 212.dp)
                .align(Alignment.CenterStart)
        ) {
            Text(
                text = info.name,
                color = SteamDarkColors.textPrimary,
                fontSize = 18.sp
            )
            Text(
                text = netWorth.formatAsString(),
                color = SteamDarkColors.textSecondary,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = stringResource(R.string.region, country.name),
                color = SteamDarkColors.textSecondary,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
            ClickableText(
                text = AnnotatedString(stringResource(R.string.change_region)),
                style = TextStyle.Default.copy(
                    color = SteamDarkColors.accent,
                    fontSize = 12.sp,
                ),
                modifier = Modifier.padding(top = 8.dp),
                onClick = { onCountryChangeClick() }
            )
        }
    }
}

@Composable
private fun GameItem(
    game: Game,
    modifier: Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        AsyncImage(
            model = game.iconUrl,
            contentDescription = "Game icon",
            modifier = Modifier
                .padding(start = 16.dp)
                .align(Alignment.CenterVertically)
                .size(32.dp)
        )
        Text(
            text = game.name,
            color = SteamDarkColors.textPrimary,
            fontSize = 14.sp,
            maxLines = 2,
            overflow = TextOverflow.Clip,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(start = 8.dp, end = 8.dp)
                .widthIn(max = 200.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        val price = when (game.price) {
            GamePrice.Free -> stringResource(R.string.free)
            GamePrice.NotAvailable -> stringResource(R.string.not_available)
            is GamePrice.PriceTag -> game.price.price.formatAsString()
        }
        Text(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(end = 16.dp),
            text = price,
            color = SteamDarkColors.textPrimary,
            fontSize = 12.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SteamNetWorthLoadingPreview() {
    SteamNetWorthTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            SteamNetWorthLoading()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SteamNetWorthErrorPreview() {
    SteamNetWorthTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            SteamNetWorthError(onRetryClick = {})
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SteamNetWorthContentPreview() {
    val state = SteamNetWorthScreenState.Content(
        userInfo = UserInfo(
            name = "Steam User",
            avatarUrl = ""
        ),
        netWorth = MoneyAmount(300.00.toBigDecimal(), "USD"),
        games = listOf(
            Game(
                name = "Higurashi When They Cry - Ch. 8 Matsuribayashi",
                iconUrl = "",
                price = GamePrice.PriceTag(MoneyAmount("389.00".toBigDecimal(), "RUB"))
            ),
            Game(
                name = "Dota 2",
                iconUrl = "",
                price = GamePrice.Free,
            ),
            Game(
                name = "Half-Life 3",
                iconUrl = "",
                price = GamePrice.NotAvailable
            )
        ),
        country = Country(
            name = "Россия",
            isoCountryCode = "ru"
        )
    )
    SteamNetWorthTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            SteamNetWorthContent(
                content = state,
                onCountryChangeClick = {}
            )
        }
    }
}