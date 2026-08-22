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

    suspend fun claimEarnOpportunity(opportunityId: String): Result<Pair<Int, Int>> // returns (pointsAdded, xpAdded)
    suspend fun redeemReward(rewardItem: RewardItem): Result<RedeemedVoucher>
}

class RewardRepository : IRewardRepository {
    private val _pointsBalance = MutableStateFlow<Int>(LocalMockDataSource.currentUser.rewardPoints)
    private val _rewardItems = MutableStateFlow<List<RewardItem>>(LocalMockDataSource.rewardItems)
    private val _earnOpportunities = MutableStateFlow<List<EarnOpportunity>>(LocalMockDataSource.earnOpportunities)
    private val _redeemedVouchers = MutableStateFlow<List<RedeemedVoucher>>(emptyList())

    override val pointsBalanceFlow: Flow<Int> = _pointsBalance.asStateFlow()
    override val rewardItemsFlow: Flow<List<RewardItem>> = _rewardItems.asStateFlow()
    override val earnOpportunitiesFlow: Flow<List<EarnOpportunity>> = _earnOpportunities.asStateFlow()
    override val redeemedVouchersFlow: Flow<List<RedeemedVoucher>> = _redeemedVouchers.asStateFlow()

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
}
