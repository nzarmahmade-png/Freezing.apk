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
import androidx.compose.ui.graphics.Brush
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
    modifier: Modifier = Modifier
) {
    val isRegistered = tournament.registeredPlayers.any { it.userId == currentUser.id }
    val userGameProfile = currentUser.gameProfiles[tournament.gameType.id]
    var showRegistrationDialog by remember { mutableStateOf(false) }
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

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${tournament.startDateArabic} • ${tournament.startTimeArabic} • ${tournament.serverRegion}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Key Stats Grid (Prize Pool, Entry Fee, Format, Seats)
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
                        textColor = if (tournament.entryFeeSDG == 0L) NeonGreenLight else NeonCyanLight,
                        modifier = Modifier.weight(1f)
                    )
                    DetailStatBox(
                        title = "نظام اللعب",
                        value = tournament.format.titleArabic.split(" ").first(),
                        textColor = TextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            // Registered Player Custom Room ID & Password Disclosure (Secret Match Keys)
            if (isRegistered && tournament.customRoomId != null) {
                item {
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        backgroundColor = ObsidianCardElevated,
                        borderColor = NeonGreen.copy(alpha = 0.5f)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.VpnKey,
                                    contentDescription = null,
                                    tint = NeonGreenLight,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "بيانات غرفة المباراة الخاصة بك 🔑",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = NeonGreenLight
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "معرف الروم (Room ID): ${tournament.customRoomId}",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = TextPrimary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                                TextButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(tournament.customRoomId))
                                    }
                                ) {
                                    Text("نسخ ID", color = NeonCyanLight, fontSize = 12.sp)
                                }
                            }
                            if (tournament.customRoomPassword != null) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "كلمة المرور (Password): ${tournament.customRoomPassword}",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = TextPrimary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                    TextButton(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(tournament.customRoomPassword))
                                        }
                                    ) {
                                        Text("نسخ الرمز", color = NeonCyanLight, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            // Tabs: 0: Details & Rules, 1: Matches & Brackets, 2: Participants
            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = ObsidianCard,
                    contentColor = NeonGold,
                    divider = {}
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("التفاصيل والقوانين", fontSize = 13.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("المباريات والنتائج", fontSize = 13.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("المشاركون (${tournament.registeredCount})", fontSize = 13.sp, fontWeight = FontWeight.Bold) }
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Tab 0: Details & Rules
            if (selectedTab == 0) {
                item {
                    Text(
                        text = "عن البطولة والمنظم",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = tournament.description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextSecondary,
                            lineHeight = 20.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    // Prize Distribution breakdown
                    Text(
                        text = "توزيع الجوائز النقدية 🏆",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = NeonGoldLight
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    PrizeRow(rank = "المركز الأول 🥇", amountSDG = tournament.firstPlacePrizeSDG, color = NeonGoldLight)
                    PrizeRow(rank = "المركز الثاني 🥈", amountSDG = tournament.secondPlacePrizeSDG, color = Color(0xFFCBD5E1))
                    PrizeRow(rank = "المركز الثالث 🥉", amountSDG = tournament.thirdPlacePrizeSDG, color = Color(0xFFD97706))

                    Spacer(modifier = Modifier.height(14.dp))

                    // Tournament Rules
                    Text(
                        text = "شروط وقوانين البطولة 📜",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                items(tournament.rules) { rule ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(top = 6.dp)
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(NeonGold)
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

            // Tab 1: Matches & Brackets
            if (selectedTab == 1) {
                if (tournament.matches.isEmpty()) {
                    item {
                        EmptyStateText("سيتم إعلان جدول المباريات وتوزيع الفرق فور اكتمال التسجيل.")
                    }
                } else {
                    items(tournament.matches) { match ->
                        MatchScheduleCard(match = match)
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
                        EmptyStateText("لا يوجد مشاركون مسجلون حتى الآن. كن أول من يحجز مقعده!")
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
                            color = NeonGoldLight,
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
                        Text("رصيدك المتاح:", color = TextSecondary, fontSize = 13.sp)
                        Text(
                            text = "%,d ج.س".format(currentUser.totalAvailableBalanceSDG),
                            fontWeight = FontWeight.Bold,
                            color = if (currentUser.totalAvailableBalanceSDG >= tournament.entryFeeSDG) NeonGreenLight else NeonRed,
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
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGold, contentColor = TextOnAccent)
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
fun MatchScheduleCard(match: TournamentMatch) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        backgroundColor = ObsidianCardElevated,
        borderColor = ObsidianBorder
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
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
                        text = "النتيجة: ${match.team1Score} - ${match.team2Score} (الفائز: ${match.winnerName})",
                        style = MaterialTheme.typography.bodySmall.copy(color = NeonGreenLight, fontWeight = FontWeight.SemiBold)
                    )
                }
            }

            Text(
                text = match.scheduledTime,
                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
            )
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
            .background(ObsidianCardElevated)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "#${standing.rank}",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (standing.rank == 1) NeonGoldLight else TextSecondary
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = standing.playerName,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            )
        }
        Text(
            text = "${standing.points} نقطة (${standing.kills} قتل)",
            style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
        )
    }
}

@Composable
fun ParticipantCard(player: RegisteredPlayer) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(ObsidianCardElevated)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(ObsidianCard),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = player.username,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
                Text(
                    text = "ID: ${player.gameId}${if (player.teamName != null) " • ${player.teamName}" else ""}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                )
            }
        }
        Text(
            text = player.registeredAt.split(" ").firstOrNull() ?: "",
            style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 10.sp)
        )
    }
}

@Composable
fun EmptyStateText(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall.copy(
                color = TextMuted,
                fontSize = 12.sp
            )
        )
    }
}
