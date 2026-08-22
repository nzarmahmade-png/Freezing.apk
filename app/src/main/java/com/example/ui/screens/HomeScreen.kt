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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    user: User,
    tournaments: List<Tournament>,
    games: List<Game>,
    season: Season,
    earnOpportunities: List<EarnOpportunity>,
    onTournamentClick: (Tournament) -> Unit,
    onGameClick: (Game) -> Unit,
    onDailyRewardClick: (EarnOpportunity) -> Unit,
    onWatchAdClick: (EarnOpportunity) -> Unit,
    onNavigateToTournaments: () -> Unit,
    onNavigateToWallet: () -> Unit,
    onNavigateToRewards: () -> Unit,
    onNavigateToRankings: () -> Unit,
    onNavigateToTeams: () -> Unit,
    modifier: Modifier = Modifier
) {
    val featuredTournament = tournaments.find { it.isFeatured } ?: tournaments.firstOrNull()
    val activeTournaments = tournaments.filter { it.status == TournamentStatus.LIVE || it.status == TournamentStatus.REGISTRATION_OPEN }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // User Level & XP Banner
        item {
            UserXpCard(
                user = user,
                onWalletClick = onNavigateToWallet,
                onRankingsClick = onNavigateToRankings
            )
        }

        // Daily Quick Earn Opportunities Row
        item {
            DailyEarnBar(
                opportunities = earnOpportunities,
                onClaim = onDailyRewardClick,
                onWatchAd = onWatchAdClick
            )
        }

        // Supported Games Carousel
        item {
            SectionHeader(
                title = "الألعاب المدعومة",
                subtitle = "اختر لعبتك للمشاركة في بطولاتها المخصصة",
                actionText = "عرض الكل",
                onActionClick = onNavigateToTournaments
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(games) { game ->
                    GameQuickCard(
                        game = game,
                        onClick = { onGameClick(game) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Featured Tournament Hero Banner
        if (featuredTournament != null) {
            item {
                SectionHeader(
                    title = "البطولة المميزة اليوم 🔥",
                    subtitle = "أكبر مجموع جوائز في نيالا ودارفور"
                )
                FeaturedTournamentCard(
                    tournament = featuredTournament,
                    onClick = { onTournamentClick(featuredTournament) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Season 1 Darfur Champions Banner
        item {
            SeasonHighlightCard(
                season = season,
                onClick = onNavigateToRankings
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Active & Open Tournaments List
        item {
            SectionHeader(
                title = "بطولات التسجيل المفتوح والجارية",
                subtitle = "سجل الآن قبل اكتمال المقاعد",
                actionText = "كل البطولات (${tournaments.size})",
                onActionClick = onNavigateToTournaments
            )
        }

        items(activeTournaments) { tourney ->
            TournamentListItemCard(
                tournament = tourney,
                onClick = { onTournamentClick(tourney) }
            )
        }

        // Gaming News & Darfur Announcements
        item {
            Spacer(modifier = Modifier.height(8.dp))
            SectionHeader(
                title = "أخبار ومستجدات المنصة",
                subtitle = "آخر تحديثات مجتمع الألعاب في السودان ودارفور"
            )
            NewsAndAnnouncementCard(
                title = "افتتاح مقر سيرفرات بطولات نيالا 🎮",
                date = "22 أغسطس 2026",
                content = "تم تدشين رومات مخصصة ذات بنق منخفض (Low Ping) لضمان أعلى أداء للاعبي فري فاير وببجي في دارفور."
            )
            NewsAndAnnouncementCard(
                title = "شراكة متجر عازم وشحن الألعاب الفوري 💎",
                date = "20 أغسطس 2026",
                content = "يمكن الآن استبدال نقاط المكافآت بجواهر فري فاير وشدات ببجي وكوينز بيس بشكل فوري ومباشر."
            )
        }
    }
}

@Composable
fun UserXpCard(
    user: User,
    onWalletClick: () -> Unit,
    onRankingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        backgroundColor = ObsidianCard,
        borderColor = NeonGold.copy(alpha = 0.3f),
        elevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.radialGradient(
                        colors = listOf(NeonGold.copy(alpha = 0.08f), Color.Transparent),
                        radius = 400f
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
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(NeonGold, NeonOrange)))
                            .border(2.dp, NeonGoldLight, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user.username.take(2).uppercase(),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextOnAccent
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = user.fullName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
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
                            text = "${user.seasonRank} • ${user.location}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        )
                    }
                }

                // Season Trophy Button
                IconButton(
                    onClick = onRankingsClick,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(ObsidianCardElevated)
                        .border(1.dp, ObsidianBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "التصنيفات",
                        tint = NeonGoldLight,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // XP Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "نقاط الخبرة (XP)",
                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                )
                Text(
                    text = "${user.currentXp} / ${user.nextLevelXp} XP",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = NeonCyanLight,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { user.xpProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = NeonCyan,
                trackColor = ObsidianBorder
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Balances Summary Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Deposit Balance
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(ObsidianCardElevated)
                        .border(0.5.dp, ObsidianBorder, RoundedCornerShape(10.dp))
                        .clickable(onClick = onWalletClick)
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = "رصيد الإيداع",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "%,d ج.س".format(user.depositBalanceSDG),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontSize = 13.sp
                            )
                        )
                    }
                }

                // Winnings Balance
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(ObsidianCardElevated)
                        .border(0.5.dp, NeonGreen.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .clickable(onClick = onWalletClick)
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = "أرباح قابلة للسحب 💰",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = NeonGreenLight,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "%,d ج.س".format(user.tournamentWinningsSDG),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = NeonGreenLight,
                                fontSize = 13.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DailyEarnBar(
    opportunities: List<EarnOpportunity>,
    onClaim: (EarnOpportunity) -> Unit,
    onWatchAd: (EarnOpportunity) -> Unit,
    modifier: Modifier = Modifier
) {
    val dailyLogin = opportunities.find { it.actionType == EarnActionType.DAILY_LOGIN }
    val watchAd = opportunities.find { it.actionType == EarnActionType.WATCH_REWARD_AD }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (dailyLogin != null) {
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(enabled = !dailyLogin.isClaimedToday) { onClaim(dailyLogin) },
                color = if (dailyLogin.isClaimedToday) ObsidianCard else ObsidianCardElevated,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (dailyLogin.isClaimedToday) ObsidianBorder else NeonGold.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (dailyLogin.isClaimedToday) Icons.Default.CheckCircle else Icons.Default.CardGiftcard,
                        contentDescription = null,
                        tint = if (dailyLogin.isClaimedToday) NeonGreenLight else NeonGoldLight,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = if (dailyLogin.isClaimedToday) "تم استلام اليوم ✅" else "مكافأة الدخول 🎁",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (dailyLogin.isClaimedToday) TextSecondary else TextPrimary,
                                fontSize = 11.sp
                            )
                        )
                        Text(
                            text = "+${dailyLogin.pointsReward} نقطة",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (dailyLogin.isClaimedToday) TextMuted else NeonGoldLight,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }
        }

        if (watchAd != null) {
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onWatchAd(watchAd) },
                color = ObsidianCardElevated,
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayCircle,
                        contentDescription = null,
                        tint = NeonCyanLight,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "شاهد إعلاناً واكسب 🎬",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontSize = 11.sp
                            )
                        )
                        Text(
                            text = "+${watchAd.pointsReward} نقطة و XP",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = NeonCyanLight,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GameQuickCard(
    game: Game,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier
            .width(180.dp)
            .clickable(onClick = onClick),
        backgroundColor = ObsidianCard,
        borderColor = game.type.brandColor.copy(alpha = 0.4f),
        cornerRadius = 14.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(game.type.brandColor)
                )
                Text(
                    text = "${game.activeTournamentsCount} بطولات",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = game.type.brandColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = game.nameArabic,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${game.activePlayersCount} لاعب نشط في دارفور",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondary,
                    fontSize = 10.sp
                )
            )
        }
    }
}

@Composable
fun FeaturedTournamentCard(
    tournament: Tournament,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClick),
        backgroundColor = ObsidianCard,
        borderColor = NeonGold,
        elevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            tournament.gameType.brandColor.copy(alpha = 0.15f),
                            ObsidianCard
                        )
                    )
                )
                .padding(16.dp)
        ) {
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
                    Spacer(modifier = Modifier.width(4.dp))
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
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary,
                    fontSize = 17.sp
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "${tournament.startDateArabic} • ${tournament.startTimeArabic} • نظام ${tournament.format.titleArabic}",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Prize & Fee Badges
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(ObsidianBg.copy(alpha = 0.7f))
                    .border(0.5.dp, ObsidianBorder, RoundedCornerShape(10.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "مجموع الجوائز",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                    )
                    Text(
                        text = "%,d ج.س".format(tournament.totalPrizePoolSDG),
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Black,
                            color = NeonGoldLight,
                            fontSize = 15.sp
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .height(30.dp)
                        .width(1.dp)
                        .background(ObsidianBorder)
                )

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "رسوم الدخول",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                    )
                    Text(
                        text = if (tournament.entryFeeSDG == 0L) "مجانية" else "%,d ج.س".format(tournament.entryFeeSDG),
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (tournament.entryFeeSDG == 0L) NeonGreenLight else NeonCyanLight,
                            fontSize = 14.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Registration progress
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "المقاعد المحجوزة: ${tournament.registeredCount} / ${tournament.maxParticipants}",
                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                )
                Text(
                    text = "متبقي ${tournament.seatsLeft} مقاعد",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (tournament.seatsLeft <= 5) NeonRed else NeonGreenLight,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { tournament.progressRatio },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = NeonGold,
                trackColor = ObsidianBorder
            )
        }
    }
}

