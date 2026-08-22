package com.example.data.repository

import com.example.data.datasource.LocalMockDataSource
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

interface ICommunityRepository {
    val postsFlow: Flow<List<CommunityPost>>
    val blockedUserIdsFlow: Flow<Set<String>>

    suspend fun createPost(content: String, gameTag: GameType?, author: User): Result<CommunityPost>
    suspend fun toggleLikePost(postId: String): Result<Boolean>
    suspend fun addComment(postId: String, content: String, author: User): Result<Comment>
    suspend fun reportPost(postId: String, reason: String): Result<Unit>
    suspend fun blockUser(authorId: String): Result<Unit>
    suspend fun searchPlayers(query: String): List<PlayerSearchResult>
}

class CommunityRepository : ICommunityRepository {
    private val _posts = MutableStateFlow<List<CommunityPost>>(LocalMockDataSource.communityPosts)
    private val _blockedUserIds = MutableStateFlow<Set<String>>(emptySet())

    override val postsFlow: Flow<List<CommunityPost>> = _posts.asStateFlow()
    override val blockedUserIdsFlow: Flow<Set<String>> = _blockedUserIds.asStateFlow()

    private val sampleSearchPlayers = listOf(
        PlayerSearchResult("u_s1", "Azom_Sniper_SD", "محمد نزار", "نيالا", 14, GameType.FREE_FIRE, "FF-8849201", 47.1, "ذئاب نيالا"),
        PlayerSearchResult("u_s2", "Nyala_Ghost", "طارق قوست", "نيالا", 16, GameType.PUBG_MOBILE, "510982341", 52.4, "صقور الفاشر"),
        PlayerSearchResult("u_s3", "Darfur_Messi", "إبراهيم ميسي", "الفاشر", 12, GameType.EFOOTBALL, "EF-992018", 68.0, "نمور الضعين"),
        PlayerSearchResult("u_s4", "Sudan_Legend", "عبدالله الفارس", "الجنينة", 15, GameType.FREE_FIRE, "FF-4401923", 55.2, "فرسان الجنينة"),
        PlayerSearchResult("u_s5", "Desert_Eagle_FF", "مصطفى قناص", "نيالا", 11, GameType.FREE_FIRE, "FF-3301928", 41.0, null)
    )

    override suspend fun createPost(content: String, gameTag: GameType?, author: User): Result<CommunityPost> {
        if (content.isBlank()) {
            return Result.failure(Exception("نص المنشور لا يمكن أن يكون فارغاً"))
        }

        val newPost = CommunityPost(
            id = "post_${UUID.randomUUID().toString().take(8)}",
            authorId = author.id,
            authorName = "${author.fullName} (${author.username})",
            authorAvatar = author.avatarUrl,
            authorBadge = "عضو مجتمع دارفور",
            authorLocation = author.location.split("،").firstOrNull() ?: "نيالا",
            content = content.trim(),
            gameTag = gameTag,
            timestamp = "الآن",
            likesCount = 0,
            commentsCount = 0,
            isLikedByMe = false,
            comments = emptyList()
        )

        _posts.value = listOf(newPost) + _posts.value
        return Result.success(newPost)
    }

    override suspend fun toggleLikePost(postId: String): Result<Boolean> {
        val list = _posts.value.toMutableList()
        val index = list.indexOfFirst { it.id == postId }
        if (index == -1) return Result.failure(Exception("المنشور غير موجود"))

        val post = list[index]
        val newLikedState = !post.isLikedByMe
        val newLikesCount = if (newLikedState) post.likesCount + 1 else (post.likesCount - 1).coerceAtLeast(0)

        list[index] = post.copy(
            isLikedByMe = newLikedState,
            likesCount = newLikesCount
        )
        _posts.value = list
        return Result.success(newLikedState)
    }

    override suspend fun addComment(postId: String, content: String, author: User): Result<Comment> {
        if (content.isBlank()) return Result.failure(Exception("نص التعليق فارغ"))

        val list = _posts.value.toMutableList()
        val index = list.indexOfFirst { it.id == postId }
        if (index == -1) return Result.failure(Exception("المنشور غير موجود"))

        val post = list[index]
        val newComment = Comment(
            id = "c_${UUID.randomUUID().toString().take(8)}",
            authorId = author.id,
            authorName = author.username,
            authorAvatar = author.avatarUrl,
            content = content.trim(),
            timestamp = "الآن"
        )

        list[index] = post.copy(
            comments = post.comments + newComment,
            commentsCount = post.commentsCount + 1
        )
        _posts.value = list
        return Result.success(newComment)
    }

    override suspend fun reportPost(postId: String, reason: String): Result<Unit> {
        val list = _posts.value.toMutableList()
        val index = list.indexOfFirst { it.id == postId }
        if (index != -1) {
            list[index] = list[index].copy(isReported = true)
            _posts.value = list
        }
        return Result.success(Unit)
    }

    override suspend fun blockUser(authorId: String): Result<Unit> {
        val currentBlocked = _blockedUserIds.value.toMutableSet()
        currentBlocked.add(authorId)
        _blockedUserIds.value = currentBlocked

        // Filter out posts from blocked user
        _posts.value = _posts.value.filter { it.authorId != authorId }
        return Result.success(Unit)
    }

    override suspend fun searchPlayers(query: String): List<PlayerSearchResult> {
        if (query.isBlank()) return sampleSearchPlayers
        return sampleSearchPlayers.filter {
            it.username.contains(query, ignoreCase = true) ||
            it.fullName.contains(query, ignoreCase = true) ||
            it.location.contains(query, ignoreCase = true) ||
            it.inGameId.contains(query, ignoreCase = true)
        }
    }
}
