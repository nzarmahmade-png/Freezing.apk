package com.example.data.model

data class TeamMember(
    val userId: String,
    val username: String,
    val roleArabic: String, // قائد الفريق, لاعب أساسي, بديل
    val gameId: String,
    val joinedDate: String,
    val isLeader: Boolean = false
)

data class Team(
    val id: String,
    val name: String,
    val tag: String, // e.g. [NW], [DFR]
    val logoEmoji: String = "🐺",
    val primaryGame: GameType,
    val leaderId: String,
    val leaderName: String,
    val members: List<TeamMember>,
    val maxMembers: Int = 8,
    val bio: String,
    val trophiesCount: Int = 5,
    val tournamentsWon: Int = 3,
    val matchesPlayed: Int = 28,
    val winRate: Double = 64.2,
    val region: String = "نيالا - جنوب دارفور",
    val isRecruiting: Boolean = true
)
