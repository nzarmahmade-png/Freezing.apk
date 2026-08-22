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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.DetailStatBox
import com.example.ui.components.GlassCard
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
    user: User,
    onUpdateGameId: (GameType, String, String) -> Unit,
    onNavigateToRankings: () -> Unit,
    onNavigateToWallet: () -> Unit,
    modifier: Modifier = Modifier
) {
    var editingGameType by remember { mutableStateOf<GameType?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // User Profile Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ObsidianSurface)
                    .padding(16.dp)
            ) {
                Text(
                    text = "الملف الشخصي والمسيرة 👤",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = TextPrimary,
                        fontSize = 20.sp
                    )
                )
                Text(
                    text = "إدارة معرفات الألعاب وإحصائيات بطولاتك الرسمية",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 12.sp)
                )
            }
        }

        // Profile Identity Card
        item {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                backgroundColor = ObsidianCard,
                borderColor = NeonGold.copy(alpha = 0.4f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(NeonGold.copy(alpha = 0.1f), Color.Transparent)
                            )
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(NeonGold, NeonOrange)))
                                .border(2.dp, NeonGoldLight, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = user.username.take(2).uppercase(),
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = TextOnAccent
                                )
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = user.fullName,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        color = TextPrimary,
                                        fontSize = 17.sp
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(NeonGold.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "LVL ${user.level}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = NeonGoldLight,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }
                            Text(
                                text = "@${user.username} • ${user.phone}",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 12.sp)
                            )
                            Text(
                                text = "📍 ${user.location}",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 11.sp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // XP Progress
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "تقدم المستوى (${user.currentXp}/${user.nextLevelXp} XP)", color = TextSecondary, fontSize = 11.sp)
                        Text(text = user.seasonRank, color = NeonGoldLight, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { user.xpProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = NeonCyan,
                        trackColor = ObsidianBorder
                    )
                }
            }
        }

        // In-Game IDs Management Section
        item {
            SectionHeader(
                title = "معرفات الألعاب المسجلة (Game IDs) 🎮",
                subtitle = "المعرفات المعتمدة لتسجيلك التلقائي في رومات المباريات"
            )
        }

        items(listOf(GameType.FREE_FIRE, GameType.PUBG_MOBILE, GameType.EFOOTBALL)) { gameType ->
            val profile = user.gameProfiles[gameType.id]
            GameProfileItemCard(
                gameType = gameType,
                profile = profile,
                onEdit = { editingGameType = gameType }
            )
        }

        // Performance & Career Stats Section
        item {
            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader(
                title = "إحصائيات المسيرة التنافسية 📊",
                subtitle = "سجل بطولاتك الرسمية في نيالا ودارفور"
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DetailStatBox(
                    title = "البطولات الملعوبة",
                    value = "${user.stats.totalTournamentsEntered}",
                    textColor = TextPrimary,
                    modifier = Modifier.weight(1f)
                )
                DetailStatBox(
                    title = "المباريات الفائزة 🏆",
                    value = "${user.stats.matchesWon}",
                    textColor = NeonGoldLight,
                    modifier = Modifier.weight(1f)
                )
                DetailStatBox(
                    title = "نسبة الفوز",
                    value = "${user.stats.winRatePercent}%",
                    textColor = NeonGreenLight,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DetailStatBox(
                    title = "مجموع الأرباح النقدية",
                    value = "%,d ج.س".format(user.stats.totalWinningsSDG),
                    textColor = NeonGreenLight,
                    modifier = Modifier.weight(1f)
                )
                DetailStatBox(
                    title = "مرات MVP (رجل المباراة)",
                    value = "${user.stats.mvpCount} مرة ⭐",
                    textColor = NeonPurple,
                    modifier = Modifier.weight(1f)
                )
                DetailStatBox(
                    title = "عمليات القتل (Kills)",
                    value = "${user.stats.killsTotal}",
                    textColor = NeonRed,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Achievements Badges Grid
        item {
            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader(
                title = "الإنجازات والأوسمة (Achievements) 🎖️",
                subtitle = "افتح الإنجازات لربح نقاط إضافية ورفع مستواك"
            )
        }

        items(user.achievements) { ach ->
            AchievementItemCard(achievement = ach)
        }
    }

    // Edit Game ID Dialog
    if (editingGameType != null) {
        val currentProfile = user.gameProfiles[editingGameType!!.id]
        EditGameIdDialog(
            gameType = editingGameType!!,
            currentId = currentProfile?.inGameId ?: "",
            currentName = currentProfile?.inGameName ?: "",
            onDismiss = { editingGameType = null },
            onSubmit = { inGameId, inGameName ->
                val g = editingGameType!!
                editingGameType = null
                onUpdateGameId(g, inGameId, inGameName)
            }
        )
    }
}

@Composable
fun GameProfileItemCard(
    gameType: GameType,
    profile: GameIdProfile?,
    onEdit: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        backgroundColor = ObsidianCard,
        borderColor = ObsidianBorder
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(gameType.brandColor)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = gameType.titleArabic,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    if (profile != null) {
                        Text(
                            text = "معرف اللعبة: ${profile.inGameId} (${profile.inGameName})",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = NeonCyanLight,
                                fontSize = 11.sp
                            )
                        )
                    } else {
                        Text(
                            text = "لم يتم تعيين المعرف بعد",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "تعديل", tint = NeonGoldLight, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
fun AchievementItemCard(achievement: Achievement) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        backgroundColor = if (achievement.isUnlocked) ObsidianCard else ObsidianCardElevated,
        borderColor = if (achievement.isUnlocked) NeonGold.copy(alpha = 0.4f) else ObsidianBorder
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(if (achievement.isUnlocked) NeonGold.copy(alpha = 0.2f) else ObsidianBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (achievement.isUnlocked) Icons.Default.EmojiEvents else Icons.Default.Lock,
                        contentDescription = null,
                        tint = if (achievement.isUnlocked) NeonGoldLight else TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = achievement.titleArabic,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (achievement.isUnlocked) TextPrimary else TextMuted
                        )
                    )
                    Text(
                        text = "${achievement.descriptionArabic} (+${achievement.pointsReward} نقطة)",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            if (achievement.isUnlocked) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(NeonGreen.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = "مكتمل ✅", color = NeonGreenLight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                Text(
                    text = "مقفل 🔒",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun EditGameIdDialog(
    gameType: GameType,
    currentId: String,
    currentName: String,
    onDismiss: () -> Unit,
    onSubmit: (String, String) -> Unit
) {
    var inGameId by remember { mutableStateOf(currentId) }
    var inGameName by remember { mutableStateOf(currentName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ObsidianCard,
        title = {
            Text("تعديل معرف ${gameType.titleArabic}", fontWeight = FontWeight.Bold, color = TextPrimary)
        },
        text = {
            Column {
                OutlinedTextField(
                    value = inGameId,
                    onValueChange = { inGameId = it },
                    label = { Text("معرف اللاعب في اللعبة (UID / In-Game ID)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = inGameName,
                    onValueChange = { inGameName = it },
                    label = { Text("اسم الحساب داخل اللعبة (IGN)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(inGameId, inGameName) },
                enabled = inGameId.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = NeonGold, contentColor = TextOnAccent)
            ) {
                Text("حفظ التغييرات", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء", color = TextSecondary) }
        }
    )
}
