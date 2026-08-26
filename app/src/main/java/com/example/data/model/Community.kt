package com.example.data.model

data class Comment(
    val id: String,
    val authorId: String,
    val authorName: String,
    val authorAvatar: String = "",
    val content: String,
    val timestamp: String,
    val likesCount: Int = 0
)

data class CommunityPost(
    val id: String,
    val authorId: String,
    val authorName: String,
    val authorAvatar: String = "",
    val authorBadge: String = "لاعب محترف",
    val authorLocation: String = "نيالا",
    val content: String,
    val gameTag: GameType? = null,
    val hashtags: List<String> = emptyList(),
    val timestamp: String,
    val likesCount: Int,
    val commentsCount: Int,
    val isLikedByMe: Boolean = false,
    val isFollowingAuthor: Boolean = false,
    val isPinnedByAdmin: Boolean = false,
    val pinnedDurationArabic: String? = null,
    val isAdvertisement: Boolean = false,
    val adCtaUrl: String? = null,
    val adCtaText: String? = null,
    val comments: List<Comment> = emptyList(),
    val isReported: Boolean = false,
    val isBlockedAuthor: Boolean = false
)

data class PlayerSearchResult(
    val id: String,
    val username: String,
    val fullName: String,
    val location: String,
    val level: Int,
    val primaryGame: GameType,
    val inGameId: String,
    val winRate: Double,
    val teamName: String? = null,
    val isFollowing: Boolean = false
)
