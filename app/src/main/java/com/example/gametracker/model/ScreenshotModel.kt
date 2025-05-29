package com.example.gametracker.model

data class ScreenshotResponse(
    val results: List<Screenshot>
)

data class Screenshot(
    val id: Int,
    val image: String
)
