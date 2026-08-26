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
import com.example.data.model.*
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
    COMMUNITY("المجتمع", Icons.Default.Forum, Icons.Outlined.Forum),
    REWARDS("المكافآت", Icons.Default.CardGiftcard, Icons.Outlined.CardGiftcard),
    PROFILE("ملفي", Icons.Default.Person, Icons.Outlined.Person),
    WALLET("المحفظة", Icons.Default.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet),
    RANKINGS("التصنيف", Icons.Default.Leaderboard, Icons.Outlined.Leaderboard),
    TEAMS("الفرق", Icons.Default.Groups, Icons.Outlined.Groups)
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
    val uiMessages by viewModel.uiMessages.collectAsStateWithLifecycle()
    val selectedRankingScope by viewModel.selectedRankingScope.collectAsStateWithLifecycle()
    val currentLeaderboard by viewModel.currentLeaderboard.collectAsStateWithLifecycle()
    val playerSearchResults by viewModel.playerSearchResults.collectAsStateWithLifecycle()
    val miniGames by viewModel.miniGames.collectAsStateWithLifecycle()
    val economyConfig by viewModel.economyConfig.collectAsStateWithLifecycle()
    val dailyGamePointsEarned by viewModel.dailyGamePointsEarned.collectAsStateWithLifecycle()
    val activeMiniGame by viewModel.activeMiniGame.collectAsStateWithLifecycle()
    val lastGameRewardResult by viewModel.lastGameRewardResult.collectAsStateWithLifecycle()
    val isWatchingAd by viewModel.isWatchingAd.collectAsStateWithLifecycle()
    val adCountdown by viewModel.adCountdown.collectAsStateWithLifecycle()

    // Chat states
    val conversations by viewModel.conversations.collectAsStateWithLifecycle()
    val messagesMap by viewModel.messagesMap.collectAsStateWithLifecycle()
    val activeConversation by viewModel.activeConversation.collectAsStateWithLifecycle()
    val showChatSheet by viewModel.showChatSheet.collectAsStateWithLifecycle()

    var currentTab by remember { mutableStateOf(NavigationTab.HOME) }
    var selectedTournament by remember { mutableStateOf<Tournament?>(null) }
    var showNotificationsSheet by remember { mutableStateOf(false) }

    val unreadNotificationsCount = notifications.count { !it.isRead }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiMessages) {
        val latestMsg = uiMessages.lastOrNull()
        latestMsg?.let {
            snackbarHostState.showSnackbar(
                message = it.message,
                duration = SnackbarDuration.Short
            )
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
                    actionColor = NeonCyanLight
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
                contentColor = NeonCyan,
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
                            selectedIconColor = NeonCyanLight,
                            selectedTextColor = NeonCyanLight,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary,
                            indicatorColor = NeonCyan.copy(alpha = 0.15f)
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
                        onTournamentClick = { selectedTournament = it },
                        onGameClick = { game ->
                            viewModel.selectGameFilter(game.type)
                            currentTab = NavigationTab.TOURNAMENTS
                        },
                        onDailyRewardClick = { viewModel.claimEarnOpportunity(it) },
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
                        onGameFilterChange = { viewModel.selectGameFilter(it) },
                        onStatusFilterChange = { viewModel.selectTournamentStatus(it) },
                        onSearchQueryChange = { viewModel.setSearchQuery(it) },
                        onTournamentClick = { selectedTournament = it }
                    )
                }
                NavigationTab.COMMUNITY -> {
                    CommunityScreen(
                        currentUser = currentUser,
                        posts = communityPosts,
                        playerSearchResults = playerSearchResults,
                        onLikePost = { viewModel.toggleLikePost(it) },
                        onFollowUser = { viewModel.toggleFollowUser(it) },
                        onAddComment = { postId, content -> viewModel.addComment(postId, content) },
                        onCreatePost = { content, gameTag -> viewModel.createPost(content, gameTag) },
                        onReportPost = { postId, reason -> viewModel.reportPost(postId, reason) },
                        onBlockUser = { authorId -> viewModel.blockUser(authorId) },
                        onSearchPlayers = { viewModel.searchPlayers(it) },
                        onSearchPosts = { viewModel.setCommunitySearchQuery(it) },
                        onInvitePlayerToTeam = { viewModel.invitePlayerToTeam(it) },
                        onOpenDirectChat = { playerId, playerName, gameType ->
                            viewModel.openDirectChatWithPlayer(playerId, playerName, gameType)
                        },
                        onOpenGeneralChat = { viewModel.openChat() }
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
                        activePlayingGame = activeMiniGame,
                        lastGameRewardResult = lastGameRewardResult,
                        isWatchingAd = isWatchingAd,
                        adCountdown = adCountdown,
                        onClaimOpportunity = { viewModel.claimEarnOpportunity(it) },
                        onWatchAd = { viewModel.startWatchingRewardedAd(it) },
                        onRedeemReward = { viewModel.redeemReward(it) },
                        onPlayGame = { viewModel.playMiniGame(it) },
                        onCloseGame = { viewModel.closeMiniGame() },
                        onSubmitGameSession = { sub, watchedAd -> viewModel.submitMiniGameSession(sub, watchedAd) },
                        onWatchAdForGameMultiplier = { viewModel.watchAdForMiniGameMultiplier(it) }
                    )
                }
                NavigationTab.PROFILE -> {
                    ProfileScreen(
                        user = currentUser,
                        onUpdateGameId = { gameType, inGameId, inGameName -> viewModel.updateGameId(gameType, inGameId, inGameName) },
                        onChangeUsernameOnce = { viewModel.changeUsernameOnce(it) },
                        onSubmitAdminUsernameRequest = { desired, reason -> viewModel.submitAdminUsernameRequest(desired, reason) },
                        onNavigateToRankings = { currentTab = NavigationTab.RANKINGS },
                        onNavigateToWallet = { currentTab = NavigationTab.WALLET }
                    )
                }
                NavigationTab.WALLET -> {
                    WalletScreen(
                        depositBalance = depositBalance,
                        tournamentWinnings = tournamentWinnings,
                        transactions = transactions,
                        onDeposit = { amount, method, refCode, phone -> viewModel.depositFunds(amount, method, refCode, phone) },
                        onWithdrawal = { amount, method, account, name -> viewModel.requestWithdrawal(amount, method, account, name) }
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
                NavigationTab.TEAMS -> {
                    TeamsScreen(
                        currentTeam = currentTeam,
                        currentUser = currentUser,
                        onCreateTeam = { name, tag, game, bio -> viewModel.createTeam(name, tag, game, bio) },
                        onInvitePlayer = { viewModel.invitePlayerToTeam(it) },
                        onRemoveMember = { viewModel.removeTeamMember(it) }
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
            onDismiss = { selectedTournament = null },
            onRegister = { tourney, usePoints ->
                viewModel.registerTournament(tourney, usePoints)
            },
            onSubmitMatchResult = { tId, mId, s1, s2, proof ->
                viewModel.submitMatchResult(tId, mId, s1, s2, proof)
            },
            onDisputeMatch = { tId, mId, reason ->
                viewModel.disputeMatch(tId, mId, reason)
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

    // Modal Private Chat Sheet
    if (showChatSheet) {
        val activeConvMessages = activeConversation?.let { messagesMap[it.id] } ?: emptyList()
        ChatSheet(
            conversations = conversations,
            activeConversation = activeConversation,
            messages = activeConvMessages,
            onSelectConversation = { viewModel.selectConversation(it) },
            onSendMessage = { text, timer, isVoice -> viewModel.sendMessage(text, timer, isVoice) },
            onSetTimer = { viewModel.setChatTimer(it) },
            onBlockUser = { viewModel.blockUser(it) },
            onReportUser = { id, reason -> viewModel.reportPost(id, reason) },
            onViewOnceClick = { viewModel.markViewOnceMessage(it) },
            onDismiss = { viewModel.closeChat() }
        )
    }
}
