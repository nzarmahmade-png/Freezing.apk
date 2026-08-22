package com.example.data.model

enum class RankingScope(val titleArabic: String) {
    GLOBAL("العام (السودان)"),
    REGIONAL_DARFUR("إقليم دارفور ونيالا"),
    SEASONAL("الموسم الحالي"),
    FREE_FIRE("فري فاير"),
    PUBG_MOBILE("ببجي موبايل"),
    EFOOTBALL("إي فوتبول")
}

data class LeaderboardEntry(
    val rank: Int,
    val userId: String,
    val username: String,
    val location: String,
    val tierTitle: String,
    val scorePoints: Int,
    val matchesWon: Int,
    val winRate: Double,
    val isCurrentUser: Boolean = false
)

data class SeasonTierReward(
    val tierNameArabic: String,
    val requiredXp: Int,
    val pointsReward: Int,
    val cashRewardSDG: Long,
    val badgeName: String,
    val isClaimed: Boolean = false,
    val isUnlocked: Boolean = false
)

data class Season(
    val seasonNumber: Int,
    val seasonTitleArabic: String,
    val seasonThemeArabic: String,
    val daysRemaining: Int,
    val totalPrizePoolSDG: Long,
    val currentTierArabic: String,
    val userSeasonXp: Int,
    val maxSeasonXp: Int,
    val rewards: List<SeasonTierReward>,
    val topLeaderboard: List<LeaderboardEntry>
)
