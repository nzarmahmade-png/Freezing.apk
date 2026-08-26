package com.example.data.repository

import com.example.data.datasource.LocalMockDataSource
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

interface ICommunityRepository {
    val postsFlow: Flow<List<CommunityPost>>
    val blockedUserIdsFlow: Flow<Set<String>>
    val followingUserIdsFlow: Flow<Set<String>>

    suspend fun createPost(content: String, gameTag: GameType?, author: User): Result<CommunityPost>
    suspend fun toggleLikePost(postId: String): Result<Boolean>
    suspend fun toggleFollowUser(authorId: String): Result<Boolean>
    suspend fun addComment(postId: String, content: String, author: User): Result<Comment>
    suspend fun reportPost(postId: String, reason: String): Result<Unit>
    suspend fun blockUser(authorId: String): Result<Unit>
    suspend fun searchPlayers(query: String): List<PlayerSearchResult>
    suspend fun searchPosts(query: String): List<CommunityPost>
}

class CommunityRepository : ICommunityRepository {

    private val initialPostsWithPinnedAndAds = listOf(
        CommunityPost(
            id = "post_admin_pinned",
            authorId = "admin_official",
            authorName = "إدارة منصة عازم الرسمية 🛡️",
            authorAvatar = "",
            authorBadge = "إدارة المنصة",
            authorLocation = "نيالا - المقر الرئيسي",
            content = "📢 تنبيه رسمي لجميع اللاعبين: ستبدأ الليلة في تمام الساعة 09:00 م تصفيات بطولة كأس أبطال نيالا الكبرى لفري فاير وببجي موبايل. يرجى التواجد في رومات البطولة قبل الموعد بـ 15 دقيقة مع الالتزام باللعب النظيف.",
            gameTag = GameType.FREE_FIRE,
            hashtags = listOf("#بطولة_دارفور", "#فري_فاير", "#نيالا", "#لعب_نظيف"),
            timestamp = "مثبت من الإدارة 📌",
            likesCount = 142,
            commentsCount = 28,
            isLikedByMe = true,
            isFollowingAuthor = true,
            isPinnedByAdmin = true,
            pinnedDurationArabic = "مثبت طوال فترة البطولة",
            comments = listOf(
                Comment("c_adm_1", "u_02", "أحمد صقر الفاشر", "", "جاهزين بأقوى تشكيلة يا إدارة عازم! 🔥", "منذ ساعة", 8)
            )
        ),
        CommunityPost(
            id = "post_1",
            authorId = "user_darfur_pro_01",
            authorName = "محمد نزار (Azom_Sniper)",
            authorAvatar = "",
            authorBadge = "بطل الموسم 🏆",
            authorLocation = "نيالا",
            content = "جاهزين لبطولة كلاش سكواد الليلة؟ فريق ذئاب نيالا مستعد لكأس دارفور! منو من الفرق بيتحدى في النهائي؟ 🔥🎮 #كأس_دارفور #فري_فاير",
            gameTag = GameType.FREE_FIRE,
            hashtags = listOf("#كأس_دارفور", "#فري_فاير", "#ذئاب_نيالا"),
            timestamp = "منذ ساعة",
            likesCount = 38,
            commentsCount = 12,
            isLikedByMe = true,
            isFollowingAuthor = false,
            comments = listOf(
                Comment("c_1", "u_02", "أحمد صقر الفاشر", "", "صقور الفاشر جاهزين ونازلين بقوة الليلة! الوعد في النهائي 🦅", "منذ 45 دقيقة", 5),
                Comment("c_2", "u_03", "عثمان الجنينة", "", "بالتوفيق للجميع، تنظيم ممتاز يا إدارة عازم 👏", "منذ 20 دقيقة", 2)
            )
        ),
        CommunityPost(
            id = "post_sponsor_ad_1",
            authorId = "sponsor_azom_store",
            authorName = "متجر عازم للإلكترونيات 🛍️",
            authorAvatar = "",
            authorBadge = "شريك رسمي معتمد",
            authorLocation = "نيالا - شارع السينما",
            content = "عرض حصري للاعبي منصة عازم! خصم 30% على سماعات الألعاب المحيطية واصابع اللعب الاحترافية. متوفر التوصيل لكافة مدن دارفور.",
            gameTag = null,
            hashtags = listOf("#إعلان", "#متجر_عازم", "#سماعات_ألعاب", "#خصومات"),
            timestamp = "إعلان ترويجي 📢",
            likesCount = 56,
            commentsCount = 4,
            isLikedByMe = false,
            isFollowingAuthor = true,
            isAdvertisement = true,
            adCtaText = "تصفح العروض في متجر عازم",
            adCtaUrl = "https://azomstore.sd"
        ),
        CommunityPost(
            id = "post_2",
            authorId = "user_pubg_master",
            authorName = "طارق قوست (Nyala_Ghost)",
            authorAvatar = "",
            authorBadge = "قناص محترف 🎯",
            authorLocation = "نيالا - حي الوادي",
            content = "محتاجين لاعب رابع سكواد لببجي موبايل لبطولة درع دارفور غداً. الشروط: تقييم كراون فما فوق ومايك شغال. تواصلوا معي خاص أو علقوا بالـ ID. #ببجي_موبايل #كلان_دارفور",
            gameTag = GameType.PUBG_MOBILE,
            hashtags = listOf("#ببجي_موبايل", "#كلان_دارفور", "#سكواد"),
            timestamp = "منذ 3 ساعات",
            likesCount = 24,
            commentsCount = 8,
            isLikedByMe = false,
            isFollowingAuthor = false,
            comments = listOf(
                Comment("c_3", "u_pubg_04", "ياسر الدرع", "", "أنا جاهز يا كابتن! الـ ID: 518829103 - تقييم آيس ومايك جاهز.", "منذ ساعتين", 3)
            )
        ),
        CommunityPost(
            id = "post_3",
            authorId = "user_pes_champ",
            authorName = "إبراهيم ميسي دارفور",
            authorAvatar = "",
            authorBadge = "بطل بيس ⚽",
            authorLocation = "الفاشر",
            content = "تأهلت لنصف نهائي دوري إي فوتبول! مواجهة قوية قادمة في تمام الساعة 8:30 م. دعواتكم يا شباب بالفوز بالكأس 🏆⚽ #إي_فوتبول #بيس2026",
            gameTag = GameType.EFOOTBALL,
            hashtags = listOf("#إي_فوتبول", "#بيس2026", "#دوري_الأبطال"),
            timestamp = "منذ 5 ساعات",
            likesCount = 45,
            commentsCount = 15,
            isLikedByMe = true,
            isFollowingAuthor = true
        )
    )

