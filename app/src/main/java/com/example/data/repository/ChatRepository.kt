package com.example.data.repository

import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

interface IChatRepository {
    val conversationsFlow: Flow<List<ChatConversation>>
    val messagesFlow: Flow<Map<String, List<ChatMessage>>> // conversationId -> messages

    suspend fun getOrCreateConversation(participantId: String, participantName: String, gameType: GameType): ChatConversation
    suspend fun sendMessage(conversationId: String, text: String, disappearingTimer: DisappearingTimer, imageUrl: String? = null, isVoiceNote: Boolean = false, voiceDurationSec: Int = 0): Result<ChatMessage>
    suspend fun markViewOnceAsViewed(conversationId: String, messageId: String): Result<Unit>
    suspend fun purgeExpiredMessages()
    suspend fun setConversationDisappearingTimer(conversationId: String, timer: DisappearingTimer): Result<Unit>
    suspend fun blockConversation(conversationId: String): Result<Unit>
    suspend fun reportConversation(conversationId: String, reason: String): Result<Unit>
}

class ChatRepository : IChatRepository {

    private val _conversations = MutableStateFlow<List<ChatConversation>>(
        listOf(
            ChatConversation(
                id = "conv_1",
                participantId = "u_02",
                participantName = "أحمد صقر الفاشر (SF)",
                participantLocation = "الفاشر",
                participantGame = GameType.FREE_FIRE,
                isGroup = false,
                lastMessage = "جاهزين لمواجهة نصف النهائي الليلة!",
                lastMessageTime = "10:30 م",
                unreadCount = 1,
                disappearingTimer = DisappearingTimer.OFF,
                isOnline = true
            ),
            ChatConversation(
                id = "conv_2",
                participantId = "user_pubg_master",
                participantName = "طارق قوست (Nyala_Ghost)",
                participantLocation = "نيالا",
                participantGame = GameType.PUBG_MOBILE,
                isGroup = false,
                lastMessage = "أرسلت لك الـ ID الخاص بسكواد ببجي",
                lastMessageTime = "08:15 م",
                unreadCount = 0,
                disappearingTimer = DisappearingTimer.ONE_HOUR,
                isOnline = false
            ),
            ChatConversation(
                id = "conv_grp_wolves",
                participantId = "grp_wolves",
                participantName = "قروب سكواد ذئاب نيالا [NW]",
                participantLocation = "نيالا",
                participantGame = GameType.FREE_FIRE,
                isGroup = true,
                lastMessage = "خالد: دخلت الروم يا شباب، جاهز بالمايك",
                lastMessageTime = "09:00 م",
                unreadCount = 3,
                disappearingTimer = DisappearingTimer.TWENTY_FOUR_HOURS,
                isOnline = true
            )
        )
    )

    private val _messages = MutableStateFlow<Map<String, List<ChatMessage>>>(
        mapOf(
            "conv_1" to listOf(
                ChatMessage(
                    id = "m_101",
                    senderId = "u_02",
                    senderName = "أحمد صقر الفاشر",
                    text = "السلام عليكم كابتن محمد، مباراة اليوم الساعة 9:30 إن شاء الله؟",
                    timestamp = "09:15 م",
                    isMine = false
                ),
                ChatMessage(
                    id = "m_102",
                    senderId = "user_darfur_pro_01",
                    senderName = "محمد نزار",
                    text = "وعليكم السلام، بإذن الله الروم بيفتح قبلها بربع ساعة بالتوفيق للجميع 🔥",
                    timestamp = "09:20 م",
                    isMine = true
                ),
                ChatMessage(
                    id = "m_103",
                    senderId = "u_02",
                    senderName = "أحمد صقر الفاشر",
                    text = "جاهزين لمواجهة نصف النهائي الليلة!",
                    timestamp = "10:30 م",
                    isMine = false
                )
            ),
            "conv_2" to listOf(
                ChatMessage(
                    id = "m_201",
                    senderId = "user_darfur_pro_01",
                    senderName = "محمد نزار",
                    text = "مرحباً طارق، بخصوص روم بطولة ببجي، محتاجين رقم اللاعب الإضافي.",
                    timestamp = "08:00 م",
                    isMine = true
                ),
                ChatMessage(
                    id = "m_202",
                    senderId = "user_pubg_master",
                    senderName = "طارق قوست",
                    text = "أرسلت لك الـ ID الخاص بسكواد ببجي",
                    timestamp = "08:15 م",
                    disappearingTimer = DisappearingTimer.ONE_HOUR,
                    expiresAtTimestamp = System.currentTimeMillis() + 3600000,
                    isMine = false
                )
            ),
            "conv_grp_wolves" to listOf(
                ChatMessage(
                    id = "m_301",
                    senderId = "u_mem_2",
                    senderName = "عمر الرهيب",
                    text = "الخطة: 2 راشرين و2 سنايبر من أطراف الزون",
                    timestamp = "08:45 م",
                    isMine = false
                ),
                ChatMessage(
                    id = "m_302",
                    senderId = "user_darfur_pro_01",
                    senderName = "محمد نزار",
                    text = "تمام، أنا وماسكين الواجهة، عمر ركز على التغطية الخلفية",
                    timestamp = "08:50 م",
                    isMine = true
                ),
                ChatMessage(
                    id = "m_303",
                    senderId = "u_mem_3",
                    senderName = "خالد المدمر",
                    text = "خالد: دخلت الروم يا شباب، جاهز بالمايك",
                    timestamp = "09:00 م",
                    isMine = false
                )
            )
        )
    )

