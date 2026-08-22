package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GameType
import com.example.data.model.Tournament
import com.example.data.model.TournamentStatus
import com.example.ui.components.GamePill
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*

@Composable
fun TournamentsScreen(
    tournaments: List<Tournament>,
    selectedGameFilter: GameType,
    selectedStatusFilter: TournamentStatus?,
    searchQuery: String,
    onGameFilterChange: (GameType) -> Unit,
    onStatusFilterChange: (TournamentStatus?) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onTournamentClick: (Tournament) -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredTournaments = tournaments.filter { tourney ->
        val matchGame = selectedGameFilter == GameType.ALL || tourney.gameType == selectedGameFilter
        val matchStatus = selectedStatusFilter == null || tourney.status == selectedStatusFilter
        val matchQuery = searchQuery.isBlank() ||
                tourney.title.contains(searchQuery, ignoreCase = true) ||
                tourney.description.contains(searchQuery, ignoreCase = true)
        matchGame && matchStatus && matchQuery
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
    ) {
        // Search & Filter Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(ObsidianSurface)
                .padding(16.dp)
        ) {
            Text(
                text = "بطولات دارفور التنافسية 🏆",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    color = TextPrimary,
                    fontSize = 20.sp
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "نافس في فري فاير، ببجي موبايل، وإي فوتبول بيس بجوائز نقدية فورية",
                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 12.sp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                placeholder = { Text("ابحث عن اسم البطولة أو اللعبة...", fontSize = 12.sp, color = TextMuted) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(Icons.Default.Close, contentDescription = "مسح", tint = TextSecondary)
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = ObsidianCard,
                    unfocusedContainerColor = ObsidianCard,
                    focusedBorderColor = NeonGold,
                    unfocusedBorderColor = ObsidianBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Game Type Pills Row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(GameType.values()) { gameType ->
                    GamePill(
                        gameType = gameType,
                        isSelected = selectedGameFilter == gameType,
                        onClick = { onGameFilterChange(gameType) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Status filter chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    StatusFilterChip(
                        title = "الكل",
                        isSelected = selectedStatusFilter == null,
                        onClick = { onStatusFilterChange(null) }
                    )
                }
                items(TournamentStatus.values()) { status ->
                    StatusFilterChip(
                        title = status.titleArabic,
                        isSelected = selectedStatusFilter == status,
                        onClick = { onStatusFilterChange(status) }
                    )
                }
            }
        }

        // Tournaments List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 8.dp, bottom = 90.dp)
        ) {
            item {
                SectionHeader(
                    title = "قائمة البطولات المتاحة",
                    subtitle = "تم العثور على ${filteredTournaments.size} بطولة"
                )
            }

            if (filteredTournaments.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "لا توجد بطولات مطابقة للبحث",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondary
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "جرب تغيير فلتر اللعبة أو حالة البطولة",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                            )
                        }
                    }
                }
            } else {
                items(filteredTournaments) { tourney ->
                    TournamentListItemCard(
                        tournament = tourney,
                        onClick = { onTournamentClick(tourney) }
                    )
                }
            }
        }
    }
}

@Composable
fun StatusFilterChip(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        color = if (isSelected) NeonCyan.copy(alpha = 0.2f) else ObsidianCard,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) NeonCyanLight else ObsidianBorder
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) NeonCyanLight else TextSecondary,
                fontSize = 11.sp
            ),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}
