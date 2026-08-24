package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AppTopBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

enum class NavigationTab(
    val titleArabic: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME("الرئيسية", Icons.Default.Home, Icons.Outlined.Home),
    TOURNAMENTS("البطولات", Icons.Default.EmojiEvents, Icons.Outlined.EmojiEvents),
    GAMES("الألعاب", Icons.Default.SportsEsports, Icons.Outlined.SportsEsports),
    COMMUNITY("المجتمع", Icons.Default.Forum, Icons.Outlined.Forum),
    TEAMS("الفرق", Icons.Default.Groups, Icons.Outlined.Groups),
    WALLET("المحفظة", Icons.Default.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet),
    REWARDS("المكافآت", Icons.Default.CardGiftcard, Icons.Outlined.CardGiftcard),
    PROFILE("ملفي", Icons.Default.Person, Icons.Outlined.Person),
    RANKINGS("التصنيف", Icons.Default.Leaderboard, Icons.Outlined.Leaderboard)
}

val BottomNavTabs = listOf(
    NavigationTab.HOME,
    NavigationTab.TOURNAMENTS,
    NavigationTab.COMMUNITY,
    NavigationTab.REWARDS,
    NavigationTab.PROFILE
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val currentTeam by viewModel.currentTeam.collectAsStateWithLifecycle()
    val tournaments by viewModel.tournaments.collectAsStateWithLifecycle()
    val depositBalance by viewModel.depositBalance.collectAsStateWithLifecycle()
    val tournamentWinnings by viewModel.tournamentWinnings.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val pointsBalance by viewModel.rewardPoints.collectAsStateWithLifecycle()
    val rewardItems by viewModel.rewardItems.collectAsStateWithLifecycle()
    val earnOpportunities by viewModel.earnOpportunities.collectAsStateWithLifecycle()
    val redeemedVouchers by viewModel.redeemedVouchers.collectAsStateWithLifecycle()
    val communityPosts by viewModel.communityPosts.collectAsStateWithLifecycle()
    val currentSeason by viewModel.currentSeason.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val selectedGameFilter by viewModel.selectedGameFilter.collectAsStateWithLifecycle()
    val selectedStatusFilter by viewModel.selectedTournamentStatus.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedTournament by viewModel.selectedTournament.collectAsStateWithLifecycle()
    val uiMessage by viewModel.uiMessage.collectAsStateWithLifecycle()
    val isWatchingAd by viewModel.isWatchingAd.collectAsStateWithLifecycle()
    val adCountdown by viewModel.adCountdown.collectAsStateWithLifecycle()
    val selectedRankingScope by viewModel.selectedRankingScope.collectAsStateWithLifecycle()
    val currentLeaderboard by viewModel.currentLeaderboard.collectAsStateWithLifecycle()
    val playerSearchResults by viewModel.playerSearchResults.collectAsStateWithLifecycle()
    val miniGames by viewModel.miniGames.collectAsStateWithLifecycle()
    val economyConfig by viewModel.economyConfig.collectAsStateWithLifecycle()
    val dailyGamePointsEarned by viewModel.dailyGamePointsEarned.collectAsStateWithLifecycle()
    val activePlayingMiniGame by viewModel.activePlayingMiniGame.collectAsStateWithLifecycle()
    val lastGameRewardResult by viewModel.lastGameRewardResult.collectAsStateWithLifecycle()

    var currentTab by remember { mutableStateOf(NavigationTab.HOME) }
    var showNotificationsSheet by remember { mutableStateOf(false) }

    val unreadNotificationsCount = notifications.count { !it.isRead }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiMessage) {
        uiMessage?.let {
            snackbarHostState.showSnackbar(
                message = it.message,
                duration = SnackbarDuration.Short
            )
            viewModel.dismissMessage()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = ObsidianBg,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = ObsidianCardElevated,
                    contentColor = TextPrimary,
                    actionColor = NeonGoldLight
                )
            }
        },
        topBar = {
            AppTopBar(
                user = currentUser,
                unreadNotificationsCount = unreadNotificationsCount,
                onWalletClick = { currentTab = NavigationTab.WALLET },
                onRewardsClick = { currentTab = NavigationTab.REWARDS },
                onRankingsClick = { currentTab = NavigationTab.RANKINGS },
                onNotificationsClick = { showNotificationsSheet = true }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = ObsidianSurface,
                contentColor = NeonGold,
                tonalElevation = 8.dp
            ) {
                BottomNavTabs.forEach { tab ->
                    val isSelected = currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentTab = tab },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.titleArabic
                            )
                        },
                        label = {
                            Text(
                                text = tab.titleArabic,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 10.sp
                                )
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NeonGoldLight,
                            selectedTextColor = NeonGoldLight,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary,
                            indicatorColor = NeonGold.copy(alpha = 0.15f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                NavigationTab.HOME -> {
                    HomeScreen(
                        user = currentUser,
                        tournaments = tournaments,
                        games = viewModel.supportedGames,
                        season = currentSeason,
                        earnOpportunities = earnOpportunities,
                        onTournamentClick = { viewModel.selectTournament(it) },
                        onGameClick = { game ->
                            viewModel.setGameFilter(game.type)
                            currentTab = NavigationTab.TOURNAMENTS
                        },
                        onDailyRewardClick = { viewModel.claimDailyReward(it) },
                        onWatchAdClick = { viewModel.startWatchingRewardedAd(it) },
                        onNavigateToTournaments = { currentTab = NavigationTab.TOURNAMENTS },
                        onNavigateToWallet = { currentTab = NavigationTab.WALLET },
                        onNavigateToRewards = { currentTab = NavigationTab.REWARDS },
                        onNavigateToRankings = { currentTab = NavigationTab.RANKINGS },
                        onNavigateToTeams = { currentTab = NavigationTab.TEAMS }
                    )
                }
                NavigationTab.TOURNAMENTS -> {
                    TournamentsScreen(
                        tournaments = tournaments,
                        selectedGameFilter = selectedGameFilter,
                        selectedStatusFilter = selectedStatusFilter,
                        searchQuery = searchQuery,
                        onGameFilterChange = { viewModel.setGameFilter(it) },
                        onStatusFilterChange = { viewModel.setTournamentStatusFilter(it) },
                        onSearchQueryChange = { viewModel.setSearchQuery(it) },
                        onTournamentClick = { viewModel.selectTournament(it) }
                    )
                }
                NavigationTab.GAMES -> {
                    GamesScreen(
                        games = viewModel.supportedGames,
                        tournaments = tournaments,
                        onSelectGame = { gameType ->
                            viewModel.setGameFilter(gameType)
                            currentTab = NavigationTab.TOURNAMENTS
                        },
                        onTournamentClick = { viewModel.selectTournament(it) }
                    )
                }
                NavigationTab.COMMUNITY -> {
                    CommunityScreen(
                        currentUser = currentUser,
                        posts = communityPosts,
                        playerSearchResults = playerSearchResults,
                        onLikePost = { viewModel.toggleLike(it) },
                        onAddComment = { postId, content -> viewModel.addComment(postId, content) },
                        onCreatePost = { content, gameTag -> viewModel.createPost(content, gameTag) },
                        onReportPost = { postId, reason -> viewModel.reportPost(postId, reason) },
                        onBlockUser = { authorId -> viewModel.blockUser(authorId) },
                        onSearchPlayers = { viewModel.searchPlayers(it) },
                        onInvitePlayerToTeam = { viewModel.invitePlayerToTeam(it) }
                    )
                }
                NavigationTab.TEAMS -> {
                    TeamsScreen(
                        currentTeam = currentTeam,
                        currentUser = currentUser,
                        onCreateTeam = { name, tag, game, bio -> viewModel.createTeam(name, tag, game, bio) },
                        onInvitePlayer = { viewModel.invitePlayerToTeam(it) },
                        onRemoveMember = { viewModel.removeTeamMember(it) }
                    )
                }
                NavigationTab.WALLET -> {
                    WalletScreen(
                        depositBalance = depositBalance,
                        tournamentWinnings = tournamentWinnings,
                        transactions = transactions,
                        onDeposit = { amount, method, refCode, phone -> viewModel.deposit(amount, method, refCode, phone) },
                        onWithdrawal = { amount, method, account, name -> viewModel.requestWithdrawal(amount, method, account, name) }
                    )
                }
                NavigationTab.REWARDS -> {
                    RewardsScreen(
                        pointsBalance = pointsBalance,
                        rewardItems = rewardItems,
                        earnOpportunities = earnOpportunities,
                        redeemedVouchers = redeemedVouchers,
                        miniGames = miniGames,
                        dailyGamePointsEarned = dailyGamePointsEarned,
                        economyConfig = economyConfig,
                        activePlayingGame = activePlayingMiniGame,
                        lastGameRewardResult = lastGameRewardResult,
                        isWatchingAd = isWatchingAd,
                        adCountdown = adCountdown,
                        onClaimOpportunity = { viewModel.claimDailyReward(it) },
                        onWatchAd = { viewModel.startWatchingRewardedAd(it) },
                        onRedeemReward = { viewModel.redeemReward(it) },
                        onPlayGame = { viewModel.playMiniGame(it) },
                        onCloseGame = { viewModel.closeMiniGame() },
                        onSubmitGameSession = { sub, adWatched -> viewModel.submitMiniGameSession(sub, adWatched) },
                        onWatchAdForGameMultiplier = { viewModel.watchAdForMiniGameMultiplier(it) }
                    )
                }
                NavigationTab.PROFILE -> {
                    ProfileScreen(
                        user = currentUser,
                        onUpdateGameId = { gameType, inGameId, inGameName -> viewModel.updateGameId(gameType, inGameId, inGameName) },
                        onNavigateToRankings = { currentTab = NavigationTab.RANKINGS },
                        onNavigateToWallet = { currentTab = NavigationTab.WALLET }
                    )
                }
                NavigationTab.RANKINGS -> {
                    RankingsScreen(
                        season = currentSeason,
                        leaderboard = currentLeaderboard,
                        selectedScope = selectedRankingScope,
                        onScopeChange = { viewModel.setRankingScope(it) },
                        onClaimReward = { viewModel.claimSeasonReward(it) }
                    )
                }
            }
        }
    }

    // Modal Tournament Detail Sheet
    selectedTournament?.let { tournament ->
        TournamentDetailSheet(
            tournament = tournament,
            currentUser = currentUser,
            onDismiss = { viewModel.selectTournament(null) },
            onRegister = { tourney, usePoints ->
                viewModel.registerForTournament(tourney, usePoints)
            }
        )
    }

    // Modal Notifications Sheet
    if (showNotificationsSheet) {
        NotificationsSheet(
            notifications = notifications,
            onDismiss = { showNotificationsSheet = false },
            onMarkRead = { viewModel.markNotificationRead(it) },
            onMarkAllRead = { viewModel.markAllNotificationsRead() }
        )
    }
}
