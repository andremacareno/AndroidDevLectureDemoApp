package com.example.steamnetworth

import com.example.steamnetworth.data.SteamUserInfoLocalDataSource
import com.example.steamnetworth.data.SteamUserInfoRemoteDataSource
import com.example.steamnetworth.domain.SteamUserInfoRepositoryImpl
import com.example.steamnetworth.models.UserInfo
import kotlinx.coroutines.runBlocking
import org.junit.Test

internal class SteamUserInfoRepositoryTest {

    private val userInfoStub = UserInfo(name = "", avatarUrl = "")

    @Test
    fun `Obtain data from local data source if exist`() = runBlocking {
        val localDataSource = object : SteamUserInfoLocalDataSource {

            var localDataSourceCallsCount = 0

            override suspend fun getUserInfo(): UserInfo {

                return userInfoStub.also { localDataSourceCallsCount++ }
            }

            override suspend fun saveUserInfo(userInfo: UserInfo) = Unit
        }
        val remoteDataSource = object : SteamUserInfoRemoteDataSource {

            var remoteDatasourceCallsCount = 0;

            override suspend fun getUserInfo(): UserInfo {
                return userInfoStub.also { remoteDatasourceCallsCount++ }
            }
        }
        val repository = SteamUserInfoRepositoryImpl(localDataSource, remoteDataSource)
        repository.getUserInfo()
        assert(localDataSource.localDataSourceCallsCount == 1 &&
            remoteDataSource.remoteDatasourceCallsCount == 0
        )
    }

    @Test
    fun `Obtain data from remote data source if missing local`() = runBlocking {
        val localDataSource = object : SteamUserInfoLocalDataSource {
            override suspend fun getUserInfo() = null

            override suspend fun saveUserInfo(userInfo: UserInfo) = Unit
        }
        val remoteDataSource = object : SteamUserInfoRemoteDataSource {

            var remoteDatasourceCallsCount = 0;

            override suspend fun getUserInfo(): UserInfo {
                return userInfoStub.also { remoteDatasourceCallsCount++ }
            }
        }
        val repository = SteamUserInfoRepositoryImpl(localDataSource, remoteDataSource)
        repository.getUserInfo()
        assert(remoteDataSource.remoteDatasourceCallsCount == 1)
    }

    @Test
    fun `Save data from remote data source`() = runBlocking {
        val localDataSource = object : SteamUserInfoLocalDataSource {

            var saveUserInfoCallsCount = 0;

            override suspend fun getUserInfo() = null

            override suspend fun saveUserInfo(userInfo: UserInfo) {
                saveUserInfoCallsCount++
            }
        }
        val remoteDataSource = object : SteamUserInfoRemoteDataSource {

            var remoteDatasourceCallsCount = 0;

            override suspend fun getUserInfo(): UserInfo {
                return userInfoStub.also { remoteDatasourceCallsCount++ }
            }
        }
        val repository = SteamUserInfoRepositoryImpl(localDataSource, remoteDataSource)
        repository.getUserInfo()
        assert(localDataSource.saveUserInfoCallsCount == 1)
    }
}