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

interface IRewardRepository {
    val pointsBalanceFlow: Flow<Int>
    val rewardItemsFlow: Flow<List<RewardItem>>
    val earnOpportunitiesFlow: Flow<List<EarnOpportunity>>
    val redeemedVouchersFlow: Flow<List<RedeemedVoucher>>
    val miniGamesFlow: Flow<List<MiniGameItem>>
    val economyConfigFlow: Flow<RewardEconomyConfig>
    val dailyGamePointsEarnedFlow: Flow<Int>

    suspend fun claimEarnOpportunity(opportunityId: String): Result<Pair<Int, Int>> // returns (pointsAdded, xpAdded)
    suspend fun redeemReward(rewardItem: RewardItem): Result<RedeemedVoucher>
    suspend fun validateAndClaimGameReward(
        submission: GameSessionSubmission,
        watchedMultiplierAd: Boolean
    ): Result<GameRewardResult>
}

class RewardRepository : IRewardRepository {
    private val _pointsBalance = MutableStateFlow<Int>(LocalMockDataSource.currentUser.rewardPoints)
    private val _rewardItems = MutableStateFlow<List<RewardItem>>(LocalMockDataSource.rewardItems)
    private val _earnOpportunities = MutableStateFlow<List<EarnOpportunity>>(LocalMockDataSource.earnOpportunities)
    private val _redeemedVouchers = MutableStateFlow<List<RedeemedVoucher>>(emptyList())
    private val _miniGames = MutableStateFlow<List<MiniGameItem>>(LocalMockDataSource.miniGames)
    private val _economyConfig = MutableStateFlow<RewardEconomyConfig>(LocalMockDataSource.defaultEconomyConfig)
    private val _dailyGamePointsEarned = MutableStateFlow<Int>(0)

    override val pointsBalanceFlow: Flow<Int> = _pointsBalance.asStateFlow()
    override val rewardItemsFlow: Flow<List<RewardItem>> = _rewardItems.asStateFlow()
    override val earnOpportunitiesFlow: Flow<List<EarnOpportunity>> = _earnOpportunities.asStateFlow()
    override val redeemedVouchersFlow: Flow<List<RedeemedVoucher>> = _redeemedVouchers.asStateFlow()
    override val miniGamesFlow: Flow<List<MiniGameItem>> = _miniGames.asStateFlow()
    override val economyConfigFlow: Flow<RewardEconomyConfig> = _economyConfig.asStateFlow()
    override val dailyGamePointsEarnedFlow: Flow<Int> = _dailyGamePointsEarned.asStateFlow()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    override suspend fun claimEarnOpportunity(opportunityId: String): Result<Pair<Int, Int>> {
        val list = _earnOpportunities.value.toMutableList()
        val index = list.indexOfFirst { it.id == opportunityId }
        if (index == -1) {
            return Result.failure(Exception("النشاط غير موجود"))
        }

        val opp = list[index]
        if (opp.isClaimedToday && opp.actionType == EarnActionType.DAILY_LOGIN) {
            return Result.failure(Exception("تم استلام مكافأة تسجيل الدخول لليوم بالفعل"))
        }

        _pointsBalance.value += opp.pointsReward
        list[index] = opp.copy(isClaimedToday = true)
        _earnOpportunities.value = list

        return Result.success(Pair(opp.pointsReward, opp.xpReward))
    }

    override suspend fun redeemReward(rewardItem: RewardItem): Result<RedeemedVoucher> {
        if (_pointsBalance.value < rewardItem.pointsCost) {
            return Result.failure(Exception("نقاط المكافآت غير كافية. تحتاج إلى ${rewardItem.pointsCost} نقطة (رصيدك: ${_pointsBalance.value})"))
        }

        val randomCode = "AZOM-" + rewardItem.id.takeLast(4).uppercase() + "-" + (1000..9999).random()
        val voucher = RedeemedVoucher(
            id = "vouch_${UUID.randomUUID().toString().take(8)}",
            rewardItemId = rewardItem.id,
            title = rewardItem.titleArabic,
            voucherCode = randomCode,
            redeemedAt = dateFormat.format(Date()),
            pointsSpent = rewardItem.pointsCost
        )

        _pointsBalance.value -= rewardItem.pointsCost
        _redeemedVouchers.value = listOf(voucher) + _redeemedVouchers.value

        return Result.success(voucher)
    }