    private val _posts = MutableStateFlow<List<CommunityPost>>(initialPostsWithPinnedAndAds)
    private val _blockedUserIds = MutableStateFlow<Set<String>>(emptySet())
    private val _followingUserIds = MutableStateFlow<Set<String>>(setOf("sponsor_azom_store", "user_pes_champ", "admin_official"))

    override val postsFlow: Flow<List<CommunityPost>> = _posts.asStateFlow()
    override val blockedUserIdsFlow: Flow<Set<String>> = _blockedUserIds.asStateFlow()
    override val followingUserIdsFlow: Flow<Set<String>> = _followingUserIds.asStateFlow()

    private val sampleSearchPlayers = listOf(
        PlayerSearchResult("u_s1", "Azom_Sniper_SD", "محمد نزار", "نيالا", 14, GameType.FREE_FIRE, "FF-8849201", 47.1, "ذئاب نيالا", false),
        PlayerSearchResult("u_s2", "Nyala_Ghost", "طارق قوست", "نيالا", 16, GameType.PUBG_MOBILE, "510982341", 52.4, "صقور الفاشر", false),
        PlayerSearchResult("u_s3", "Darfur_Messi", "إبراهيم ميسي", "الفاشر", 12, GameType.EFOOTBALL, "EF-992018", 68.0, "نمور الضعين", true),
        PlayerSearchResult("u_s4", "Sudan_Legend", "عبدالله الفارس", "الجنينة", 15, GameType.FREE_FIRE, "FF-4401923", 55.2, "فرسان الجنينة", false),
        PlayerSearchResult("u_s5", "Desert_Eagle_FF", "مصطفى قناص", "نيالا", 11, GameType.FREE_FIRE, "FF-3301928", 41.0, null, false)
    )

    override suspend fun createPost(content: String, gameTag: GameType?, author: User): Result<CommunityPost> {
        if (content.isBlank()) {
            return Result.failure(Exception("نص المنشور لا يمكن أن يكون فارغاً"))
        }

        // Extract hashtags
        val extractedHashtags = Regex("#[\\p{L}0-9_]+")
            .findAll(content)
            .map { it.value }
            .toList()

        val newPost = CommunityPost(
            id = "post_${UUID.randomUUID().toString().take(8)}",
            authorId = author.id,
            authorName = "${author.fullName} (${author.username})",
            authorAvatar = author.avatarUrl,
            authorBadge = "عضو مجتمع دارفور",
            authorLocation = author.location.split("،").firstOrNull() ?: "نيالا",
            content = content.trim(),
            gameTag = gameTag,
            hashtags = extractedHashtags,
            timestamp = "الآن",
            likesCount = 0,
            commentsCount = 0,
            isLikedByMe = false,
            isFollowingAuthor = false,
            comments = emptyList()
        )

        // Insert after pinned posts
        val current = _posts.value.toMutableList()
        val insertIndex = current.indexOfLast { it.isPinnedByAdmin }.let { if (it == -1) 0 else it + 1 }
        current.add(insertIndex, newPost)
        _posts.value = current
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

    override suspend fun toggleFollowUser(authorId: String): Result<Boolean> {
        val currentFollowing = _followingUserIds.value.toMutableSet()
        val isNowFollowing = if (currentFollowing.contains(authorId)) {
            currentFollowing.remove(authorId)
            false
        } else {
            currentFollowing.add(authorId)
            true
        }
        _followingUserIds.value = currentFollowing

        // Update posts state
        _posts.value = _posts.value.map { post ->
            if (post.authorId == authorId) {
                post.copy(isFollowingAuthor = isNowFollowing)
            } else post
        }
        return Result.success(isNowFollowing)
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
        val followingSet = _followingUserIds.value
        val list = sampleSearchPlayers.map { it.copy(isFollowing = followingSet.contains(it.id)) }
        if (query.isBlank()) return list
        return list.filter {
            it.username.contains(query, ignoreCase = true) ||
            it.fullName.contains(query, ignoreCase = true) ||
            it.location.contains(query, ignoreCase = true) ||
            it.inGameId.contains(query, ignoreCase = true)
        }
    }

    override suspend fun searchPosts(query: String): List<CommunityPost> {
        if (query.isBlank()) return _posts.value
        val q = query.trim().lowercase()
        return _posts.value.filter { post ->
            post.content.lowercase().contains(q) ||
            post.authorName.lowercase().contains(q) ||
            post.hashtags.any { it.lowercase().contains(q) }
        }
    }
}
