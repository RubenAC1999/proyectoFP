package com.example.gametracker.data.remote

import com.example.gametracker.model.GameModel
import com.example.gametracker.model.ScreenshotResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RawgApiService {
    @GET("games")
    suspend fun searchGames(
        @Query("key") apiKey: String,
        @Query("search") query: String,
        @Query("page_size") pageSize: Int = 10
    ): GameModel.GameResponse


    @GET("games")
    suspend fun getPopularGames(
        @Query("key") apiKey: String,
        @Query("ordering") ordering: String = "-added",
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 10
    ): GameModel.GameResponse

    @GET("games")
    suspend fun getTopRatedGames(
        @Query("key") apiKey: String,
        @Query("ordering") ordering: String = "-metacritic",
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 10
    ): GameModel.GameResponse

    @GET("games/{id}")
    suspend fun getGameDetail(
        @retrofit2.http.Path("id") gameId: Int,
        @Query("key") apiKey: String
    ): GameModel.Game

    @GET("games/{id}/screenshots")
    suspend fun getGameScreenshots(
        @Path("id") gameId: Int,
        @Query("key") apiKey: String
    ): ScreenshotResponse

    @GET("games")
    suspend fun getGamesByGenre(
        @Query("key") apiKey: String,
        @Query("genres") genre: String,
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 10
    ): GameModel.GameResponse

    @GET("games")
    suspend fun getGamesByYear(
        @Query("key") apiKey: String,
        @Query("dates") dates: String,
        @Query("ordering") ordering: String,
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 10
    ): GameModel.GameResponse





}