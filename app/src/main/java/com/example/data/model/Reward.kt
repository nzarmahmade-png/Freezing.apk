package com.example.data.model

enum class RewardCategory(val titleArabic: String) {
    GAME_CARDS("بطاقات ألعاب وشحن"),
    TOURNAMENT_PASS("تذاكر وبطولات"),
    AZOM_STORE("منتجات متجر عازم"),
    DISCOUNTS("قسائم خصم")
}

data class RewardItem(
    val id: String,
    val titleArabic: String,
    val descriptionArabic: String,
    val category: RewardCategory,
    val pointsCost: Int,
    val estimatedValueSDG: Long,
    val stockCount: Int,
    val badgeArabic: String? = null,
    val redemptionInstructions: String
)

data class RedeemedVoucher(
    val id: String,
    val rewardItemId: String,
    val title: String,
    val voucherCode: String,
    val redeemedAt: String,
    val pointsSpent: Int,
    val isUsed: Boolean = false
)

data class EarnOpportunity(
    val id: String,
    val titleArabic: String,
    val descriptionArabic: String,
    val pointsReward: Int,
    val xpReward: Int,
    val actionType: EarnActionType,
    val isClaimedToday: Boolean = false,
    val cooldownSeconds: Int = 0
)

enum class EarnActionType {
    DAILY_LOGIN,
    WATCH_REWARD_AD,
    WIN_TOURNAMENT_MATCH,
    INVITE_FRIEND,
    SHARE_COMMUNITY_POST
}