    override val conversationsFlow: Flow<List<ChatConversation>> = _conversations.asStateFlow()
    override val messagesFlow: Flow<Map<String, List<ChatMessage>>> = _messages.asStateFlow()

    override suspend fun getOrCreateConversation(
        participantId: String,
        participantName: String,
        gameType: GameType
    ): ChatConversation {
        val existing = _conversations.value.find { it.participantId == participantId }
        if (existing != null) return existing

        val newConv = ChatConversation(
            id = "conv_${UUID.randomUUID().toString().take(6)}",
            participantId = participantId,
            participantName = participantName,
            participantGame = gameType,
            lastMessage = "بدء محادثة جديدة",
            lastMessageTime = "الآن",
            unreadCount = 0
        )
        _conversations.value = listOf(newConv) + _conversations.value
        return newConv
    }

    override suspend fun sendMessage(
        conversationId: String,
        text: String,
        disappearingTimer: DisappearingTimer,
        imageUrl: String?,
        isVoiceNote: Boolean,
        voiceDurationSec: Int
    ): Result<ChatMessage> {
        val expiresAt = if (disappearingTimer.seconds > 0) {
            System.currentTimeMillis() + (disappearingTimer.seconds * 1000)
        } else null

        val isViewOnce = disappearingTimer == DisappearingTimer.VIEW_ONCE

        val newMsg = ChatMessage(
            id = "msg_${UUID.randomUUID().toString().take(8)}",
            senderId = "user_darfur_pro_01",
            senderName = "أنا",
            text = text,
            imageUrl = imageUrl,
            isVoiceNote = isVoiceNote,
            voiceDurationSec = voiceDurationSec,
            timestamp = "الآن",
            disappearingTimer = disappearingTimer,
            expiresAtTimestamp = expiresAt,
            isViewOnce = isViewOnce,
            isViewed = false,
            isMine = true
        )

        val currentMap = _messages.value.toMutableMap()
        val list = currentMap[conversationId]?.toMutableList() ?: mutableListOf()
        list.add(newMsg)
        currentMap[conversationId] = list
        _messages.value = currentMap

        // Update conversation last message
        val convList = _conversations.value.toMutableList()
        val cIdx = convList.indexOfFirst { it.id == conversationId }
        if (cIdx != -1) {
            convList[cIdx] = convList[cIdx].copy(
                lastMessage = if (isVoiceNote) "🎤 رسالة صوتية" else if (imageUrl != null) "📷 صورة" else text,
                lastMessageTime = "الآن",
                disappearingTimer = disappearingTimer
            )
            _conversations.value = convList
        }

        return Result.success(newMsg)
    }

    override suspend fun markViewOnceAsViewed(conversationId: String, messageId: String): Result<Unit> {
        val currentMap = _messages.value.toMutableMap()
        val list = currentMap[conversationId]?.map { msg ->
            if (msg.id == messageId && msg.isViewOnce) {
                msg.copy(isViewed = true, text = "تم فتح الرسالة (تلاشت)")
            } else msg
        }
        if (list != null) {
            currentMap[conversationId] = list
            _messages.value = currentMap
        }
        return Result.success(Unit)
    }

    override suspend fun purgeExpiredMessages() {
        val now = System.currentTimeMillis()
        val currentMap = _messages.value.toMutableMap()
        currentMap.forEach { (convId, list) ->
            val filtered = list.filter { msg ->
                val exp = msg.expiresAtTimestamp
                exp == null || exp > now
            }
            currentMap[convId] = filtered
        }
        _messages.value = currentMap
    }

    override suspend fun setConversationDisappearingTimer(
        conversationId: String,
        timer: DisappearingTimer
    ): Result<Unit> {
        val convList = _conversations.value.toMutableList()
        val cIdx = convList.indexOfFirst { it.id == conversationId }
        if (cIdx != -1) {
            convList[cIdx] = convList[cIdx].copy(disappearingTimer = timer)
            _conversations.value = convList
        }
        return Result.success(Unit)
    }

    override suspend fun blockConversation(conversationId: String): Result<Unit> {
        val convList = _conversations.value.toMutableList()
        val cIdx = convList.indexOfFirst { it.id == conversationId }
        if (cIdx != -1) {
            convList[cIdx] = convList[cIdx].copy(isBlocked = true)
            _conversations.value = convList
        }
        return Result.success(Unit)
    }

    override suspend fun reportConversation(conversationId: String, reason: String): Result<Unit> {
        return Result.success(Unit)
    }
}
