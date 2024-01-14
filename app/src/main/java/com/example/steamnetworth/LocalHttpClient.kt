package com.example.steamnetworth

import androidx.compose.runtime.staticCompositionLocalOf
import io.ktor.client.HttpClient

val LocalHttpClient = staticCompositionLocalOf<HttpClient> {
    throw IllegalStateException("Nothing to provide")
}