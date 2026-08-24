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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.GlassCard
import com.example.ui.components.MiniGamePlayerDialog
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*

@Composable
fun RewardsScreen(
    pointsBalance: Int,
    rewardItems: List<RewardItem>,
    earnOpportunities: List<EarnOpportunity>,
    redeemedVouchers: List<RedeemedVoucher>,
    miniGames: List<MiniGameItem>,
    dailyGamePointsEarned: Int,
    economyConfig: RewardEconomyConfig,
    activePlayingGame: MiniGameItem?,
    lastGameRewardResult: GameRewardResult?,
    isWatchingAd: Boolean,
    adCountdown: Int,
    onClaimOpportunity: (EarnOpportunity) -> Unit,
    onWatchAd: (EarnOpportunity) -> Unit,
    onRedeemReward: (RewardItem) -> Unit,
    onPlayGame: (MiniGameItem) -> Unit,
    onCloseGame: () -> Unit,
    onSubmitGameSession: (GameSessionSubmission, Boolean) -> Unit,
    onWatchAdForGameMultiplier: (GameSessionSubmission) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf<RewardCategory?>(null) }
    var itemToRedeem by remember { mutableStateOf<RewardItem?>(null) }
    val clipboardManager = LocalClipboardManager.current

    val filteredItems = if (selectedCategory == null) {
        rewardItems
    } else {
        rewardItems.filter { it.category == selectedCategory }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // 1. Rewards Screen Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ObsidianSurface)
                    .padding(16.dp)
            ) {
                Text(
                    text = "مركز المكافآت والألعاب 🎁",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = TextPrimary,
                        fontSize = 20.sp
                    )
                )
                Text(
                    text = "العب الألعاب المصغرة، شاهد الإعلانات الترويجية، واستبدل نقاطك فوراً بشدات وجواهر وقسائم",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 12.sp)
                )
            }
        }

        // 2. Dedicated Balance Card (Exclusive to Rewards Page)
        item {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                backgroundColor = ObsidianCard,
                borderColor = NeonGold.copy(alpha = 0.6f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(NeonGold.copy(alpha = 0.15f), Color.Transparent)
                            )
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(NeonGold.copy(alpha = 0.2f))
                                    .border(1.dp, NeonGoldLight, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = NeonGoldLight)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "رصيد نقاط المكافآت الإجمالي",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                                )
                                Text(
                                    text = "%,d نقطة".format(pointsBalance),
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        color = NeonGoldLight,
                                        fontSize = 26.sp
                                    )
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = NeonGold.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonGold.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "جاهز للاستبدال 💎",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = NeonGoldLight,
                                    fontSize = 11.sp
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // 3. Dedicated Mini-Games Area (HTML5 Interactive Games with Horizontal Carousel)
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                SectionHeader(
                    title = "ساحة الألعاب وكسب النقاط 🕹️",
                    subtitle = "ألعاب HTML5 خفيفة وسريعة التفاعل لا تتطلب تحميل خارجي"
                )

                // Daily Game Points Economy Quota Bar
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    color = ObsidianCard,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.SportsEsports, contentDescription = null, tint = NeonCyanLight, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "الحد اليومي لنقاط اللعب المجاني",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextPrimary,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                            Text(
                                text = "$dailyGamePointsEarned / ${economyConfig.dailyGamePointsCap} نقطة",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (dailyGamePointsEarned >= economyConfig.dailyGamePointsCap) NeonRedLight else NeonGreenLight,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = {
                                (dailyGamePointsEarned.toFloat() / economyConfig.dailyGamePointsCap.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = NeonGold,
                            trackColor = ObsidianBorder
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "💡 نظام الاقتصاد العادل: كل 500 سكور = 5 نقاط، 1000 = 10 نقاط، 2000+ = 20 نقطة. شاهد إعلاناً لمضاعفة مكافأتك 3x!",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 10.sp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Horizontal Mini-Games Carousel
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(miniGames) { game ->
                        MiniGameCard(
                            game = game,
                            onPlay = { onPlayGame(game) }
                        )
                    }
                }
            }
        }

        // 4. Rewarded Ads & Economy Multiplier Card (Monetization Core)
        item {
            Spacer(modifier = Modifier.height(18.dp))
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                backgroundColor = ObsidianCardElevated,
                borderColor = NeonCyan.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(NeonCyan.copy(alpha = 0.12f), Color.Transparent)
                            )
                        )
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
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(NeonCyan.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.PlayCircle, contentDescription = null, tint = NeonCyanLight, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "إعلانات الرعاة والشركاء",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(NeonCyan.copy(alpha = 0.2f))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "+50 نقطة",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = NeonCyanLight,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                            Text(
                                text = "شاهد إعلانات قصيرة لدعم جوائز بطولات دارفور والحصول على أعلى مكافأة نقاط",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp)
                            )
                        }
                    }

                    Button(
                        onClick = {
                            val adOpp = earnOpportunities.firstOrNull { it.actionType == EarnActionType.WATCH_REWARD_AD }
                                ?: EarnOpportunity(
                                    id = "earn_watch_ad",
                                    titleArabic = "مشاهدة إعلان راعي",
                                    descriptionArabic = "شاهد إعلاناً واحصل على 50 نقطة فورية",
                                    pointsReward = 50,
                                    xpReward = 100,
                                    actionType = EarnActionType.WATCH_REWARD_AD
                                )
                            onWatchAd(adOpp)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("مشاهدة 🎬", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // 5. Daily Earn Opportunities Section
        item {
            Spacer(modifier = Modifier.height(18.dp))
            SectionHeader(
                title = "طرق كسب النقاط الإضافية ⚡",
                subtitle = "سجل الدخول يومياً وشارك في فعاليات المجتمع"
            )
        }

        items(earnOpportunities) { opp ->
            EarnOpportunityItemCard(
                opportunity = opp,
                onClaim = { onClaimOpportunity(opp) },
                onWatchAd = { onWatchAd(opp) }
            )
        }

        // 6. AzomStore Catalog Header & Category Chips
        item {
            Spacer(modifier = Modifier.height(20.dp))
            SectionHeader(
                title = "متجر الاستبدال الرقمي (AzomStore) 💎",
                subtitle = "أكواد شحن فورية وقسائم تسوق وبطاقات ألعاب"
            )

            LazyRow(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    RewardCategoryChip(
                        title = "الكل",
                        isSelected = selectedCategory == null,
                        onClick = { selectedCategory = null }
                    )
                }
                items(RewardCategory.values()) { cat ->
                    RewardCategoryChip(
                        title = cat.titleArabic,
                        isSelected = selectedCategory == cat,
                        onClick = { selectedCategory = cat }
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Reward Items List
        items(filteredItems) { item ->
            RewardCatalogItemCard(
                item = item,
                canAfford = pointsBalance >= item.pointsCost,
                onRedeem = { itemToRedeem = item }
            )
        }

        // 7. My Redeemed Vouchers Section
        if (redeemedVouchers.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(20.dp))
                SectionHeader(
                    title = "قسائمي المستبدلة الجاهزة للاستخدام 🎟️",
                    subtitle = "انسخ كود الشحن واستخدمه فوراً"
                )
            }

            items(redeemedVouchers) { voucher ->
                RedeemedVoucherCard(
                    voucher = voucher,
                    onCopy = { clipboardManager.setText(AnnotatedString(voucher.voucherCode)) }
                )
            }
        }
    }

    // Active HTML5 Game Dialog (Pinball / Cyber Reflex / Space Glide)
    if (activePlayingGame != null) {
        MiniGamePlayerDialog(
            game = activePlayingGame,
            onDismiss = onCloseGame,
            onSubmitGameSession = onSubmitGameSession,
            lastRewardResult = lastGameRewardResult,
            isWatchingAd = isWatchingAd,
            adCountdown = adCountdown,
            onWatchAdForMultiplier = onWatchAdForGameMultiplier,
            dailyPointsEarned = dailyGamePointsEarned,
            dailyPointsCap = economyConfig.dailyGamePointsCap
        )
    }

    // Rewarded Ad Simulation Overlay (Standalone)
    if (isWatchingAd && activePlayingGame == null) {
        AlertDialog(
            onDismissRequest = {},
            containerColor = ObsidianCard,
            title = {
                Text("جاري تشغيل الإعلان الترويجي 🎬", fontWeight = FontWeight.Bold, color = TextPrimary)
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "عازم جيمنج - شريك الرياضات الإلكترونية الأول في دارفور",
                        style = MaterialTheme.typography.bodyMedium.copy(color = NeonCyanLight, fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator(color = NeonGold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "يتم احتساب المكافأة خلال: $adCountdown ثواني",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = NeonGoldLight)
                    )
                }
            },
            confirmButton = {}
        )
    }

    // Redeem Confirmation Dialog
    if (itemToRedeem != null) {
        AlertDialog(
            onDismissRequest = { itemToRedeem = null },
            containerColor = ObsidianCard,
            title = {
                Text("تأكيد استبدال المكافأة", fontWeight = FontWeight.Bold, color = TextPrimary)
            },
            text = {
                Column {
                    Text(
                        text = itemToRedeem!!.titleArabic,
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = NeonGoldLight,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = itemToRedeem!!.descriptionArabic,
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "تكلفة الاستبدال: %,d نقطة".format(itemToRedeem!!.pointsCost),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text = "سيتم توليد كود الشحن الفوري في قائمة قسائمك.",
                        style = MaterialTheme.typography.labelSmall.copy(color = NeonGreenLight)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val item = itemToRedeem!!
                        itemToRedeem = null
                        onRedeemReward(item)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGold, contentColor = TextOnAccent)
                ) {
                    Text("استبدال الآن", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToRedeem = null }) {
                    Text("إلغاء", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
fun MiniGameCard(
    game: MiniGameItem,
    onPlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = when (game.id) {
        "pinball" -> NeonGold
        "reflex" -> NeonCyan
        else -> NeonGreen
    }

    GlassCard(
        modifier = modifier
            .width(260.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onPlay),
        backgroundColor = ObsidianCard,
        borderColor = accentColor.copy(alpha = 0.5f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(accentColor.copy(alpha = 0.12f), Color.Transparent)
                    )
                )
                .padding(14.dp)
        ) {
            // Card Top Row: Badge & Category
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(ObsidianCardElevated)
                        .border(1.dp, accentColor.copy(alpha = 0.6f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (game.id) {
                            "pinball" -> Icons.Default.SportsEsports
                            "reflex" -> Icons.Default.FlashOn
                            else -> Icons.Default.RocketLaunch
                        },
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                if (game.badgeArabic != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(accentColor.copy(alpha = 0.2f))
                            .border(0.5.dp, accentColor, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = game.badgeArabic,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Game Name
            Text(
                text = game.titleArabic,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Black,
                    color = TextPrimary,
                    fontSize = 15.sp
                ),
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Short Description
            Text(
                text = game.shortDescriptionArabic,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                ),
                maxLines = 2,
                modifier = Modifier.height(30.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Reward Tier & Play Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "المكافأة المستحقة",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 9.sp)
                    )
                    Text(
                        text = "حتى ${game.maxRewardPoints} نقطة 🎁",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = accentColor,
                            fontSize = 11.sp
                        )
                    )
                }

                Button(
                    onClick = onPlay,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accentColor,
                        contentColor = if (accentColor == NeonGold) TextOnAccent else Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text("العب الآن 🕹️", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun LesserText(
    text: String,
    color: Color,
    fontSize: androidx.compose.ui.unit.TextUnit,
    fontWeight: FontWeight
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall.copy(
            color = color,
            fontSize = fontSize,
            fontWeight = fontWeight
        )
    )
}

@Composable
fun EarnOpportunityItemCard(
    opportunity: EarnOpportunity,
    onClaim: () -> Unit,
    onWatchAd: () -> Unit
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
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(ObsidianCardElevated),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (opportunity.actionType == EarnActionType.WATCH_REWARD_AD) Icons.Default.PlayCircle else Icons.Default.Stars,
                        contentDescription = null,
                        tint = NeonGoldLight,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = opportunity.titleArabic,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "${opportunity.descriptionArabic} (+${opportunity.pointsReward} نقطة • +${opportunity.xpReward} XP)",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            if (opportunity.actionType == EarnActionType.WATCH_REWARD_AD) {
                Button(
                    onClick = onWatchAd,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("مشاهدة 🎬", color = NeonCyanLight, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            } else if (opportunity.isClaimedToday) {
                Text("تم الاستلام ✅", color = TextMuted, fontSize = 11.sp)
            } else {
                Button(
                    onClick = onClaim,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGold, contentColor = TextOnAccent),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("استلام", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun RewardCatalogItemCard(
    item: RewardItem,
    canAfford: Boolean,
    onRedeem: () -> Unit
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
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(ObsidianCardElevated)
                        .border(1.dp, NeonGold.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (item.category) {
                            RewardCategory.GAME_CARDS -> Icons.Default.SportsEsports
                            RewardCategory.TOURNAMENT_PASS -> Icons.Default.ConfirmationNumber
                            RewardCategory.AZOM_STORE -> Icons.Default.ShoppingBag
                            RewardCategory.DISCOUNTS -> Icons.Default.LocalOffer
                        },
                        contentDescription = null,
                        tint = NeonGoldLight,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = item.titleArabic,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        if (item.badgeArabic != null) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(NeonGold.copy(alpha = 0.2f))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = item.badgeArabic,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = NeonGoldLight,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                    Text(
                        text = item.descriptionArabic,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "%,d نقطة (متبقي ${item.stockCount})".format(item.pointsCost),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = NeonGoldLight,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Button(
                onClick = onRedeem,
                enabled = canAfford && item.stockCount > 0,
                colors = ButtonDefaults.buttonColors(containerColor = NeonGold, contentColor = TextOnAccent),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (item.stockCount <= 0) "نفذت الكمية" else if (canAfford) "استبدال" else "نقاط غير كافية",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun RedeemedVoucherCard(
    voucher: RedeemedVoucher,
    onCopy: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        backgroundColor = ObsidianCardElevated,
        borderColor = NeonGreen.copy(alpha = 0.5f)
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
                Text(
                    text = voucher.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
                Text(
                    text = voucher.redeemedAt,
                    style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 10.sp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(ObsidianBg)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = voucher.voucherCode,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = NeonGreenLight,
                        letterSpacing = 1.sp
                    )
                )
                TextButton(onClick = onCopy) {
                    Text("نسخ الكود 📋", color = NeonCyanLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun RewardCategoryChip(
    title: String,
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
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) NeonGoldLight else TextSecondary,
                fontSize = 11.sp
            ),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}
