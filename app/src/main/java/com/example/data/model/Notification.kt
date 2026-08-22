package com.example.data.model

enum class NotificationType(val titleArabic: String, val iconName: String) {
    TOURNAMENT("تنبيه بطولة", "emoji_events"),
    MATCH("غرفة ومباراة", "sports_esports"),
    WINNING("أرباح بطولة", "payments"),
    DEPOSIT("إيداع رصيد", "account_balance_wallet"),
    WITHDRAWAL("سحب أرباح", "account_balance"),
    REWARD("نقاط ومكافآت", "card_giftcard"),
    ANNOUNCEMENT("إعلان عام", "campaign")
}

data class NotificationItem(
    val id: String,
    val titleArabic: String,
    val messageArabic: String,
    val type: NotificationType,
    val timestamp: String,
    val isRead: Boolean = false,
    val targetId: String? = null
)
