package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TournamentDetailSheet(
    tournament: Tournament,
    currentUser: User,
    onDismiss: () -> Unit,
    onRegister: (Tournament, Boolean) -> Unit,
    onSubmitMatchResult: (tournamentId: String, matchId: String, team1Score: Int, team2Score: Int, proofUrl: String?) -> Unit = { _, _, _, _, _ -> },
    onDisputeMatch: (tournamentId: String, matchId: String, reason: String) -> Unit = { _, _, _ -> },
    modifier: Modifier = Modifier
) {
    val isRegistered = tournament.registeredPlayers.any { it.userId == currentUser.id }
    val userGameProfile = currentUser.gameProfiles[tournament.gameType.id]
    var showRegistrationDialog by remember { mutableStateOf(false) }
    var selectedMatchForSubmission by remember { mutableStateOf<TournamentMatch?>(null) }
    var selectedMatchForDispute by remember { mutableStateOf<TournamentMatch?>(null) }
    var selectedTab by remember { mutableStateOf(0) } // 0: Details & Rules, 1: Matches & Results, 2: Participants
    val clipboardManager = LocalClipboardManager.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = ObsidianSurface,
        dragHandle = { BottomSheetDefaults.DragHandle(color = ObsidianBorder) },
        modifier = modifier
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Header: Status, Game, Title
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusTag(status = tournament.status)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(tournament.gameType.brandColor)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = tournament.gameType.titleArabic,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = tournament.gameType.brandColor,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = tournament.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = TextPrimary,
                        fontSize = 20.sp
                    )
                )

                Text(
                    text = "تنظيم: ${tournament.organizer} • سيرفر: ${tournament.serverRegion}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))
            }

            // Key Stats Grid
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DetailStatBox(
                        title = "مجموع الجوائز",
                        value = "%,d ج.س".format(tournament.totalPrizePoolSDG),
                        textColor = NeonGoldLight,
                        modifier = Modifier.weight(1f)
                    )
                    DetailStatBox(
                        title = "رسوم الدخول",
                        value = if (tournament.entryFeeSDG == 0L) "مجانية" else "%,d ج.س".format(tournament.entryFeeSDG),
                        textColor = NeonCyanLight,
                        modifier = Modifier.weight(1f)
                    )
                    DetailStatBox(
                        title = "المقاعد",
                        value = "${tournament.registeredCount}/${tournament.maxParticipants}",
                        textColor = if (tournament.isFull) NeonRed else NeonGreenLight,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Schedule & Format Badges
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    InfoBadge(icon = Icons.Default.CalendarMonth, text = "${tournament.startDateArabic} - ${tournament.startTimeArabic}", modifier = Modifier.weight(1.2f))
                    InfoBadge(icon = Icons.Default.Groups, text = tournament.format.titleArabic, modifier = Modifier.weight(0.8f))
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Tabs Selector
            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = ObsidianCard,
                    contentColor = NeonCyan,
                    divider = {}
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("التفاصيل والجوائز", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("المباريات والنتائج", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("المسجلون (${tournament.registeredCount})", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Tab 0: Details, Prizes, Room Access
            if (selectedTab == 0) {
                // Room / Lobby Credentials (If Registered and Available)
                if (isRegistered && tournament.customRoomId != null && tournament.status == TournamentStatus.LIVE) {
                    item {
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            backgroundColor = ObsidianCardElevated,
                            borderColor = NeonGreen
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "معلومات الروم والمباراة الحية 🔴",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = NeonGreenLight
                                        )
                                    )
                                    Text(
                                        text = "خاص بالمشتركين",
                                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = "رقم الغرفة (Room ID):", color = TextSecondary, fontSize = 11.sp)
                                        Text(
                                            text = tournament.customRoomId,
                                            color = TextPrimary,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 16.sp
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(tournament.customRoomId))
                                        }
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "نسخ", tint = NeonGreenLight)
                                    }
                                }

                                tournament.customRoomPassword?.let { pwd ->
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(text = "كلمة المرور (Password):", color = TextSecondary, fontSize = 11.sp)
                                            Text(
                                                text = pwd,
                                                color = NeonGoldLight,
                                                fontWeight = FontWeight.Black,
                                                fontSize = 16.sp
                                            )
                                        }
                                        IconButton(
                                            onClick = {
                                                clipboardManager.setText(AnnotatedString(pwd))
                                            }
                                        ) {
                                            Icon(Icons.Default.ContentCopy, contentDescription = "نسخ", tint = NeonGoldLight)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "تفتح الروم قبل 15 دقيقة من انطلاق المباراة. الالتزام باللعب النظيف إجباري.",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 10.sp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }
                }

                // Description
                item {
                    Text(
                        text = "عن البطولة 📝",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = tournament.description,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            lineHeight = 18.sp,
                            fontSize = 13.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Prize Breakdown
                item {
                    Text(
                        text = "توزيع الجوائز المالية 🏆",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    PrizeRow(rank = "المركز الأول 🥇", amountSDG = tournament.firstPlacePrizeSDG, color = NeonGoldLight)
                    if (tournament.secondPlacePrizeSDG > 0) {
                        PrizeRow(rank = "المركز الثاني 🥈", amountSDG = tournament.secondPlacePrizeSDG, color = Color(0xFFC0C0C0))
                    }
                    if (tournament.thirdPlacePrizeSDG > 0) {
                        PrizeRow(rank = "المركز الثالث 🥉", amountSDG = tournament.thirdPlacePrizeSDG, color = Color(0xFFCD7F32))
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Tournament Rules
                item {
                    Text(
                        text = "شروط وقوانين البطولة 📜",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(tournament.rules) { rule ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(top = 6.dp)
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(NeonCyan)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = rule,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 18.sp
                            )
                        )
                    }
                }
            }

            // Tab 1: Matches, Results Submission & Disputes
            if (selectedTab == 1) {
                if (tournament.matches.isEmpty()) {
                    item {
                        Text(
                            text = "سيتم إعلان جدول المباريات وتوزيع الفرق فور اكتمال التسجيل.",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted),
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                } else {
                    items(tournament.matches) { match ->
                        MatchScheduleCard(
                            match = match,
                            onSubmitResultClick = { selectedMatchForSubmission = match },
                            onDisputeClick = { selectedMatchForDispute = match }
                        )
                    }
                }

                if (tournament.standings.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "ترتيب ونتائج البطولة 📊",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    items(tournament.standings) { standing ->
                        StandingRow(standing = standing)
                    }
                }
            }

            // Tab 2: Participants
            if (selectedTab == 2) {
                item {
                    Text(
                        text = "اللاعبون والفرق المسجلة (${tournament.registeredCount} / ${tournament.maxParticipants})",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (tournament.registeredPlayers.isEmpty()) {
                    item {
                        Text(
                            text = "لا يوجد مشاركون مسجلون حتى الآن. كن أول من يحجز مقعده!",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted),
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                } else {
                    items(tournament.registeredPlayers) { player ->
                        ParticipantCard(player = player)
                    }
                }
            }

            // Action Button: Register or Registered Status
            item {
                Spacer(modifier = Modifier.height(20.dp))
                if (isRegistered) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp)),
                        color = NeonGreen.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonGreen)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NeonGreenLight)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "أنت مسجل بالفعل في هذه البطولة ✅",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = NeonGreenLight
                                )
                            )
                        }
                    }
                } else if (tournament.isFull) {
                    Button(
                        onClick = {},
                        enabled = false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("اكتملت المقاعد", fontWeight = FontWeight.Bold)
                    }
                } else {
                    NeonButton(
                        text = if (tournament.entryFeeSDG == 0L) "تسجيل مجاني الآن" else "تسجيل واشتراك (%,d ج.س)".format(tournament.entryFeeSDG),
                        icon = Icons.Default.HowToReg,
                        onClick = { showRegistrationDialog = true }
                    )
                }
            }
        }
    }

    // Match Result Submission Dialog
    if (selectedMatchForSubmission != null) {
        val match = selectedMatchForSubmission!!
        var s1 by remember { mutableStateOf(match.team1Score?.toString() ?: "0") }
        var s2 by remember { mutableStateOf(match.team2Score?.toString() ?: "0") }
        var proofUrl by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { selectedMatchForSubmission = null },
            containerColor = ObsidianCard,
            title = {
                Text("رفع نتيجة المباراة ولقطة الشاشة 📋", fontWeight = FontWeight.Bold, color = TextPrimary)
            },
            text = {
                Column {
                    Text(
                        text = "${match.team1Name} ضد ${match.team2Name}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = NeonCyanLight)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = s1,
                            onValueChange = { s1 = it },
                            label = { Text("سكور ${match.team1Name}", fontSize = 10.sp) },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = s2,
                            onValueChange = { s2 = it },
                            label = { Text("سكور ${match.team2Name}", fontSize = 10.sp) },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = proofUrl,
                        onValueChange = { proofUrl = it },
                        label = { Text("رابط لقطة الشاشة (Screenshot Proof)") },
                        placeholder = { Text("https://...", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "📌 سيتم التحقق من لقطة الشاشة من قِبل الحكام قبل اعتماد الفائز رسمياً.",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 9.sp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val score1 = s1.toIntOrNull() ?: 0
                        val score2 = s2.toIntOrNull() ?: 0
                        onSubmitMatchResult(tournament.id, match.id, score1, score2, proofUrl.ifBlank { "https://azom.game/proofs/${match.id}.jpg" })
                        selectedMatchForSubmission = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black)
                ) {
                    Text("إرسال النتيجة للمراجعة", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedMatchForSubmission = null }) { Text("إلغاء", color = TextSecondary) }
            }
        )
    }

    // Match Dispute Dialog
    if (selectedMatchForDispute != null) {
        val match = selectedMatchForDispute!!
        var disputeReason by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { selectedMatchForDispute = null },
            containerColor = ObsidianCard,
            title = {
                Text("تقديم اعتراض تحكيمي رسمي ⚠️", fontWeight = FontWeight.Bold, color = NeonRedLight)
            },
            text = {
                Column {
                    Text(
                        text = "المباراة: ${match.team1Name} vs ${match.team2Name}",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = disputeReason,
                        onValueChange = { disputeReason = it },
                        label = { Text("سبب الاعتراض (غش، استخدام برامج، تغيب الخصم...)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDisputeMatch(tournament.id, match.id, disputeReason)
                        selectedMatchForDispute = null
                    },
                    enabled = disputeReason.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonRed, contentColor = Color.White)
                ) {
                    Text("رفع الاعتراض للحكام", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedMatchForDispute = null }) { Text("إلغاء", color = TextSecondary) }
            }
        )
    }

    // Interactive Registration Confirmation Dialog
    if (showRegistrationDialog) {
        AlertDialog(
            onDismissRequest = { showRegistrationDialog = false },
            containerColor = ObsidianCard,
            title = {
                Text(
                    text = "تأكيد الاشتراك في البطولة",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
            },
            text = {
                Column {
                    Text(
                        text = "البطولة: ${tournament.title}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = NeonCyanLight,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "معرفك في اللعبة: ${userGameProfile?.inGameId ?: "غير محدد"}",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                    Text(
                        text = "اسمك داخل اللعبة: ${userGameProfile?.inGameName ?: currentUser.username}",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(color = ObsidianBorder)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("رسوم الدخول:", color = TextSecondary, fontSize = 13.sp)
                        Text(
                            text = if (tournament.entryFeeSDG == 0L) "مجانية" else "%,d ج.س".format(tournament.entryFeeSDG),
                            fontWeight = FontWeight.Bold,
                            color = NeonCyanLight,
                            fontSize = 13.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("رصيد الإيداع المتاح:", color = TextSecondary, fontSize = 13.sp)
                        Text(
                            text = "%,d ج.س".format(currentUser.depositBalanceSDG),
                            fontWeight = FontWeight.Bold,
                            color = if (currentUser.depositBalanceSDG >= tournament.entryFeeSDG) NeonGreenLight else NeonRed,
                            fontSize = 13.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showRegistrationDialog = false
                        onRegister(tournament, false)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black)
                ) {
                    Text("تأكيد ودفع الرسوم", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRegistrationDialog = false }) {
                    Text("إلغاء", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
fun DetailStatBox(
    title: String,
    value: String,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(ObsidianCardElevated)
            .border(0.5.dp, ObsidianBorder, RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextSecondary,
                    fontSize = 10.sp
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    fontSize = 13.sp
                )
            )
        }
    }
}

@Composable
fun PrizeRow(rank: String, amountSDG: Long, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(ObsidianCardElevated)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = rank, style = MaterialTheme.typography.bodyMedium.copy(color = color, fontWeight = FontWeight.Bold))
        Text(text = "%,d ج.س".format(amountSDG), style = MaterialTheme.typography.bodyMedium.copy(color = color, fontWeight = FontWeight.ExtraBold))
    }
}

@Composable
fun MatchScheduleCard(
    match: TournamentMatch,
    onSubmitResultClick: () -> Unit = {},
    onDisputeClick: () -> Unit = {}
) {
    val isDisputed = match.submissionStatus == MatchSubmissionStatus.DISPUTED
    val isPendingReview = match.submissionStatus == MatchSubmissionStatus.SUBMITTED_PENDING_REVIEW

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        backgroundColor = ObsidianCardElevated,
        borderColor = if (isDisputed) NeonRed.copy(alpha = 0.6f) else if (isPendingReview) NeonGold.copy(alpha = 0.6f) else ObsidianBorder
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = match.roundName,
                        style = MaterialTheme.typography.labelSmall.copy(color = NeonCyanLight, fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${match.team1Name} vs ${match.team2Name}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                    )
                    if (match.isCompleted && match.team1Score != null && match.team2Score != null) {
                        Text(
                            text = "النتيجة: ${match.team1Score} - ${match.team2Score} (الفائز: ${match.winnerName ?: match.team1Name})",
                            style = MaterialTheme.typography.bodySmall.copy(color = NeonGreenLight, fontWeight = FontWeight.SemiBold)
                        )
                    }
                }

                Text(
                    text = match.scheduledTime,
                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status tag
                when (match.submissionStatus) {
                    MatchSubmissionStatus.DISPUTED -> {
                        Text(text = "⚠️ النتيجة معترض عليها وقيد التحكيم", color = NeonRedLight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    MatchSubmissionStatus.SUBMITTED_PENDING_REVIEW -> {
                        Text(text = "⏳ النتيجة مرفوعة وقيد مراجعة الحكام", color = NeonGoldLight, fontSize = 10.sp)
                    }
                    MatchSubmissionStatus.APPROVED -> {
                        Text(text = "✅ نتيجة معتمدة رسمياً", color = NeonGreenLight, fontSize = 10.sp)
                    }
                    else -> {
                        Text(text = "🎮 مجدولة للبدء", color = TextMuted, fontSize = 10.sp)
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (!match.isCompleted || match.submissionStatus == MatchSubmissionStatus.SCHEDULED) {
                        TextButton(
                            onClick = onSubmitResultClick,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("رفع النتيجة 📋", color = NeonCyanLight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    TextButton(
                        onClick = onDisputeClick,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("اعتراض ⚠️", color = NeonRedLight, fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun StandingRow(standing: TournamentStanding) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(ObsidianCard)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (standing.rank == 1) NeonGold.copy(alpha = 0.2f) else ObsidianBg),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${standing.rank}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (standing.rank == 1) NeonGoldLight else TextSecondary,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = standing.playerName,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
                standing.teamName?.let {
                    Text(text = "[$it]", color = TextMuted, fontSize = 10.sp)
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(text = "${standing.kills} قتلات", color = TextMuted, fontSize = 11.sp)
            Text(text = "${standing.points} نقطة", color = NeonCyanLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            if (standing.prizeSDG > 0) {
                Text(text = "%,d ج.س".format(standing.prizeSDG), color = NeonGoldLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ParticipantCard(player: RegisteredPlayer) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(ObsidianCard)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(NeonCyan.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = player.username.take(1),
                    color = NeonCyanLight,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = player.username,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
                Text(
                    text = "UID: ${player.gameId}${if (player.teamName != null) " • [${player.teamName}]" else ""}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                )
            }
        }

        Text(
            text = player.registeredAt,
            style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 10.sp)
        )
    }
}

@Composable
fun InfoBadge(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.clip(RoundedCornerShape(8.dp)),
        color = ObsidianCardElevated,
        border = androidx.compose.foundation.BorderStroke(0.5.dp, ObsidianBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = NeonCyanLight, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                ),
                maxLines = 1
            )
        }
    }
}