@Composable
fun TournamentListItemCard(
    tournament: Tournament,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable(onClick = onClick),
        backgroundColor = ObsidianCard,
        borderColor = ObsidianBorder
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Game Color Strip
            Box(
                modifier = Modifier
                    .size(width = 4.dp, height = 70.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(tournament.gameType.brandColor)
            )
            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = tournament.gameType.titleArabic,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = tournament.gameType.brandColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )
                    StatusTag(status = tournament.status)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = tournament.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 14.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${tournament.startDateArabic} • ${tournament.startTimeArabic} • ${tournament.format.titleArabic}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "الجوائز: %,d ج.س".format(tournament.totalPrizePoolSDG),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = NeonGoldLight,
                            fontSize = 11.sp
                        )
                    )
                    Text(
                        text = "${tournament.registeredCount}/${tournament.maxParticipants} لاعب",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun SeasonHighlightCard(
    season: Season,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClick),
        backgroundColor = ObsidianCard,
        borderColor = NeonPurple.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(NeonPurple.copy(alpha = 0.15f), Color.Transparent)
                    )
                )
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(NeonPurple.copy(alpha = 0.3f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "الموسم 1",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFFC084FC),
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "متبقي ${season.daysRemaining} يوم",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = season.seasonTitleArabic,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "جوائز الموسم: %,d ج.س نقدية وكؤوس".format(season.totalPrizePoolSDG),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = NeonGoldLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = Color(0xFFC084FC),
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

@Composable
fun NewsAndAnnouncementCard(
    title: String,
    date: String,
    content: String,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        backgroundColor = ObsidianCard,
        borderColor = ObsidianBorder
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
                Text(
                    text = date,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            )
        }
    }
}
