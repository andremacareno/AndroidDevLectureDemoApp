package com.example.steamnetworth

import android.app.Application
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpRedirect

class SteamNetWorthApp : Application() {

    companion object {

        @JvmStatic
        private var instance: SteamNetWorthApp? = null

        @JvmStatic
        fun getInstance(): SteamNetWorthApp {
            return instance!!
        }
    }

    lateinit var httpClient: HttpClient

    override fun onCreate() {
        super.onCreate()
        instance = this
        httpClient = HttpClient(CIO) {
            install(HttpRedirect) {
                checkHttpMethod = false
            }
        }
    }
}