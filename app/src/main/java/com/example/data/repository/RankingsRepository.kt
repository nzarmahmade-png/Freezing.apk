package com.example.data.repository

import com.example.data.datasource.LocalMockDataSource
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

interface IRankingsRepository {
    val currentSeasonFlow: Flow<Season>
    suspend fun getLeaderboard(scope: RankingScope): List<LeaderboardEntry>
    suspend fun claimSeasonReward(tierName: String): Result<Pair<Int, Long>> // points, cashSDG
}

class RankingsRepository : IRankingsRepository {
    private val _season = MutableStateFlow<Season>(LocalMockDataSource.currentSeason)
    override val currentSeasonFlow: Flow<Season> = _season.asStateFlow()

    override suspend fun getLeaderboard(scope: RankingScope): List<LeaderboardEntry> {
        val base = _season.value.topLeaderboard
        return when (scope) {
            RankingScope.GLOBAL -> base
            RankingScope.REGIONAL_DARFUR -> base.filter { it.location.contains("نيالا") || it.location.contains("الفاشر") || it.location.contains("الجنينة") || it.location.contains("الضعين") || it.location.contains("زالنجي") }
            RankingScope.SEASONAL -> base
            RankingScope.FREE_FIRE -> base.mapIndexed { idx, item -> item.copy(rank = idx + 1, scorePoints = item.scorePoints + 200) }
            RankingScope.PUBG_MOBILE -> base.reversed().mapIndexed { idx, item -> item.copy(rank = idx + 1, scorePoints = item.scorePoints - 100) }
            RankingScope.EFOOTBALL -> base.mapIndexed { idx, item -> item.copy(rank = idx + 1) }
        }
    }

    override suspend fun claimSeasonReward(tierName: String): Result<Pair<Int, Long>> {
        val s = _season.value
        val list = s.rewards.toMutableList()
        val index = list.indexOfFirst { it.tierNameArabic == tierName }
        if (index == -1) return Result.failure(Exception("المستوى غير موجود"))

        val reward = list[index]
        if (!reward.isUnlocked) return Result.failure(Exception("هذا المستوى لم يتم فتحه بعد"))
        if (reward.isClaimed) return Result.failure(Exception("تم استلام مكافأة هذا المستوى بالفعل"))

        list[index] = reward.copy(isClaimed = true)
        _season.value = s.copy(rewards = list)
        return Result.success(Pair(reward.pointsReward, reward.cashRewardSDG))
    }
}
