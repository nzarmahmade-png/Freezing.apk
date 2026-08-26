package com.example.data.model

enum class DisappearingTimer(val titleArabic: String, val seconds: Long) {
    OFF("بدون توقيت (دائم)", 0),
    TEN_SECONDS("10 ثوانٍ", 10),
    ONE_MINUTE("دقيقة واحدة", 60),
    ONE_HOUR("ساعة واحدة", 3600),
    TWENTY_FOUR_HOURS("24 ساعة", 86400),
    VIEW_ONCE("عرض لمرة واحدة 👁️", -1)
}

data class ChatMessage(
    val id: String,
    val senderId: String,
    val senderName: String,
    val senderAvatar: String = "",
    val text: String,
    val imageUrl: String? = null,
    val isVoiceNote: Boolean = false,
    val voiceDurationSec: Int = 0,
    val timestamp: String,
    val disappearingTimer: DisappearingTimer = DisappearingTimer.OFF,
    val expiresAtTimestamp: Long? = null,
    val isViewOnce: Boolean = false,
    val isViewed: Boolean = false,
    val isMine: Boolean = false
)

data class ChatConversation(
    val id: String,
    val participantId: String,
    val participantName: String,
    val participantAvatar: String = "",
    val participantLocation: String = "نيالا",
    val participantGame: GameType = GameType.FREE_FIRE,
    val isGroup: Boolean = false,
    val lastMessage: String,
    val lastMessageTime: String,
    val unreadCount: Int = 0,
    val disappearingTimer: DisappearingTimer = DisappearingTimer.OFF,
    val isBlocked: Boolean = false,
    val isOnline: Boolean = true
)