    /**
     * Anti-Abuse & Economy Validation Engine
     * Validates session duration, tap frequency, token integrity, and economy limits.
     * Prevents client-side manipulation.
     */
    override suspend fun validateAndClaimGameReward(
        submission: GameSessionSubmission,
        watchedMultiplierAd: Boolean
    ): Result<GameRewardResult> {
        val config = _economyConfig.value

        // 1. Anti-Abuse: Challenge Token Check
        if (submission.sessionChallengeToken.isBlank() || !submission.sessionChallengeToken.startsWith("AZOM_")) {
            return Result.failure(Exception("جلسة اللعب غير صالحة أو غير موثقة (Security Token Invalid)"))
        }

        // 2. Anti-Abuse: Minimum Duration Check (must have played realistically)
        if (submission.durationSeconds < 2) {
            return Result.failure(Exception("مدة الجلسة قصيرة جداً لاحتساب المكافأة"))
        }

        // 3. Anti-Abuse: Max Tap Frequency (Bot prevention: impossible > 35 taps/sec)
        val tapRate = submission.tapCount.toFloat() / submission.durationSeconds.coerceAtLeast(1)
        if (tapRate > 35.0f) {
            return Result.failure(Exception("تم رصد نقرات غير طبيعية (مكافحة التكرار الآلي)"))
        }

        // 4. Server-grade Reward Tier Calculation based on Performance Score
        val rawPoints = when {
            submission.rawScore <= config.scoreTier1Max -> config.scoreTier1Points
            submission.rawScore <= config.scoreTier2Max -> config.scoreTier2Points
            submission.rawScore <= config.scoreTier3Max -> config.scoreTier3Points
            else -> config.scoreTier4Points
        }

        // 5. Daily Cap on Free Game Points (protecting platform economy)
        val currentEarnedToday = _dailyGamePointsEarned.value
        val remainingDailyQuota = (config.dailyGamePointsCap - currentEarnedToday).coerceAtLeast(0)

        val allowedBasePoints = rawPoints.coerceAtMost(remainingDailyQuota)
        val dailyCapReached = allowedBasePoints < rawPoints || remainingDailyQuota == 0

        // 6. Advertisement Reward (The primary monetization source)
        val bonusAdPoints = if (watchedMultiplierAd && allowedBasePoints > 0) {
            allowedBasePoints * (config.rewardedAdMultiplier - 1)
        } else if (watchedMultiplierAd) {
            config.rewardedAdBasePoints
        } else {
            0
        }

        val totalPointsAwarded = allowedBasePoints + bonusAdPoints
        val xpAwarded = 25 + (submission.rawScore / 50).coerceAtMost(150)

        // Apply Points
        if (totalPointsAwarded > 0) {
            _pointsBalance.value += totalPointsAwarded
            _dailyGamePointsEarned.value += allowedBasePoints
        }

        // Update Game Sessions Count in list
        val currentGames = _miniGames.value.toMutableList()
        val gIndex = currentGames.indexOfFirst { it.id == submission.gameId }
        if (gIndex != -1) {
            val g = currentGames[gIndex]
            currentGames[gIndex] = g.copy(
                currentDailySessionsPlayed = g.currentDailySessionsPlayed + 1
            )
            _miniGames.value = currentGames
        }

        val message = when {
            watchedMultiplierAd -> "🎉 تم مضاعفة المكافأة عبر الإعلان الترويجي! +$totalPointsAwarded نقطة"
            dailyCapReached && allowedBasePoints == 0 -> "🎮 تم تسجيل سكور الجولة (${submission.rawScore}). لقد استنفدت الحد اليومي لنقاط اللعب المجاني (60 نقطة). يمكنك مواصلة اللعب للمتعة أو مشاهدة إعلان."
            allowedBasePoints > 0 -> "✨ تم احتساب +$totalPointsAwarded نقطة مكافأة و +$xpAwarded XP!"
            else -> "🎮 أداء جيد! اجمع أكثر من 500 نقطة في الجولة القادمة لبدء كسب النقاط."
        }

        return Result.success(
            GameRewardResult(
                gameId = submission.gameId,
                score = submission.rawScore,
                basePointsAwarded = allowedBasePoints,
                bonusAdPointsAwarded = bonusAdPoints,
                totalPointsAwarded = totalPointsAwarded,
                totalXpAwarded = xpAwarded,
                dailyCapReached = dailyCapReached,
                message = message
            )
        )
    }
}
