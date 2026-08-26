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
    onChangeUsernameOnce: (String) -> Unit,
    onSubmitAdminUsernameRequest: (String, String) -> Unit,
    onNavigateToRankings: () -> Unit,
    onNavigateToWallet: () -> Unit,
    modifier: Modifier = Modifier
) {
    var editingGameType by remember { mutableStateOf<GameType?>(null) }
    var showUsernameDialog by remember { mutableStateOf(false) }
    var showAdminRequestDialog by remember { mutableStateOf(false) }
    var showFollowersDialog by remember { mutableStateOf(false) }
    var showFollowingDialog by remember { mutableStateOf(false) }

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
                    text = "إدارة بيانات اللاعب، الرتبة التنافسية ومعرفات الألعاب",
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
                borderColor = NeonCyan.copy(alpha = 0.4f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(NeonCyan.copy(alpha = 0.08f), Color.Transparent)
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
                                .background(Brush.linearGradient(listOf(NeonCyan, Color(0xFF0052FF))))
                                .border(2.dp, NeonCyanLight, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = user.username.take(2).uppercase(),
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
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

                                IconButton(
                                    onClick = {
                                        if (!user.hasChangedUsernameOnce) {
                                            showUsernameDialog = true
                                        } else {
                                            showAdminRequestDialog = true
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (!user.hasChangedUsernameOnce) Icons.Default.Edit else Icons.Default.AdminPanelSettings,
                                        contentDescription = "تعديل اسم المستخدم",
                                        tint = if (!user.hasChangedUsernameOnce) NeonCyanLight else NeonGoldLight
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "@${user.username}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = NeonCyanLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                if (user.hasChangedUsernameOnce) {
                                    Text(
                                        text = "(تم استهلاك فرصة التغيير 🔒)",
                                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 9.sp)
                                    )
                                }
                            }

                            Text(
                                text = "📍 ${user.location} • ${user.phone}",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 11.sp)
                            )
                        }
                    }

                    // Followers & Following Bar
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(ObsidianCardElevated)
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { showFollowersDialog = true }
                        ) {
                            Text(
                                text = "${user.followersCount}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                            )
                            Text(
                                text = "المتابعون (Followers)",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontSize = 10.sp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .height(24.dp)
                                .width(1.dp)
                                .background(ObsidianBorder)
                        )

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { showFollowingDialog = true }
                        ) {
                            Text(
                                text = "${user.followingCount}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                            )
                            Text(
                                text = "يتابع (Following)",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontSize = 10.sp)
                            )
                        }
                    }

                    if (user.pendingUsernameRequest != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = NeonGold.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonGold.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.HourglassTop, contentDescription = null, tint = NeonGoldLight, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "طلب تعديل اسم المستخدم لـ '${user.pendingUsernameRequest.requestedUsername}': ${user.pendingUsernameRequest.status}",
                                    style = MaterialTheme.typography.labelSmall.copy(color = TextPrimary, fontSize = 10.sp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // XP Progress
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "رصيد الخبرة (${user.currentXp}/${user.nextLevelXp} XP)", color = TextSecondary, fontSize = 11.sp)
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

        // Quick Navigation to Wallet & Rankings
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GlassCard(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onNavigateToWallet),
                    backgroundColor = ObsidianCardElevated,
                    borderColor = NeonGold.copy(alpha = 0.4f)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = NeonGoldLight)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("المحفظة المالية", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("%,d ج.س".format(user.totalAvailableBalanceSDG), color = NeonGoldLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                GlassCard(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onNavigateToRankings),
                    backgroundColor = ObsidianCardElevated,
                    borderColor = NeonCyan.copy(alpha = 0.4f)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Leaderboard, contentDescription = null, tint = NeonCyanLight)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("لوحة المتصدرين", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("الموسم الأول", color = NeonCyanLight, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // In-Game IDs Management Section
        item {
            Spacer(modifier = Modifier.height(14.dp))
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
                DetailStatBox(title = "المباريات", value = "${user.stats.matchesPlayed}", textColor = TextPrimary, modifier = Modifier.weight(1f))
                DetailStatBox(title = "الانتصارات", value = "${user.stats.matchesWon}", textColor = NeonGreenLight, modifier = Modifier.weight(1f))
                DetailStatBox(title = "نسبة الفوز", value = "%.1f%%".format(user.stats.winRatePercent), textColor = NeonCyanLight, modifier = Modifier.weight(1f))
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DetailStatBox(title = "البطولات", value = "${user.stats.totalTournamentsEntered}", textColor = TextPrimary, modifier = Modifier.weight(1f))
                DetailStatBox(title = "أفضل لاعب (MVP)", value = "${user.stats.mvpCount}", textColor = NeonGoldLight, modifier = Modifier.weight(1f))
                DetailStatBox(title = "مجموع الكيلات", value = "${user.stats.killsTotal}", textColor = NeonGoldLight, modifier = Modifier.weight(1f))
            }
        }

        // Current Team Section
        item {
            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader(
                title = "الفريق الحالي (Team) ⚡",
                subtitle = "الكلان والسكواد المنافس في بطولات الفرق"
            )

            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
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
                        Text(text = "🐺", fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = user.currentTeamName ?: "لا يوجد فريق حالياً",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                            )
                            Text(
                                text = "قائد الفريق ومؤسس السكواد",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(NeonCyan.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(text = "نشط ⚡", color = NeonCyanLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Achievements Section
        item {
            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader(
                title = "الإنجازات والأوسمة 🏅",
                subtitle = "أوسمة الشرف والتحديات التنافسية المحققة"
            )
        }

        items(user.achievements) { ach ->
            AchievementItemCard(achievement = ach)
        }
    }

    // Edit Game ID Dialog
    if (editingGameType != null) {
        val gt = editingGameType!!
        val currentProfile = user.gameProfiles[gt.id]
        EditGameIdDialog(
            gameType = gt,
            currentId = currentProfile?.inGameId ?: "",
            currentName = currentProfile?.inGameName ?: "",
            onDismiss = { editingGameType = null },
            onSubmit = { id, name ->
                onUpdateGameId(gt, id, name)
                editingGameType = null
            }
        )
    }

    // Single Username Change Dialog
    if (showUsernameDialog) {
        var newUsername by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showUsernameDialog = false },
            containerColor = ObsidianCard,
            title = {
                Text("تغيير اسم المستخدم (فرصة لمرة واحدة)", fontWeight = FontWeight.Bold, color = TextPrimary)
            },
            text = {
                Column {
                    Text(
                        text = "تنبيه: يمكنك تغيير اسم المستخدم لمرة واحدة فقط مجاناً. أي تغيير إضافي مستقبلاً سيتطلب موافقة إدارة المنصة.",
                        style = MaterialTheme.typography.bodySmall.copy(color = NeonGoldLight, fontSize = 11.sp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newUsername,
                        onValueChange = { newUsername = it },
                        label = { Text("اسم المستخدم الجديد") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onChangeUsernameOnce(newUsername)
                        showUsernameDialog = false
                    },
                    enabled = newUsername.isNotBlank() && newUsername.length >= 3,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black)
                ) {
                    Text("تأكيد التغيير", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showUsernameDialog = false }) { Text("إلغاء", color = TextSecondary) }
            }
        )
    }

    // Admin Username Request Dialog
    if (showAdminRequestDialog) {
        var desiredUsername by remember { mutableStateOf("") }
        var reason by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAdminRequestDialog = false },
            containerColor = ObsidianCard,
            title = {
                Text("طلب تغيير اسم المستخدم من الإدارة 🛡️", fontWeight = FontWeight.Bold, color = TextPrimary)
            },
            text = {
                Column {
                    Text(
                        text = "لقد قمت بتغيير اسم المستخدم لمرة واحدة مسبقاً. لطلب تعديل إضافي، يرجى ملء النموذج للمراجعة من قِبل إدارة عازم.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = desiredUsername,
                        onValueChange = { desiredUsername = it },
                        label = { Text("اسم المستخدم المطلوب") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = reason,
                        onValueChange = { reason = it },
                        label = { Text("سبب طلب التعديل") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSubmitAdminUsernameRequest(desiredUsername, reason)
                        showAdminRequestDialog = false
                    },
                    enabled = desiredUsername.isNotBlank() && reason.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGold, contentColor = Color.Black)
                ) {
                    Text("إرسال الطلب للإدارة", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAdminRequestDialog = false }) { Text("إلغاء", color = TextSecondary) }
            }
        )
    }

    // Followers List Dialog
    if (showFollowersDialog) {
        AlertDialog(
            onDismissRequest = { showFollowersDialog = false },
            containerColor = ObsidianCard,
            title = { Text("المتابعون (${user.followersCount})", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(modifier = Modifier.heightIn(max = 250.dp)) {
                    items(user.followersList) { follower ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(NeonCyan.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(follower.take(1), color = NeonCyanLight, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(follower, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showFollowersDialog = false }) { Text("إغلاق", color = TextSecondary) }
            }
        )
    }

    // Following List Dialog
    if (showFollowingDialog) {
        AlertDialog(
            onDismissRequest = { showFollowingDialog = false },
            containerColor = ObsidianCard,
            title = { Text("يتابع (${user.followingCount})", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(modifier = Modifier.heightIn(max = 250.dp)) {
                    items(user.followingList) { following ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(NeonGold.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(following.take(1), color = NeonGoldLight, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(following, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showFollowingDialog = false }) { Text("إغلاق", color = TextSecondary) }
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
    val (iconColor, brandColor) = when (gameType) {
        GameType.FREE_FIRE -> Pair(FreeFireColor, FreeFireColor)
        GameType.PUBG_MOBILE -> Pair(PubgColor, PubgColor)
        GameType.EFOOTBALL -> Pair(EFootballColor, EFootballColor)
        else -> Pair(NeonCyan, NeonCyan)
    }

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp),
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
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(brandColor.copy(alpha = 0.15f))
                        .border(1.dp, brandColor.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (gameType) {
                            GameType.EFOOTBALL -> Icons.Default.SportsSoccer
                            else -> Icons.Default.SportsEsports
                        },
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = gameType.titleArabic,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )

                    if (profile != null && profile.inGameId.isNotBlank()) {
                        Text(
                            text = "UID: ${profile.inGameId} • ${profile.inGameName}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = NeonCyanLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Text(
                            text = "الرتبة: ${profile.rankTier}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextMuted,
                                fontSize = 10.sp
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

            IconButton(
                onClick = onEdit,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(ObsidianCardElevated)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "تعديل",
                    tint = NeonCyanLight,
                    modifier = Modifier.size(18.dp)
                )
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
