package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatConversation
import com.example.data.model.ChatMessage
import com.example.data.model.DisappearingTimer
import com.example.ui.components.GlassCard
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatSheet(
    conversations: List<ChatConversation>,
    activeConversation: ChatConversation?,
    messages: List<ChatMessage>,
    onSelectConversation: (ChatConversation) -> Unit,
    onSendMessage: (String, DisappearingTimer, isVoice: Boolean) -> Unit,
    onSetTimer: (DisappearingTimer) -> Unit,
    onBlockUser: (String) -> Unit,
    onReportUser: (String, String) -> Unit,
    onViewOnceClick: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var messageText by remember { mutableStateOf("") }
    var selectedTimer by remember { mutableStateOf(activeConversation?.disappearingTimer ?: DisappearingTimer.OFF) }
    var showTimerDropdown by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
    var isRecordingVoice by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = ObsidianBg,
        dragHandle = { BottomSheetDefaults.DragHandle(color = ObsidianBorder) },
        modifier = modifier.fillMaxHeight(0.92f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            if (activeConversation == null) {
                // Conversations list view
                Text(
                    text = "الرسائل الخاصة والمجموعات 💬",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "تواصل مشفر وسريع مع اللاعبين والفرق مع دعم الرسائل المؤقتة",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp)
                )
                Spacer(modifier = Modifier.height(14.dp))

                if (conversations.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("لا توجد محادثات سابقة. ابدأ محادثة من قائمة اللاعبين!", color = TextMuted, fontSize = 12.sp)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(conversations) { conv ->
                            GlassCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelectConversation(conv) },
                                backgroundColor = ObsidianCard,
                                borderColor = if (conv.unreadCount > 0) NeonCyan.copy(alpha = 0.5f) else ObsidianBorder
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(if (conv.isGroup) NeonPurple.copy(alpha = 0.2f) else NeonCyan.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (conv.isGroup) Icons.Default.Groups else Icons.Default.Person,
                                            contentDescription = null,
                                            tint = if (conv.isGroup) NeonPurple else NeonCyanLight,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = conv.participantName,
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = TextPrimary
                                                )
                                            )
                                            Text(
                                                text = conv.lastMessageTime,
                                                style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 10.sp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = conv.lastMessage,
                                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp),
                                                maxLines = 1
                                            )
                                            if (conv.disappearingTimer != DisappearingTimer.OFF) {
                                                Text(
                                                    text = "⏱️ ${conv.disappearingTimer.titleArabic}",
                                                    style = MaterialTheme.typography.labelSmall.copy(color = NeonGoldLight, fontSize = 9.sp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Active Chat Conversation Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { onSelectConversation(activeConversation) }) {
                            Icon(Icons.Default.ArrowForward, contentDescription = "رجوع", tint = TextPrimary)
                        }
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(NeonCyan.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (activeConversation.isGroup) Icons.Default.Groups else Icons.Default.Person,
                                contentDescription = null,
                                tint = NeonCyanLight,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = activeConversation.participantName,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            Text(
                                text = if (activeConversation.isOnline) "🟢 متصل الآن" else "غير متصل",
                                style = MaterialTheme.typography.labelSmall.copy(color = if (activeConversation.isOnline) NeonGreenLight else TextMuted, fontSize = 10.sp)
                            )
                        }
                    }

                    Row {
                        // Disappearing timer button
                        IconButton(onClick = { showTimerDropdown = true }) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = "توقيت الرسائل المؤقتة",
                                tint = if (selectedTimer != DisappearingTimer.OFF) NeonGoldLight else TextMuted
                            )
                        }
                        IconButton(onClick = { showReportDialog = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "خيارات", tint = TextMuted)
                        }
                    }
                }

                // Disappearing Timer Selector Row
                if (showTimerDropdown) {
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        backgroundColor = ObsidianCardElevated,
                        borderColor = NeonGold.copy(alpha = 0.5f)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "⏱️ ضبط الرسائل ذاتية الاختفاء (Disappearing Messages)",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = NeonGoldLight)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(DisappearingTimer.values().toList()) { timer ->
                                    val isSelected = selectedTimer == timer
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) NeonGold else ObsidianCard,
                                        modifier = Modifier.clickable {
                                            selectedTimer = timer
                                            onSetTimer(timer)
                                            showTimerDropdown = false
                                        }
                                    ) {
                                        Text(
                                            text = timer.titleArabic,
                                            color = if (isSelected) Color.Black else TextPrimary,
                                            fontSize = 10.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "🛡️ ملاحظة أمان: الرسائل المؤقتة تختفي تلقائياً فور انقضاء المدة. لا تمنع التصوير الخارجي أو لقطات الشاشة.",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 9.sp)
                            )
                        }
                    }
                }

                // Messages list
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(messages) { msg ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (msg.isMine) Arrangement.End else Arrangement.Start
                        ) {
                            Surface(
                                shape = RoundedCornerShape(
                                    topStart = 14.dp,
                                    topEnd = 14.dp,
                                    bottomStart = if (msg.isMine) 14.dp else 2.dp,
                                    bottomEnd = if (msg.isMine) 2.dp else 14.dp
                                ),
                                color = if (msg.isMine) NeonCyan.copy(alpha = 0.25f) else ObsidianCardElevated,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (msg.isMine) NeonCyan.copy(alpha = 0.6f) else ObsidianBorder
                                ),
                                modifier = Modifier.widthIn(max = 280.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    if (!msg.isMine && activeConversation.isGroup) {
                                        Text(
                                            text = msg.senderName,
                                            color = NeonGoldLight,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                    }

                                    if (msg.isViewOnce) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.clickable { onViewOnceClick(msg.id) }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Visibility,
                                                contentDescription = null,
                                                tint = if (msg.isViewed) TextMuted else NeonGoldLight,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (msg.isViewed) "تم عرض الرسالة (تلاشت)" else "اضغط لعرض الرسالة لمرة واحدة 👁️",
                                                color = if (msg.isViewed) TextMuted else TextPrimary,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    } else if (msg.isVoiceNote) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = NeonCyanLight)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("رسالة صوتية (${msg.voiceDurationSec} ثانية)", color = TextPrimary, fontSize = 11.sp)
                                        }
                                    } else {
                                        Text(
                                            text = msg.text,
                                            color = TextPrimary,
                                            fontSize = 12.sp,
                                            lineHeight = 16.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        if (msg.disappearingTimer != DisappearingTimer.OFF) {
                                            Text(
                                                text = "⏱️ مؤقتة (${msg.disappearingTimer.titleArabic})",
                                                color = NeonGoldLight,
                                                fontSize = 8.sp
                                            )
                                        } else {
                                            Spacer(modifier = Modifier.width(1.dp))
                                        }
                                        Text(
                                            text = msg.timestamp,
                                            color = TextMuted,
                                            fontSize = 9.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Chat Bottom Input Bar
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = ObsidianCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            onSendMessage("📷 [صورة لقطة الشاشة من المباراة]", selectedTimer, false)
                        }) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = "إرسال صورة", tint = NeonCyanLight)
                        }

                        OutlinedTextField(
                            value = messageText,
                            onValueChange = { messageText = it },
                            placeholder = {
                                Text(
                                    if (selectedTimer != DisappearingTimer.OFF) "رسالة مؤقتة (${selectedTimer.titleArabic})..." else "اكتب رسالتك هنا...",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        // Voice message simulation button
                        IconButton(onClick = {
                            isRecordingVoice = !isRecordingVoice
                            if (!isRecordingVoice) {
                                onSendMessage("🎤 تسجيل صوتي", selectedTimer, true)
                            }
                        }) {
                            Icon(
                                imageVector = if (isRecordingVoice) Icons.Default.Stop else Icons.Default.Mic,
                                contentDescription = "تسجيل صوتي",
                                tint = if (isRecordingVoice) NeonRedLight else TextMuted
                            )
                        }

                        // Send button
                        IconButton(
                            onClick = {
                                if (messageText.isNotBlank()) {
                                    onSendMessage(messageText, selectedTimer, false)
                                    messageText = ""
                                }
                            },
                            enabled = messageText.isNotBlank()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "إرسال",
                                tint = if (messageText.isNotBlank()) NeonCyanLight else TextMuted
                            )
                        }
                    }
                }
            }
        }
    }

    if (showReportDialog && activeConversation != null) {
        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            title = { Text("خيارات المحادثة والأمان", color = TextPrimary) },
            text = {
                Column {
                    TextButton(onClick = {
                        onBlockUser(activeConversation.participantId)
                        showReportDialog = false
                    }) {
                        Text("🚫 حظر هذا اللاعب", color = NeonRedLight)
                    }
                    TextButton(onClick = {
                        onReportUser(activeConversation.participantId, "سلوك مسيء أو سبام")
                        showReportDialog = false
                    }) {
                        Text("⚠️ إبلاغ الإدارة عن إساءة", color = NeonGoldLight)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showReportDialog = false }) {
                    Text("إغلاق", color = TextSecondary)
                }
            },
            containerColor = ObsidianCard
        )
    }
}
