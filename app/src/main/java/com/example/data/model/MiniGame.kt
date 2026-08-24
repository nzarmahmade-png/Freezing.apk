package com.example.data.model

data class MiniGameItem(
    val id: String,
    val titleArabic: String,
    val shortDescriptionArabic: String,
    val categoryNameArabic: String,
    val assetUrl: String,
    val maxRewardPoints: Int = 20,
    val dailyLimitSessions: Int = 5,
    val currentDailySessionsPlayed: Int = 0,
    val badgeArabic: String? = null,
    val accentColorHex: String = "#FFB800"
)

data class GameSessionSubmission(
    val gameId: String,
    val rawScore: Int,
    val claimedPoints: Int,
    val durationSeconds: Int,
    val tapCount: Int,
    val sessionChallengeToken: String
)

data class GameRewardResult(
    val gameId: String,
    val score: Int,
    val basePointsAwarded: Int,
    val bonusAdPointsAwarded: Int = 0,
    val totalPointsAwarded: Int,
    val totalXpAwarded: Int,
    val dailyCapReached: Boolean = false,
    val message: String
)

data class RewardEconomyConfig(
    val scoreTier1Max: Int = 499,
    val scoreTier1Points: Int = 0,
    val scoreTier2Max: Int = 999,
    val scoreTier2Points: Int = 5,
    val scoreTier3Max: Int = 1999,
    val scoreTier3Points: Int = 10,
    val scoreTier4Points: Int = 20, // 2000+
    val dailyGamePointsCap: Int = 60,
    val rewardedAdBasePoints: Int = 50,
    val rewardedAdMultiplier: Int = 3,
    val sessionCooldownSeconds: Int = 30
)

enum class AdPlacementType(val titleArabic: String) {
    PRE_GAME_BOOST("مضاعف ما قبل الجولة ⚡"),
    POST_GAME_MULTIPLIER("مضاعفة نقاط الجولة 🎬"),
    INTERSTITIAL_SESSION("إعلان بين الجولات"),
    BANNER_FOOTER("شريط إعلاني ترويجي")
}
