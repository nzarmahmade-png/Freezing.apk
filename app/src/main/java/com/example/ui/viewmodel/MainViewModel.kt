package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.datasource.LocalMockDataSource
import com.example.data.model.*
import com.example.data.repository.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class UiMessage(
    val id: String = System.currentTimeMillis().toString(),
    val message: String,
    val isError: Boolean = false
)

class MainViewModel(
    private val tournamentRepo: ITournamentRepository = TournamentRepository(),
    private val walletRepo: IWalletRepository = WalletRepository(),
    private val rewardRepo: IRewardRepository = RewardRepository(),
    private val communityRepo: ICommunityRepository = CommunityRepository(),
    private val userRepo: IUserRepository = UserRepository(),
    private val rankingsRepo: IRankingsRepository = RankingsRepository(),
    private val notifRepo: INotificationRepository = NotificationRepository()
) : ViewModel() {

    // Global Flows
    val currentUser: StateFlow<User> = userRepo.currentUserFlow.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        LocalMockDataSource.currentUser
    )
    val currentTeam: StateFlow<Team?> = userRepo.currentTeamFlow.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        LocalMockDataSource.currentTeam
    )
    val tournaments: StateFlow<List<Tournament>> = tournamentRepo.tournamentsFlow.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        LocalMockDataSource.initialTournaments
    )
    val depositBalance: StateFlow<Long> = walletRepo.depositBalanceFlow.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        LocalMockDataSource.currentUser.depositBalanceSDG
    )
    val tournamentWinnings: StateFlow<Long> = walletRepo.tournamentWinningsFlow.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        LocalMockDataSource.currentUser.tournamentWinningsSDG
    )
    val transactions: StateFlow<List<WalletTransaction>> = walletRepo.transactionsFlow.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        LocalMockDataSource.initialTransactions
    )
    val rewardPoints: StateFlow<Int> = rewardRepo.pointsBalanceFlow.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        LocalMockDataSource.currentUser.rewardPoints
    )
    val rewardItems: StateFlow<List<RewardItem>> = rewardRepo.rewardItemsFlow.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        LocalMockDataSource.rewardItems
    )
    val earnOpportunities: StateFlow<List<EarnOpportunity>> = rewardRepo.earnOpportunitiesFlow.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        LocalMockDataSource.earnOpportunities
    )
    val redeemedVouchers: StateFlow<List<RedeemedVoucher>> = rewardRepo.redeemedVouchersFlow.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        emptyList()
    )
    val miniGames: StateFlow<List<MiniGameItem>> = rewardRepo.miniGamesFlow.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        LocalMockDataSource.miniGames
    )
    val economyConfig: StateFlow<RewardEconomyConfig> = rewardRepo.economyConfigFlow.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        LocalMockDataSource.defaultEconomyConfig
    )
    val dailyGamePointsEarned: StateFlow<Int> = rewardRepo.dailyGamePointsEarnedFlow.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        0
    )
    val communityPosts: StateFlow<List<CommunityPost>> = communityRepo.postsFlow.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        LocalMockDataSource.communityPosts
    )
    val currentSeason: StateFlow<Season> = rankingsRepo.currentSeasonFlow.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        LocalMockDataSource.currentSeason
    )
    val notifications: StateFlow<List<NotificationItem>> = notifRepo.notificationsFlow.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        LocalMockDataSource.notifications
    )

    val supportedGames = LocalMockDataSource.supportedGames

    // UI State
    private val _selectedGameFilter = MutableStateFlow<GameType>(GameType.ALL)
    val selectedGameFilter = _selectedGameFilter.asStateFlow()

    private val _selectedTournamentStatus = MutableStateFlow<TournamentStatus?>(null)
    val selectedTournamentStatus = _selectedTournamentStatus.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedTournament = MutableStateFlow<Tournament?>(null)
    val selectedTournament = _selectedTournament.asStateFlow()

    private val _uiMessage = MutableStateFlow<UiMessage?>(null)
    val uiMessage = _uiMessage.asStateFlow()

    private val _isWatchingAd = MutableStateFlow(false)
    val isWatchingAd = _isWatchingAd.asStateFlow()

    private val _adCountdown = MutableStateFlow(5)
    val adCountdown = _adCountdown.asStateFlow()

    private val _selectedRankingScope = MutableStateFlow(RankingScope.GLOBAL)
    val selectedRankingScope = _selectedRankingScope.asStateFlow()

    private val _currentLeaderboard = MutableStateFlow<List<LeaderboardEntry>>(emptyList())
    val currentLeaderboard = _currentLeaderboard.asStateFlow()

    private val _playerSearchResults = MutableStateFlow<List<PlayerSearchResult>>(emptyList())
    val playerSearchResults = _playerSearchResults.asStateFlow()

    private val _activePlayingMiniGame = MutableStateFlow<MiniGameItem?>(null)
    val activePlayingMiniGame = _activePlayingMiniGame.asStateFlow()

    private val _lastGameRewardResult = MutableStateFlow<GameRewardResult?>(null)
    val lastGameRewardResult = _lastGameRewardResult.asStateFlow()

    init {
        loadLeaderboard(RankingScope.GLOBAL)
        searchPlayers("")
    }

    fun showMessage(message: String, isError: Boolean = false) {
        _uiMessage.value = UiMessage(message = message, isError = isError)
    }

    fun dismissMessage() {
        _uiMessage.value = null
    }

    fun setGameFilter(gameType: GameType) {
        _selectedGameFilter.value = gameType
    }

    fun setTournamentStatusFilter(status: TournamentStatus?) {
        _selectedTournamentStatus.value = status
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectTournament(tournament: Tournament?) {
        _selectedTournament.value = tournament
    }

    // --- Tournament Actions ---
    fun registerForTournament(tournament: Tournament, usePoints: Boolean = false) {
        viewModelScope.launch {
            val user = currentUser.first()
            val gameProfile = user.gameProfiles[tournament.gameType.id]
            val inGameId = gameProfile?.inGameId ?: "ID-FF-${user.username}"

            // Pay Entry Fee
            val feeSDG = if (usePoints) 0L else tournament.entryFeeSDG
            val payResult = walletRepo.payTournamentEntryFee(tournament.id, tournament.title, feeSDG)

            payResult.fold(
                onSuccess = {
                    val regResult = tournamentRepo.registerForTournament(
                        tournamentId = tournament.id,
                        userId = user.id,
                        username = user.username,
                        gameId = inGameId,
                        teamName = user.currentTeamName
                    )

                    regResult.fold(
                        onSuccess = { updatedTourney ->
                            _selectedTournament.value = updatedTourney
                            // Award XP for joining
                            userRepo.addXpAndPoints(150, 20)
                            notifRepo.addNotification(
                                title = "🎮 تم تأكيد اشتراكك في ${tournament.title}",
                                message = "تم حجز مقعدك بنجاح. معرف الروم: ${updatedTourney.customRoomId ?: "سيتم إعلانه قريباً"}",
                                type = NotificationType.TOURNAMENT,
                                targetId = tournament.id
                            )
                            showMessage("تم التسجيل وحجز مقعدك في البطولة بنجاح! 🎉")
                        },
                        onFailure = { err ->
                            showMessage(err.message ?: "فشل التسجيل في البطولة", isError = true)
                        }
                    )
                },
                onFailure = { err ->
                    showMessage(err.message ?: "فشل دفع رسوم الاشتراك", isError = true)
                }
            )
        }
    }

    // --- Wallet Actions ---
    fun deposit(amountSDG: Long, paymentMethod: PaymentMethod, refCode: String, phoneOrAccount: String) {
        viewModelScope.launch {
            val request = DepositRequest(
                amountSDG = amountSDG,
                paymentMethod = paymentMethod,
                senderPhoneNumberOrAccount = phoneOrAccount,
                transactionRefCode = refCode
            )
            val result = walletRepo.depositFunds(request)
            result.fold(
                onSuccess = { tx ->
                    notifRepo.addNotification(
                        title = "✅ تم استلام إيداعك بنجاح",
                        message = "تمت إضافة $amountSDG جنيه سوداني إلى رصيد الإيداع عبر ${paymentMethod.titleArabic}.",
                        type = NotificationType.DEPOSIT,
                        targetId = tx.id
                    )
                    showMessage("تم شحن رصيد الإيداع بمبلغ $amountSDG ج.س بنجاح!")
                },
                onFailure = { err ->
                    showMessage(err.message ?: "فشل عملية الإيداع", isError = true)
                }
            )
        }
    }

    fun requestWithdrawal(amountSDG: Long, method: PaymentMethod, account: String, recipientName: String) {
        viewModelScope.launch {
            val request = WithdrawalRequest(
                amountSDG = amountSDG,
                payoutMethod = method,
                recipientName = recipientName,
                accountNumberOrPhone = account
            )
            val result = walletRepo.requestWithdrawal(request)
            result.fold(
                onSuccess = { tx ->
                    notifRepo.addNotification(
                        title = "⏳ طلب سحب أرباح قيد المراجعة",
                        message = "تم استلام طلب سحب $amountSDG ج.س إلى حساب $account. سيتم الإيداع خلال ساعات العمل.",
                        type = NotificationType.WITHDRAWAL,
                        targetId = tx.id
                    )
                    showMessage("تم تقديم طلب سحب $amountSDG ج.س من أرباحك المعتمدة بنجاح!")
                },
                onFailure = { err ->
                    showMessage(err.message ?: "فشل طلب السحب", isError = true)
                }
            )
        }
    }

    // --- Rewards Actions ---
    fun claimDailyReward(opportunity: EarnOpportunity) {
        viewModelScope.launch {
            val result = rewardRepo.claimEarnOpportunity(opportunity.id)
            result.fold(
                onSuccess = { (pts, xp) ->
                    userRepo.addXpAndPoints(xp, pts)
                    showMessage("مبروك! حصلت على +$pts نقطة مكافأة و +$xp XP 🌟")
                },
                onFailure = { err ->
                    showMessage(err.message ?: "فشل استلام المكافأة", isError = true)
                }
            )
        }
    }

    fun startWatchingRewardedAd(opportunity: EarnOpportunity) {
        viewModelScope.launch {
            _isWatchingAd.value = true
            _adCountdown.value = 5
            for (i in 5 downTo 1) {
                _adCountdown.value = i
                delay(1000)
            }
            _isWatchingAd.value = false
            claimDailyReward(opportunity)
        }
    }

    fun redeemReward(rewardItem: RewardItem) {
        viewModelScope.launch {
            val result = rewardRepo.redeemReward(rewardItem)
            result.fold(
                onSuccess = { voucher ->
                    notifRepo.addNotification(
                        title = "🎁 كود استبدال المكافأة جاهز",
                        message = "كود الشحن لـ ${rewardItem.titleArabic}: ${voucher.voucherCode}",
                        type = NotificationType.REWARD,
                        targetId = voucher.id
                    )
                    showMessage("تم استبدال المكافأة بنجاح! الكود: ${voucher.voucherCode}")
                },
                onFailure = { err ->
                    showMessage(err.message ?: "فشل استبدال المكافأة", isError = true)
                }
            )
        }
    }

    // --- HTML5 Mini-Games & Reward Economy Actions ---
    fun playMiniGame(game: MiniGameItem) {
        _activePlayingMiniGame.value = game
        _lastGameRewardResult.value = null
    }

    fun closeMiniGame() {
        _activePlayingMiniGame.value = null
    }

    fun submitMiniGameSession(submission: GameSessionSubmission, watchedMultiplierAd: Boolean) {
        viewModelScope.launch {
            val result = rewardRepo.validateAndClaimGameReward(submission, watchedMultiplierAd)
            result.fold(
                onSuccess = { rewardRes ->
                    _lastGameRewardResult.value = rewardRes
                    if (rewardRes.totalPointsAwarded > 0 || rewardRes.totalXpAwarded > 0) {
                        userRepo.addXpAndPoints(rewardRes.totalXpAwarded, rewardRes.totalPointsAwarded)
                    }
                    showMessage(rewardRes.message, isError = false)
                },
                onFailure = { err ->
                    showMessage(err.message ?: "فشل اعتماد مكافأة جولة اللعبة", isError = true)
                }
            )
        }
    }

    fun watchAdForMiniGameMultiplier(submission: GameSessionSubmission) {
        viewModelScope.launch {
            _isWatchingAd.value = true
            _adCountdown.value = 5
            for (i in 5 downTo 1) {
                _adCountdown.value = i
                delay(1000)
            }
            _isWatchingAd.value = false
            submitMiniGameSession(submission, watchedMultiplierAd = true)
        }
    }

    // --- Community Actions ---
    fun createPost(content: String, gameTag: GameType?) {
        viewModelScope.launch {
            val user = currentUser.first()
            val result = communityRepo.createPost(content, gameTag, user)
            result.fold(
                onSuccess = {
                    userRepo.addXpAndPoints(50, 10)
                    showMessage("تم نشر منشورك في مجتمع دارفور بنجاح! ✨")
                },
                onFailure = { err ->
                    showMessage(err.message ?: "فشل نشر المنشور", isError = true)
                }
            )
        }
    }

    fun toggleLike(postId: String) {
        viewModelScope.launch {
            communityRepo.toggleLikePost(postId)
        }
    }

    fun addComment(postId: String, content: String) {
        viewModelScope.launch {
            val user = currentUser.first()
            val result = communityRepo.addComment(postId, content, user)
            result.fold(
                onSuccess = {
                    userRepo.addXpAndPoints(20, 5)
                    showMessage("تمت إضافة تعليقك بنجاح")
                },
                onFailure = { err ->
                    showMessage(err.message ?: "فشل إضافة التعليق", isError = true)
                }
            )
        }
    }

    fun reportPost(postId: String, reason: String) {
        viewModelScope.launch {
            communityRepo.reportPost(postId, reason)
            showMessage("تم استلام بلاغك وسيتم مراجعته من قِبل المشرفين.")
        }
    }

    fun blockUser(authorId: String) {
        viewModelScope.launch {
            communityRepo.blockUser(authorId)
            showMessage("تم حظر المستخدم وإخفاء منشوراته.")
        }
    }

    fun searchPlayers(query: String) {
        viewModelScope.launch {
            _playerSearchResults.value = communityRepo.searchPlayers(query)
        }
    }

    // --- Team Actions ---
    fun createTeam(name: String, tag: String, primaryGame: GameType, bio: String) {
        viewModelScope.launch {
            val result = userRepo.createTeam(name, tag, primaryGame, bio)
            result.fold(
                onSuccess = { team ->
                    userRepo.addXpAndPoints(300, 100)
                    showMessage("تم إنشاء فريق ${team.name} [${team.tag}] بنجاح! 🏆")
                },
                onFailure = { err ->
                    showMessage(err.message ?: "فشل إنشاء الفريق", isError = true)
                }
            )
        }
    }

    fun invitePlayerToTeam(usernameOrId: String) {
        viewModelScope.launch {
            val result = userRepo.invitePlayerToTeam(usernameOrId)
            result.fold(
                onSuccess = { member ->
                    showMessage("تم إرسال دعوة الانضمام للاعب ${member.username}")
                },
                onFailure = { err ->
                    showMessage(err.message ?: "فشل إرسال الدعوة", isError = true)
                }
            )
        }
    }

    fun removeTeamMember(userId: String) {
        viewModelScope.launch {
            userRepo.removeTeamMember(userId)
            showMessage("تمت إزالة العضو من الفريق")
        }
    }

    // --- Profile & Game IDs ---
    fun updateGameId(gameType: GameType, inGameId: String, inGameName: String) {
        viewModelScope.launch {
            userRepo.updateGameId(gameType, inGameId, inGameName)
            showMessage("تم حفظ وتحديث معرف لعبة ${gameType.titleArabic} بنجاح!")
        }
    }

    // --- Rankings & Season ---
    fun setRankingScope(scope: RankingScope) {
        _selectedRankingScope.value = scope
        loadLeaderboard(scope)
    }

    private fun loadLeaderboard(scope: RankingScope) {
        viewModelScope.launch {
            _currentLeaderboard.value = rankingsRepo.getLeaderboard(scope)
        }
    }

    fun claimSeasonReward(tierName: String) {
        viewModelScope.launch {
            val result = rankingsRepo.claimSeasonReward(tierName)
            result.fold(
                onSuccess = { (pts, cashSDG) ->
                    userRepo.addXpAndPoints(0, pts)
                    if (cashSDG > 0) {
                        walletRepo.awardTournamentPrize("مكافأة الموسم ($tierName)", cashSDG)
                    }
                    showMessage("تم استلام جائزة المستوى: +$pts نقطة و +$cashSDG ج.س! 🏆")
                },
                onFailure = { err ->
                    showMessage(err.message ?: "فشل استلام المكافأة", isError = true)
                }
            )
        }
    }

    // --- Notifications ---
    fun markNotificationRead(id: String) {
        viewModelScope.launch {
            notifRepo.markAsRead(id)
        }
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch {
            notifRepo.markAllAsRead()
            showMessage("تم تمييز جميع الإشعارات كمقروءة")
        }
    }
}
