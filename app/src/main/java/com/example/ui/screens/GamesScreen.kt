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
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.SportsEsports
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
import com.example.data.model.Game
import com.example.data.model.GameType
import com.example.data.model.Tournament
import com.example.ui.components.GlassCard
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*

@Composable
fun GamesScreen(
    games: List<Game>,
    tournaments: List<Tournament>,
    onSelectGame: (GameType) -> Unit,
    onTournamentClick: (Tournament) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedGameId by remember { mutableStateOf(games.firstOrNull()?.id ?: "game_ff") }
    val activeGame = games.find { it.id == selectedGameId } ?: games.first()
    val gameTournaments = tournaments.filter { it.gameType == activeGame.type }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ObsidianSurface)
                    .padding(16.dp)
            ) {
                Text(
                    text = "الألعاب المدعومة 🎮",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = TextPrimary,
                        fontSize = 20.sp
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "منظومة بطولات دارفور المعتمدة للألعاب التنافسية",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 12.sp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Game Selector Row
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(games) { game ->
                        GameSelectionTab(
                            game = game,
                            isSelected = game.id == selectedGameId,
                            onClick = { selectedGameId = game.id }
                        )
                    }
                }
            }
        }

        // Active Game Banner & Stats
        item {
            Spacer(modifier = Modifier.height(14.dp))
            ActiveGameHeroCard(
                game = activeGame,
                onExploreTournaments = { onSelectGame(activeGame.type) }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Game specific tournaments
        item {
            SectionHeader(
                title = "بطولات ${activeGame.nameArabic}",
                subtitle = "مجموع الجوائز والاشتراكات المتاحة"
            )
        }

        if (gameTournaments.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "لا توجد بطولات نشطة حالياً لهذه اللعبة، ترقبوا التحديث القادم!",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                    )
                }
            }
        } else {
            items(gameTournaments) { tourney ->
                TournamentListItemCard(
                    tournament = tourney,
                    onClick = { onTournamentClick(tourney) }
                )
            }
        }

        // Top Ranking Players & Teams Info for Game
        item {
            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader(
                title = "معلومات وأنظمة لعب ${activeGame.nameArabic}",
                subtitle = "قوانين وإرشادات المشاركة المعتمدة"
            )
            GameRulesCard(game = activeGame)
        }
    }
}

@Composable
fun GameSelectionTab(
    game: Game,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        color = if (isSelected) game.type.brandColor.copy(alpha = 0.2f) else ObsidianCard,
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp,
            if (isSelected) game.type.brandColor else ObsidianBorder
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(game.type.brandColor)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = game.nameArabic.split(" ").first(),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) TextPrimary else TextSecondary,
                    fontSize = 13.sp
                )
            )
        }
    }
}

@Composable
fun ActiveGameHeroCard(
    game: Game,
    onExploreTournaments: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        backgroundColor = ObsidianCard,
        borderColor = game.type.brandColor.copy(alpha = 0.5f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(game.type.brandColor.copy(alpha = 0.15f), Color.Transparent)
                    )
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = game.nameArabic,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = TextPrimary,
                        fontSize = 18.sp
                    )
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(game.type.brandColor.copy(alpha = 0.25f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "${game.activeTournamentsCount} بطولات نشطة",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = game.type.brandColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = game.descriptionArabic,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GameStatItem(
                    icon = Icons.Default.SportsEsports,
                    label = "اللاعبون المسجلون",
                    value = "${game.activePlayersCount}+",
                    modifier = Modifier.weight(1f)
                )
                GameStatItem(
                    icon = Icons.Default.Groups,
                    label = "الفرق والكلانات",
                    value = "${game.teamsCount} فريق",
                    modifier = Modifier.weight(1f)
                )
                GameStatItem(
                    icon = Icons.Default.EmojiEvents,
                    label = "نظام اللعب الأشهر",
                    value = game.popularFormat.split(" ").first(),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun GameStatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(ObsidianCardElevated)
            .border(0.5.dp, ObsidianBorder, RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Column {
            Icon(imageVector = icon, contentDescription = null, tint = NeonCyanLight, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = label, style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 9.sp))
            Text(text = value, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 11.sp))
        }
    }
}

@Composable
fun GameRulesCard(game: Game) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        backgroundColor = ObsidianCard,
        borderColor = ObsidianBorder
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Text(
                text = "معايير اللعب والنزاهة في دارفور",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "1. التأكد من حفظ معرف اللعبة (In-Game ID) الصحيح في ملفك الشخصي لتسهيل استلام دعوات الروم.\n2. يتم تصوير شاشة المباراة أو رفع لقطة النتيجة النهائية لاعتماد الفوز والأرباح النقدية فوراً.\n3. الالتزام باللعب النظيف، أي استخدام للغش أو الثغرات يؤدي لحظر الحساب نهائياً ومصادرة الأرصدة.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
            )
        }
    }
}
