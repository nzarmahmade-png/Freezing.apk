package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.EFootballColor
import com.example.ui.theme.FreeFireColor
import com.example.ui.theme.PubgColor

enum class GameType(
    val id: String,
    val titleArabic: String,
    val titleEnglish: String,
    val brandColor: Color,
    val defaultIconName: String
) {
    FREE_FIRE(
        id = "free_fire",
        titleArabic = "فري فاير",
        titleEnglish = "Free Fire",
        brandColor = FreeFireColor,
        defaultIconName = "local_fire_department"
    ),
    PUBG_MOBILE(
        id = "pubg_mobile",
        titleArabic = "ببجي موبايل",
        titleEnglish = "PUBG Mobile",
        brandColor = PubgColor,
        defaultIconName = "sports_esports"
    ),
    EFOOTBALL(
        id = "efootball",
        titleArabic = "إي فوتبول / بيس",
        titleEnglish = "eFootball / PES",
        brandColor = EFootballColor,
        defaultIconName = "sports_soccer"
    ),
    ALL(
        id = "all",
        titleArabic = "جميع الألعاب",
        titleEnglish = "All Games",
        brandColor = Color(0xFF38BDF8),
        defaultIconName = "grid_view"
    )
}

data class Game(
    val id: String,
    val type: GameType,
    val nameArabic: String,
    val nameEnglish: String,
    val descriptionArabic: String,
    val activeTournamentsCount: Int,
    val activePlayersCount: Int,
    val teamsCount: Int,
    val popularFormat: String,
    val tags: List<String> = emptyList()
)
