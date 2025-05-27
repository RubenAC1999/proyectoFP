package com.example.gametracker.data.remote

import com.example.gametracker.model.GameModel
import retrofit2.http.GET
import retrofit2.http.Query

interface RawgApiService {
    @GET("games")
    suspend fun getGames(
        @Query("key") apiKey: String,
        @Query("page") page: Int = 1
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
        @Query("ordering") ordering: String = "-rating",
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 10
    ): GameModel.GameResponse


}