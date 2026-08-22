package com.example.data.model

data class GameIdProfile(
    val gameType: GameType,
    val inGameId: String,
    val inGameName: String,
    val rankTier: String = "بلاتينيوم"
)

data class UserStats(
    val matchesPlayed: Int = 42,
    val matchesWon: Int = 18,
    val winRatePercent: Double = 42.8,
    val totalTournamentsEntered: Int = 14,
    val totalWinningsSDG: Long = 185000,
    val mvpCount: Int = 9,
    val killsTotal: Int = 312
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

data class User(
    val id: String,
    val username: String,
    val fullName: String,
    val email: String,
    val phone: String,
    val location: String = "نيالا، جنوب دارفور",
    val avatarUrl: String = "",
    val level: Int = 12,
    val currentXp: Int = 3450,
    val nextLevelXp: Int = 5000,
    val rewardPoints: Int = 1850,
    // Strict Real-Money Wallet separation
    val depositBalanceSDG: Long = 25000, // Non-withdrawable, used for tournament entry fees
    val tournamentWinningsSDG: Long = 120000, // Approved tournament winnings, fully withdrawable
    val gameProfiles: Map<String, GameIdProfile> = mapOf(
        GameType.FREE_FIRE.id to GameIdProfile(GameType.FREE_FIRE, "FF-9823145", "Azom_Sniper_SD"),
        GameType.PUBG_MOBILE.id to GameIdProfile(GameType.PUBG_MOBILE, "519827341", "Nyala_Ghost"),
        GameType.EFOOTBALL.id to GameIdProfile(GameType.EFOOTBALL, "EF-881920", "Darfur_Messi")
    ),
    val stats: UserStats = UserStats(),
    val currentTeamId: String? = "team_nyala_wolves",
    val currentTeamName: String? = "ذئاب نيالا (Nyala Wolves)",
    val achievements: List<Achievement> = emptyList(),
    val seasonRank: String = "المركز 4 - نخبة دارفور"
) {
    val totalAvailableBalanceSDG: Long get() = depositBalanceSDG + tournamentWinningsSDG
    val xpProgress: Float get() = if (nextLevelXp > 0) currentXp.toFloat() / nextLevelXp else 0f
}
