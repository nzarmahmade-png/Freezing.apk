package com.example.data.model

enum class TournamentStatus(val titleArabic: String) {
    REGISTRATION_OPEN("التسجيل مفتوح"),
    UPCOMING("قادمة قريباً"),
    LIVE("مباشرة الآن"),
    COMPLETED("منتهية")
}

enum class TournamentFormat(val titleArabic: String, val teamSize: Int) {
    SOLO("فردي (Solo)", 1),
    DUO("ثنائي (Duo)", 2),
    SQUAD("سكواد (Squad 4v4)", 4),
    ONE_VS_ONE("1 ضد 1 (eFootball)", 1)
}

data class TournamentRule(
    val id: Int,
    val ruleArabic: String
)

data class TournamentMatch(
    val id: String,
    val roundName: String,
    val team1Name: String,
    val team2Name: String,
    val team1Score: Int? = null,
    val team2Score: Int? = null,
    val scheduledTime: String,
    val isCompleted: Boolean = false,
    val winnerName: String? = null
)

data class TournamentStanding(
    val rank: Int,
    val playerName: String,
    val teamName: String? = null,
    val points: Int,
    val kills: Int = 0,
    val prizeSDG: Long = 0
)

data class RegisteredPlayer(
    val userId: String,
    val username: String,
    val gameId: String,
    val registeredAt: String,
    val teamName: String? = null
)

data class Tournament(
    val id: String,
    val title: String,
    val gameType: GameType,
    val format: TournamentFormat,
    val status: TournamentStatus,
    val entryFeeSDG: Long, // in Sudanese Pounds (SDG)
    val entryFeePoints: Int, // optional payment via Reward Points
    val totalPrizePoolSDG: Long,
    val firstPlacePrizeSDG: Long,
    val secondPlacePrizeSDG: Long,
    val thirdPlacePrizeSDG: Long,
    val maxParticipants: Int,
    val registeredCount: Int,
    val startDateArabic: String,
    val startTimeArabic: String,
    val serverRegion: String = "نيالا / دارفور",
    val organizer: String = "إدارة منصة عازم",
    val description: String,
    val rules: List<String>,
    val customRoomId: String? = null,
    val customRoomPassword: String? = null,
    val registeredPlayers: List<RegisteredPlayer> = emptyList(),
    val matches: List<TournamentMatch> = emptyList(),
    val standings: List<TournamentStanding> = emptyList(),
    val isFeatured: Boolean = false
) {
    val isFull: Boolean get() = registeredCount >= maxParticipants
    val seatsLeft: Int get() = (maxParticipants - registeredCount).coerceAtLeast(0)
    val progressRatio: Float get() = if (maxParticipants > 0) registeredCount.toFloat() / maxParticipants else 0f
}
