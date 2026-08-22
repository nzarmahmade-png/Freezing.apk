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
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*

@Composable
fun RewardsScreen(
    pointsBalance: Int,
    rewardItems: List<RewardItem>,
    earnOpportunities: List<EarnOpportunity>,
    redeemedVouchers: List<RedeemedVoucher>,
    isWatchingAd: Boolean,
    adCountdown: Int,
    onClaimOpportunity: (EarnOpportunity) -> Unit,
    onWatchAd: (EarnOpportunity) -> Unit,
    onRedeemReward: (RewardItem) -> Unit,
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
        // Rewards Top Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ObsidianSurface)
                    .padding(16.dp)
            ) {
                Text(
                    text = "المكافآت ومتجر عازم 🎁",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = TextPrimary,
                        fontSize = 20.sp
                    )
                )
                Text(
                    text = "اجمع النقاط واستبدلها فوراً بجواهر فري فاير وشدات ببجي وكوبونات خصم",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 12.sp)
                )
            }
        }

        // Reward Points Balance Card
        item {
            GlassCard(
                modifier = Modifier
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
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(NeonGold.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = NeonGoldLight)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "رصيد نقاط المكافآت",
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
                    }
                }
            }
        }

        // Earn Points Section
        item {
            SectionHeader(
                title = "طرق كسب النقاط اليومية ⚡",
                subtitle = "أكمل المهام اليومية لزيادة رصيدك"
            )
        }

        items(earnOpportunities) { opp ->
            EarnOpportunityItemCard(
                opportunity = opp,
                onClaim = { onClaimOpportunity(opp) },
                onWatchAd = { onWatchAd(opp) }
            )
        }

        // AzomStore Catalog Header & Category Chips
        item {
            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader(
                title = "متجر الاستبدال الرقمي (AzomStore) 💎",
                subtitle = "أكواد شحن فورية وقسائم تسوق"
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

        // My Redeemed Vouchers Section
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

    // Rewarded Ad Simulation Overlay
    if (isWatchingAd) {
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
