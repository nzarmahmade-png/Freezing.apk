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
    private val notifRepo: INotificationRepository = NotificationRepository(),
    private val chatRepo: IChatRepository = ChatRepository()
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

    // Chat Flows
    val conversations: StateFlow<List<ChatConversation>> = chatRepo.conversationsFlow.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        emptyList()
    )
    val messagesMap: StateFlow<Map<String, List<ChatMessage>>> = chatRepo.messagesFlow.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        emptyMap()
    )

    val supportedGames = LocalMockDataSource.supportedGames

    // UI State
    private val _selectedGameFilter = MutableStateFlow<GameType>(GameType.ALL)
    val selectedGameFilter = _selectedGameFilter.asStateFlow()

    private val _selectedTournamentStatus = MutableStateFlow<TournamentStatus?>(null)
    val selectedTournamentStatus = _selectedTournamentStatus.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _communitySearchQuery = MutableStateFlow("")
    val communitySearchQuery = _communitySearchQuery.asStateFlow()

    private val _selectedRankingScope = MutableStateFlow<RankingScope>(RankingScope.REGIONAL_DARFUR)
    val selectedRankingScope = _selectedRankingScope.asStateFlow()

    private val _currentLeaderboard = MutableStateFlow<List<LeaderboardEntry>>(LocalMockDataSource.currentSeason.topLeaderboard)
    val currentLeaderboard = _currentLeaderboard.asStateFlow()

    private val _activeMiniGame = MutableStateFlow<MiniGameItem?>(null)
    val activeMiniGame = _activeMiniGame.asStateFlow()

    private val _lastGameRewardResult = MutableStateFlow<GameRewardResult?>(null)
    val lastGameRewardResult = _lastGameRewardResult.asStateFlow()

    private val _isWatchingAd = MutableStateFlow(false)
    val isWatchingAd = _isWatchingAd.asStateFlow()

    private val _adCountdown = MutableStateFlow(0)
    val adCountdown = _adCountdown.asStateFlow()

    private val _activeConversation = MutableStateFlow<ChatConversation?>(null)
    val activeConversation = _activeConversation.asStateFlow()

    private val _showChatSheet = MutableStateFlow(false)
    val showChatSheet = _showChatSheet.asStateFlow()

    private val _uiMessages = MutableStateFlow<List<UiMessage>>(emptyList())
    val uiMessages = _uiMessages.asStateFlow()

    private val _playerSearchResults = MutableStateFlow<List<PlayerSearchResult>>(emptyList())
    val playerSearchResults = _playerSearchResults.asStateFlow()

    fun showMessage(msg: String, isError: Boolean = false) {
        val item = UiMessage(message = msg, isError = isError)
        _uiMessages.value = _uiMessages.value + item
        viewModelScope.launch {
            delay(3500)
            _uiMessages.value = _uiMessages.value.filter { it.id != item.id }
        }
    }

    fun dismissMessage(id: String) {
        _uiMessages.value = _uiMessages.value.filter { it.id != id }
    }

    // --- Filter & Search ---
    fun selectGameFilter(game: GameType) {
        _selectedGameFilter.value = game
    }

    fun selectTournamentStatus(status: TournamentStatus?) {
        _selectedTournamentStatus.value = status
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCommunitySearchQuery(query: String) {
        _communitySearchQuery.value = query
    }

    // --- Tournament Actions ---
    fun registerTournament(
        tournament: Tournament,
        useRewardPoints: Boolean = false
    ) {
        viewModelScope.launch {
            val user = currentUser.value
            val userGameProfile = user.gameProfiles[tournament.gameType.id]

            if (userGameProfile == null || userGameProfile.inGameId.isBlank()) {
                showMessage("يرجى إدخال وتأكيد معرّف اللعبة في ملفك الشخصي قبل التسجيل", isError = true)
                return@launch
            }

            // Fee deduction
            if (useRewardPoints) {
                if (user.rewardPoints < tournament.entryFeePoints) {
                    showMessage("نقاط المكافآت غير كافية للاشتراك", isError = true)
                    return@launch
                }
            } else {
                if (user.depositBalanceSDG < tournament.entryFeeSDG) {
                    showMessage("رصيد الإيداع غير كافٍ. يرجى شحن محفظتك بـ ${tournament.entryFeeSDG} ج.س", isError = true)
                    return@launch
                }
                // Deduct from Deposit Balance strictly
                walletRepo.payTournamentEntryFee(tournament.id, tournament.title, tournament.entryFeeSDG)
            }

            val result = tournamentRepo.registerForTournament(
                tournamentId = tournament.id,
                userId = user.id,
                username = user.username,
                gameId = userGameProfile.inGameId,
                teamName = if (tournament.format != TournamentFormat.SOLO) user.currentTeamName else null
            )

            result.fold(
                onSuccess = {
                    userRepo.addXpAndPoints(150, 50)
                    notifRepo.addNotification(
                        title = "تأكيد تسجيل في ${tournament.title}",
                        message = "تم حجز مقعدك بنجاح! موعد الانطلاق: ${tournament.startDateArabic} - ${tournament.startTimeArabic}",
                        type = NotificationType.TOURNAMENT,
                        targetId = tournament.id
                    )
                    showMessage("تم تأكيد تسجيلك في البطولة بنجاح! 🏆🎮")
                },
                onFailure = { err ->
                    showMessage(err.message ?: "فشل التسجيل في البطولة", isError = true)
                }
            )
        }
    }

    fun submitMatchResult(tournamentId: String, matchId: String, team1Score: Int, team2Score: Int, proofUrl: String?) {
        viewModelScope.launch {
            val result = tournamentRepo.submitMatchResult(tournamentId, matchId, team1Score, team2Score, proofUrl)
            result.fold(
                onSuccess = {
                    showMessage("تم إرسال نتيجة المباراة ولقطة الشاشة بنجاح! النتيجة قيد مراجعة الإدارة والاعتماد 📋")
                },
                onFailure = { err ->
                    showMessage(err.message ?: "فشل رفع النتيجة", isError = true)
                }
            )
        }
    }

    fun disputeMatch(tournamentId: String, matchId: String, reason: String) {
        viewModelScope.launch {
            val result = tournamentRepo.disputeMatch(tournamentId, matchId, reason)
            result.fold(
                onSuccess = {
                    showMessage("تم تقديم الاعتراض الرسمي لإدارة الحكام وسيتم الرد خلال ساعة ⚠️")
                },
                onFailure = { err ->
                    showMessage(err.message ?: "فشل تقديم الاعتراض", isError = true)
                }
            )
        }
    }

    // --- Wallet Actions ---
    fun depositFunds(amountSDG: Long, method: PaymentMethod, reference: String, senderPhone: String = "") {
        viewModelScope.launch {
            val request = DepositRequest(
                amountSDG = amountSDG,
                paymentMethod = method,
                senderPhoneNumberOrAccount = senderPhone,
                transactionRefCode = reference
            )
            val result = walletRepo.depositFunds(request)
            result.fold(
                onSuccess = { tx ->
                    userRepo.addXpAndPoints(50, 20)
                    notifRepo.addNotification(
                        title = "إيداع رصيد ناجح",
                        message = "تمت إضافة %,d ج.س إلى رصيد الإيداع لدخول البطولات.".format(amountSDG),
                        type = NotificationType.DEPOSIT,
                        targetId = tx.id
                    )
                    showMessage("تم شحن رصيد الإيداع بنجاح: %,d ج.س ✅".format(amountSDG))
                },
                onFailure = { err ->
                    showMessage(err.message ?: "فشل عملية الإيداع", isError = true)
                }
            )
        }
    }

    fun requestWithdrawal(amountSDG: Long, method: PaymentMethod, accountDetails: String, recipientName: String = "") {
        viewModelScope.launch {
            val request = WithdrawalRequest(
                amountSDG = amountSDG,
                payoutMethod = method,
                accountNumberOrPhone = accountDetails,
                recipientName = recipientName.ifBlank { currentUser.value.fullName }
            )
            val result = walletRepo.requestWithdrawal(request)
            result.fold(
                onSuccess = { tx ->
                    notifRepo.addNotification(
                        title = "طلب سحب الأرباح",
                        message = "تم تقديم طلب سحب %,d ج.س إلى حساب %s وهو قيد التحويل.".format(amountSDG, method.titleArabic),
                        type = NotificationType.WITHDRAWAL,
                        targetId = tx.id
                    )
                    showMessage("تم رفع طلب السحب بنجاح! سيتم التحويل لحسابك خلال 30 دقيقة.")
                },
                onFailure = { err ->
                    showMessage(err.message ?: "فشل طلب السحب", isError = true)
                }
            )
        }
    }

    // --- Rewards & MiniGames ---
    fun playMiniGame(game: MiniGameItem) {
        _activeMiniGame.value = game
    }

    fun closeMiniGame() {
        _activeMiniGame.value = null
    }

    fun submitMiniGameSession(submission: GameSessionSubmission, watchedMultiplierAd: Boolean) {
        viewModelScope.launch {
            val result = rewardRepo.validateAndClaimGameReward(submission, watchedMultiplierAd)
            result.fold(
                onSuccess = { rewardResult ->
                    _lastGameRewardResult.value = rewardResult
                    if (rewardResult.totalPointsAwarded > 0) {
                        userRepo.addXpAndPoints(rewardResult.totalXpAwarded, rewardResult.totalPointsAwarded)
                        showMessage("🎉 أحرزت سكور ${submission.rawScore} وحصلت على +${rewardResult.totalPointsAwarded} نقطة مكافأة!")
                    } else {
                        showMessage("أحرزت سكور ${submission.rawScore}. العب مجدداً لتحقيق سكور أعلى وكسب النقاط!")
                    }
                },
                onFailure = { err ->
                    showMessage(err.message ?: "حدث خطأ أثناء احتساب النقاط", isError = true)
                }
            )
            closeMiniGame()
        }
    }

    fun watchAdForMiniGameMultiplier(submission: GameSessionSubmission) {
        viewModelScope.launch {
            _isWatchingAd.value = true
            _adCountdown.value = 5
            while (_adCountdown.value > 0) {
                delay(1000)
                _adCountdown.value -= 1
            }
            _isWatchingAd.value = false
            submitMiniGameSession(submission, watchedMultiplierAd = true)
        }
    }

    fun claimEarnOpportunity(opportunity: EarnOpportunity) {
        viewModelScope.launch {
            val result = rewardRepo.claimEarnOpportunity(opportunity.id)
            result.fold(
                onSuccess = { (pts, xp) ->
                    userRepo.addXpAndPoints(xp, pts)
                    showMessage("تم استلام المكافأة: +$pts نقطة و +$xp XP! 🌟")
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
            while (_adCountdown.value > 0) {
                delay(1000)
                _adCountdown.value -= 1
            }
            _isWatchingAd.value = false
            claimEarnOpportunity(opportunity)
        }
    }

    fun redeemReward(rewardItem: RewardItem) {
        viewModelScope.launch {
            val result = rewardRepo.redeemReward(rewardItem)
            result.fold(
                onSuccess = { voucher ->
                    showMessage("تهانينا! تم استبدال '${rewardItem.titleArabic}' بنجاح! كود الشحن: ${voucher.voucherCode}")
                },
                onFailure = { err ->
                    showMessage(err.message ?: "فشل استبدال المكافأة", isError = true)
                }
            )
        }
    }

    // --- Community Actions ---
    fun createPost(content: String, gameTag: GameType?) {
        viewModelScope.launch {
            val user = currentUser.value
            val result = communityRepo.createPost(content, gameTag, user)
            result.fold(
                onSuccess = {
                    userRepo.addXpAndPoints(50, 10)
                    showMessage("تم نشر منشورك في مجتمع دارفور بنجاح! 🚀")
                },
                onFailure = { err ->
                    showMessage(err.message ?: "فشل النشر", isError = true)
                }
            )
        }
    }

    fun toggleLikePost(postId: String) {
        viewModelScope.launch {
            communityRepo.toggleLikePost(postId)
        }
    }

    fun toggleFollowUser(authorId: String) {
        viewModelScope.launch {
            val result = communityRepo.toggleFollowUser(authorId)
            result.fold(
                onSuccess = { isFollowing ->
                    if (isFollowing) {
                        showMessage("أنت الآن تتابع هذا اللاعب ✅")
                    } else {
                        showMessage("تم إلغاء المتابعة")
                    }
                },
                onFailure = { err ->
                    showMessage(err.message ?: "فشل التحديث", isError = true)
                }
            )
        }
    }

    fun addComment(postId: String, content: String) {
        viewModelScope.launch {
            val user = currentUser.value
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

    // --- Chat & Messaging ---
    fun openChat(conversation: ChatConversation? = null) {
        _activeConversation.value = conversation
        _showChatSheet.value = true
    }

    fun closeChat() {
        _showChatSheet.value = false
        _activeConversation.value = null
    }

    fun selectConversation(conv: ChatConversation) {
        if (_activeConversation.value?.id == conv.id) {
            _activeConversation.value = null
        } else {
            _activeConversation.value = conv
        }
    }

    fun openDirectChatWithPlayer(playerId: String, playerName: String, gameType: GameType) {
        viewModelScope.launch {
            val conv = chatRepo.getOrCreateConversation(playerId, playerName, gameType)
            _activeConversation.value = conv
            _showChatSheet.value = true
        }
    }

    fun sendMessage(text: String, timer: DisappearingTimer, isVoice: Boolean) {
        val active = _activeConversation.value ?: return
        viewModelScope.launch {
            chatRepo.sendMessage(
                conversationId = active.id,
                text = text,
                disappearingTimer = timer,
                isVoiceNote = isVoice,
                voiceDurationSec = if (isVoice) 4 else 0
            )
        }
    }

    fun setChatTimer(timer: DisappearingTimer) {
        val active = _activeConversation.value ?: return
        viewModelScope.launch {
            chatRepo.setConversationDisappearingTimer(active.id, timer)
            showMessage("تم ضبط مؤقت الرسائل ذاتية الاختفاء: ${timer.titleArabic}")
        }
    }

    fun markViewOnceMessage(msgId: String) {
        val active = _activeConversation.value ?: return
        viewModelScope.launch {
            chatRepo.markViewOnceAsViewed(active.id, msgId)
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

    // --- Profile & Username Rules ---
    fun changeUsernameOnce(newUsername: String) {
        viewModelScope.launch {
            val result = userRepo.changeUsernameOnce(newUsername)
            result.fold(
                onSuccess = { name ->
                    showMessage("تم تغيير اسم المستخدم الخاص بك إلى '$name' بنجاح! (تم استهلاك فرصة التعديل لمرة واحدة)")
                },
                onFailure = { err ->
                    showMessage(err.message ?: "فشل تغيير اسم المستخدم", isError = true)
                }
            )
        }
    }

    fun submitAdminUsernameRequest(desiredUsername: String, reason: String) {
        viewModelScope.launch {
            val result = userRepo.submitAdminUsernameRequest(desiredUsername, reason)
            result.fold(
                onSuccess = {
                    showMessage("تم إرسال طلب تغيير اسم المستخدم إلى إدارة المنصة بنجاح وسيتم الرد عليك قريباً! 📨")
                },
                onFailure = { err ->
                    showMessage(err.message ?: "فشل إرسال الطلب", isError = true)
                }
            )
        }
    }

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
