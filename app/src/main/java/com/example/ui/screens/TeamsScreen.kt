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
import com.example.data.model.GameType
import com.example.data.model.Team
import com.example.data.model.TeamMember
import com.example.data.model.User
import com.example.ui.components.DetailStatBox
import com.example.ui.components.GlassCard
import com.example.ui.components.NeonButton
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*

@Composable
fun TeamsScreen(
    currentTeam: Team?,
    currentUser: User,
    onCreateTeam: (String, String, GameType, String) -> Unit,
    onInvitePlayer: (String) -> Unit,
    onRemoveMember: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showCreateTeamDialog by remember { mutableStateOf(false) }
    var showInviteDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
    ) {
        // Teams Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(ObsidianSurface)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "الفرق والكلانات (Teams) 🐺",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = TextPrimary,
                            fontSize = 20.sp
                        )
                    )
                    Text(
                        text = "شكل فريقك وشارك في بطولات السكواد الكبرى بدارفور",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 12.sp)
                    )
                }

                if (currentTeam == null) {
                    Button(
                        onClick = { showCreateTeamDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonGold, contentColor = TextOnAccent),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("إنشاء فريق", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        if (currentTeam == null) {
            // No Team State
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Groups,
                        contentDescription = null,
                        tint = NeonGoldLight,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "أنت لست منضماً لأي فريق حالياً",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "أنشئ فريقك الخاص وادعُ أصدقاءك في نيالا للمنافسة في بطولات السكواد وحصد الكؤوس والجوائز النقدية.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    NeonButton(
                        text = "إنشاء فريق وكلان جديد 🚀",
                        icon = Icons.Default.Add,
                        onClick = { showCreateTeamDialog = true },
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )
                }
            }
        } else {
            // Active Team View
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
                // Team Hero Card
                item {
                    TeamHeroCard(
                        team = currentTeam,
                        isLeader = currentTeam.leaderId == currentUser.id,
                        onInviteClick = { showInviteDialog = true }
                    )
                }

                // Team Stats Grid
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DetailStatBox(
                            title = "البطولات الفائزة",
                            value = "${currentTeam.tournamentsWon} 🏆",
                            textColor = NeonGoldLight,
                            modifier = Modifier.weight(1f)
                        )
                        DetailStatBox(
                            title = "المباريات الملعوبة",
                            value = "${currentTeam.matchesPlayed} مباراة",
                            textColor = NeonCyanLight,
                            modifier = Modifier.weight(1f)
                        )
                        DetailStatBox(
                            title = "نسبة الفوز",
                            value = "${currentTeam.winRate}%",
                            textColor = NeonGreenLight,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Team Roster Section
                item {
                    SectionHeader(
                        title = "أعضاء الفريق (${currentTeam.members.size} / ${currentTeam.maxMembers})",
                        subtitle = "تشكيلة السكواد الرسمية المسجلة",
                        actionText = if (currentTeam.leaderId == currentUser.id) "إضافة عضو" else null,
                        onActionClick = { showInviteDialog = true }
                    )
                }

                items(currentTeam.members) { member ->
                    TeamMemberItemCard(
                        member = member,
                        isLeaderViewer = currentTeam.leaderId == currentUser.id,
                        onRemove = { onRemoveMember(member.userId) }
                    )
                }
            }
        }
    }

    // Create Team Dialog
    if (showCreateTeamDialog) {
        CreateTeamDialog(
            onDismiss = { showCreateTeamDialog = false },
            onSubmit = { name, tag, game, bio ->
                showCreateTeamDialog = false
                onCreateTeam(name, tag, game, bio)
            }
        )
    }

    // Invite Member Dialog
    if (showInviteDialog) {
        InviteMemberDialog(
            onDismiss = { showInviteDialog = false },
            onSubmit = { username ->
                showInviteDialog = false
                onInvitePlayer(username)
            }
        )
    }
}

@Composable
fun TeamHeroCard(
    team: Team,
    isLeader: Boolean,
    onInviteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        backgroundColor = ObsidianCard,
        borderColor = NeonGold.copy(alpha = 0.5f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(NeonGold.copy(alpha = 0.12f), Color.Transparent)
                    )
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(ObsidianCardElevated)
                            .border(1.5.dp, NeonGoldLight, RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = team.logoEmoji, fontSize = 28.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = team.name,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimary
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(NeonCyan.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "[${team.tag}]",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = NeonCyanLight,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                        Text(
                            text = "${team.primaryGame.titleArabic} • ${team.region}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        )
                    }
                }

                if (isLeader) {
                    IconButton(
                        onClick = onInviteClick,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(NeonGold)
                    ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = "دعوة لاعب", tint = TextOnAccent, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = team.bio,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            )
        }
    }
}

@Composable
fun TeamMemberItemCard(
    member: TeamMember,
    isLeaderViewer: Boolean,
    onRemove: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        backgroundColor = ObsidianCardElevated,
        borderColor = ObsidianBorder
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(if (member.isLeader) NeonGold.copy(alpha = 0.2f) else ObsidianCard)
                        .border(1.dp, if (member.isLeader) NeonGoldLight else ObsidianBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (member.isLeader) Icons.Default.Stars else Icons.Default.Person,
                        contentDescription = null,
                        tint = if (member.isLeader) NeonGoldLight else TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = member.username,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        if (member.isLeader) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "👑 القائد",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = NeonGoldLight,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                    Text(
                        text = "${member.roleArabic} • ID: ${member.gameId}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            if (isLeaderViewer && !member.isLeader) {
                IconButton(onClick = onRemove) {
                    Icon(Icons.Default.RemoveCircleOutline, contentDescription = "إزالة", tint = NeonRed, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
fun CreateTeamDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, String, GameType, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var tag by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var selectedGame by remember { mutableStateOf(GameType.FREE_FIRE) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ObsidianCard,
        title = { Text("إنشاء فريق / كلان جديد", fontWeight = FontWeight.Bold, color = TextPrimary) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("اسم الفريق (مثال: ذئاب نيالا)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = tag,
                    onValueChange = { if (it.length <= 4) tag = it.uppercase() },
                    label = { Text("اختصار الفريق (مثال: NW)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("اللعبة الأساسية:", color = TextSecondary, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(GameType.FREE_FIRE, GameType.PUBG_MOBILE, GameType.EFOOTBALL).forEach { g ->
                        FilterChip(
                            selected = selectedGame == g,
                            onClick = { selectedGame = g },
                            label = { Text(g.titleArabic.split(" ").first(), fontSize = 10.sp) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("نبذة عن الفريق وشروط الانضمام") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(name, tag, selectedGame, bio) },
                enabled = name.isNotBlank() && tag.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = NeonGold, contentColor = TextOnAccent)
            ) {
                Text("إنشاء الفريق الآن", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء", color = TextSecondary) }
        }
    )
}

@Composable
fun InviteMemberDialog(
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    var usernameOrId by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ObsidianCard,
        title = { Text("دعوة لاعب للانضمام للفريق", fontWeight = FontWeight.Bold, color = TextPrimary) },
        text = {
            Column {
                Text("أدخل اسم المستخدم أو معرف اللاعب (ID):", color = TextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = usernameOrId,
                    onValueChange = { usernameOrId = it },
                    placeholder = { Text("مثال: Nyala_Ghost_SD", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(usernameOrId) },
                enabled = usernameOrId.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = NeonGold, contentColor = TextOnAccent)
            ) {
                Text("إرسال الدعوة", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء", color = TextSecondary) }
        }
    )
}
