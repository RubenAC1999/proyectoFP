package com.example.gametracker.model

import com.google.firebase.Timestamp

data class UserModel(
    val uid: String = "",
    val displayName: String = "",
    val email: String = "",
    val role: String = "casual",
    val bio: String = "",
    val profilePicUrl: String = "",
    val createdAt: Timestamp? = null,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val isBanned: Boolean = false,
    val bannedUntil: Timestamp? = null,
    val blockMessage: String? = "",
    val warningMessage: String? = "",
    val hasReadWarning: Boolean = false,
    val isPrivate: Boolean = false
) {


}