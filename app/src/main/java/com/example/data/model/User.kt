package com.example.data.model

data class GameIdProfile(
    val gameType: GameType,
    val inGameId: String,
    val inGameName: String,
    val rankTier: String = "بلاتينيوم"
)

data class UserStats(
    val matchesPlayed: Int = 68,
    val matchesWon: Int = 32,
    val winRatePercent: Double = 47.1,
    val totalTournamentsEntered: Int = 22,
    val totalWinningsSDG: Long = 285000,
    val mvpCount: Int = 14,
    val killsTotal: Int = 582
)

data class Achievement(
    val id: String,
    val titleArabic: String,
    val descriptionArabic: String,
    val iconName: String,
    val isUnlocked: Boolean,
    val unlockedDate: String? = null,
    val pointsReward: Int = 100
)

data class UsernameChangeRequest(
    val requestedUsername: String,
    val reason: String,
    val status: String = "قيد مراجعة الإدارة",
    val requestDate: String
)

data class User(
    val id: String,
    val username: String,
    val fullName: String,
    val email: String,
    val phone: String,
    val location: String = "نيالا، حي المطار",
    val avatarUrl: String = "",
    val level: Int = 14,
    val currentXp: Int = 4250,
    val nextLevelXp: Int = 6000,
    val rewardPoints: Int = 2450,
    // Strict Real-Money Wallet separation
    val depositBalanceSDG: Long = 35000, // Non-withdrawable, used exclusively for tournament entry fees
    val tournamentWinningsSDG: Long = 150000, // Approved tournament winnings, fully withdrawable to Bankak
    val hasChangedUsernameOnce: Boolean = true, // Username rule: can only be changed once
    val pendingUsernameRequest: UsernameChangeRequest? = null,
    val followersCount: Int = 342,
    val followingCount: Int = 89,
    val followersList: List<String> = listOf("طارق قوست", "إبراهيم ميسي", "أسد دارفور", "قناص الفاشر", "صقر الضعين"),
    val followingList: List<String> = listOf("صقور الفاشر", "فرسان الجنينة", "إدارة عازم الرسمية", "كابتن بيس نيالا"),
    val gameProfiles: Map<String, GameIdProfile> = mapOf(
        GameType.FREE_FIRE.id to GameIdProfile(GameType.FREE_FIRE, "FF-8849201", "Azom_Sniper_SD", "ماستر (Master)"),
        GameType.PUBG_MOBILE.id to GameIdProfile(GameType.PUBG_MOBILE, "510982341", "Nyala_Ghost_SD", "كراون (Crown I)"),
        GameType.EFOOTBALL.id to GameIdProfile(GameType.EFOOTBALL, "EF-992018", "Darfur_Messi", "القسم الأول (Div 1)")
    ),
    val stats: UserStats = UserStats(),
    val currentTeamId: String? = "team_nyala_wolves",
    val currentTeamName: String? = "ذئاب نيالا [NW]",
    val achievements: List<Achievement> = emptyList(),
    val seasonRank: String = "المركز 4 - نخبة دارفور (Tier 1)"
) {
    val totalAvailableBalanceSDG: Long get() = depositBalanceSDG + tournamentWinningsSDG
    val xpProgress: Float get() = if (nextLevelXp > 0) currentXp.toFloat() / nextLevelXp else 0f
}
