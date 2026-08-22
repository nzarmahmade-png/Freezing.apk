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
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
fun RankingsScreen(
    season: Season,
    leaderboard: List<LeaderboardEntry>,
    selectedScope: RankingScope,
    onScopeChange: (RankingScope) -> Unit,
    onClaimReward: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Rankings Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ObsidianSurface)
                    .padding(16.dp)
            ) {
                Text(
                    text = "التصنيفات والمواسم التنافسية 🏆",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = TextPrimary,
                        fontSize = 20.sp
                    )
                )
                Text(
                    text = "لوحة الشرف لأفضل لاعبي نيالا ودارفور وجوائز الموسم المعتمدة",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 12.sp)
                )
            }
        }

        // Season Hero Card
        item {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                backgroundColor = ObsidianCard,
                borderColor = NeonPurple.copy(alpha = 0.5f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(NeonPurple.copy(alpha = 0.15f), Color.Transparent)
                            )
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = season.seasonTitleArabic,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimary,
                                    fontSize = 17.sp
                                )
                            )
                            Text(
                                text = "${season.seasonThemeArabic} • متبقي ${season.daysRemaining} يوم",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFFC084FC),
                                    fontSize = 11.sp
                                )
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = NeonGoldLight,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DetailStatBox(
                            title = "مجموع الجوائز",
                            value = "%,d ج.س".format(season.totalPrizePoolSDG),
                            textColor = NeonGoldLight,
                            modifier = Modifier.weight(1f)
                        )
                        DetailStatBox(
                            title = "المشاركون بالموسم",
                            value = "2,400+ لاعب",
                            textColor = NeonCyanLight,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Season Tier Rewards Carousel
        item {
            SectionHeader(
                title = "جوائز مستويات الموسم 🎁",
                subtitle = "احصد النقاط والجوائز النقدية عند فتح كل مستوى"
            )

            LazyRow(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(season.rewards) { reward ->
                    SeasonRewardCard(
                        reward = reward,
                        onClaim = { onClaimReward(reward.tierNameArabic) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Leaderboard Scope Filter Chips
        item {
            SectionHeader(
                title = "لوحة المتصدرين (Leaderboard)",
                subtitle = "ترتيب الأبطال وفق النقاط التنافسية"
            )

            LazyRow(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(RankingScope.values()) { scope ->
                    RankingScopeChip(
                        scope = scope,
                        isSelected = selectedScope == scope,
                        onClick = { onScopeChange(scope) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Leaderboard Entries
        items(leaderboard) { entry ->
            LeaderboardEntryCard(entry = entry)
        }
    }
}

@Composable
fun SeasonRewardCard(
    reward: SeasonTierReward,
    onClaim: () -> Unit
) {
    GlassCard(
        modifier = Modifier.width(160.dp),
        backgroundColor = if (reward.isUnlocked) ObsidianCard else ObsidianCardElevated,
        borderColor = if (reward.isUnlocked) NeonGold.copy(alpha = 0.5f) else ObsidianBorder
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (reward.isUnlocked) NeonGold.copy(alpha = 0.2f) else ObsidianBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (reward.isUnlocked) Icons.Default.CardGiftcard else Icons.Default.Lock,
                    contentDescription = null,
                    tint = if (reward.isUnlocked) NeonGoldLight else TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = reward.tierNameArabic,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
            Text(
                text = "${reward.requiredXp} XP",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextSecondary,
                    fontSize = 10.sp
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "+${reward.pointsReward} نقطة",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = NeonGoldLight,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            )
            if (reward.cashRewardSDG > 0) {
                Text(
                    text = "+%,d ج.س كاش".format(reward.cashRewardSDG),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = NeonGreenLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            if (reward.isClaimed) {
                Text("تم الاستلام ✅", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            } else if (reward.isUnlocked) {
                Button(
                    onClick = onClaim,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGold, contentColor = TextOnAccent),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Text("استلام", fontWeight = FontWeight.Bold, fontSize = 10.sp)
                }
            } else {
                Text("مقفل 🔒", color = TextMuted, fontSize = 10.sp)
            }
        }
    }
}

@Composable
fun RankingScopeChip(
    scope: RankingScope,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        color = if (isSelected) NeonGold.copy(alpha = 0.2f) else ObsidianCard,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) NeonGoldLight else ObsidianBorder
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = scope.titleArabic,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) NeonGoldLight else TextSecondary,
                fontSize = 11.sp
            ),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun LeaderboardEntryCard(entry: LeaderboardEntry) {
    val rankColor = when (entry.rank) {
        1 -> NeonGoldLight
        2 -> Color(0xFFCBD5E1)
        3 -> Color(0xFFD97706)
        else -> TextSecondary
    }

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        backgroundColor = ObsidianCard,
        borderColor = if (entry.rank <= 3) rankColor.copy(alpha = 0.4f) else ObsidianBorder
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
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(if (entry.rank <= 3) rankColor.copy(alpha = 0.2f) else ObsidianCardElevated),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "#${entry.rank}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = rankColor,
                            fontSize = 12.sp
                        )
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = entry.username,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        if (entry.rank == 1) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("👑", fontSize = 12.sp)
                        }
                    }
                    Text(
                        text = "${entry.location} • ${entry.tierTitle} • فوز ${entry.matchesWon} مباراة",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp)
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "%,d نقطة".format(entry.scorePoints),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = NeonGoldLight,
                        fontSize = 12.sp
                    )
                )
                Text(
                    text = "نسبة فوز ${entry.winRate}%",
                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontSize = 10.sp)
                )
            }
        }
    }
}
