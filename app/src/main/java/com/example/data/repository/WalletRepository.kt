package com.example.data.repository

import com.example.data.datasource.LocalMockDataSource
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

interface IWalletRepository {
    val depositBalanceFlow: Flow<Long>
    val tournamentWinningsFlow: Flow<Long>
    val transactionsFlow: Flow<List<WalletTransaction>>

    suspend fun depositFunds(request: DepositRequest): Result<WalletTransaction>
    suspend fun requestWithdrawal(request: WithdrawalRequest): Result<WalletTransaction>
    suspend fun payTournamentEntryFee(tournamentId: String, tournamentTitle: String, feeSDG: Long): Result<WalletTransaction>
    suspend fun awardTournamentPrize(tournamentTitle: String, prizeSDG: Long): WalletTransaction
}

class WalletRepository : IWalletRepository {
    private val _depositBalance = MutableStateFlow<Long>(LocalMockDataSource.currentUser.depositBalanceSDG)
    private val _tournamentWinnings = MutableStateFlow<Long>(LocalMockDataSource.currentUser.tournamentWinningsSDG)
    private val _transactions = MutableStateFlow<List<WalletTransaction>>(LocalMockDataSource.initialTransactions)

    override val depositBalanceFlow: Flow<Long> = _depositBalance.asStateFlow()
    override val tournamentWinningsFlow: Flow<Long> = _tournamentWinnings.asStateFlow()
    override val transactionsFlow: Flow<List<WalletTransaction>> = _transactions.asStateFlow()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    override suspend fun depositFunds(request: DepositRequest): Result<WalletTransaction> {
        if (request.amountSDG <= 0) {
            return Result.failure(Exception("مبلغ الإيداع يجب أن يكون أكبر من 0"))
        }

        val tx = WalletTransaction(
            id = "tx_dep_${UUID.randomUUID().toString().take(8)}",
            type = TransactionType.DEPOSIT,
            amountSDG = request.amountSDG,
            timestamp = dateFormat.format(Date()),
            status = TransactionStatus.COMPLETED,
            paymentMethod = request.paymentMethod,
            referenceNumber = request.transactionRefCode.ifBlank { "DEP-${System.currentTimeMillis().toString().takeLast(6)}" },
            note = "إيداع عبر ${request.paymentMethod.titleArabic} (المرجع: ${request.transactionRefCode.ifBlank { "تأكيد فوري" }})"
        )

        _depositBalance.value += request.amountSDG
        _transactions.value = listOf(tx) + _transactions.value
        return Result.success(tx)
    }

    override suspend fun requestWithdrawal(request: WithdrawalRequest): Result<WalletTransaction> {
        if (request.amountSDG <= 0) {
            return Result.failure(Exception("مبلغ السحب يجب أن يكون أكبر من 0"))
        }

        // IMPORTANT RULE: Only approved tournament winnings can be withdrawn
        if (request.amountSDG > _tournamentWinnings.value) {
            return Result.failure(Exception("رصيد أرباح البطولات المعتمدة لا يكفي. الرصيد القابل للسحب: ${_tournamentWinnings.value} جنيه سوداني (رصيد الإيداع مخصص فقط للمشاركة في البطولات)"))
        }

        if (request.amountSDG < 5000) {
            return Result.failure(Exception("الحد الأدنى لطلب السحب هو 5,000 جنيه سوداني"))
        }

        val tx = WalletTransaction(
            id = "tx_wth_${UUID.randomUUID().toString().take(8)}",
            type = TransactionType.WITHDRAWAL,
            amountSDG = request.amountSDG,
            timestamp = dateFormat.format(Date()),
            status = TransactionStatus.PENDING,
            paymentMethod = request.payoutMethod,
            referenceNumber = "WTH-${System.currentTimeMillis().toString().takeLast(6)}",
            note = "طلب سحب أرباح إلى ${request.payoutMethod.titleArabic} - الحساب: ${request.accountNumberOrPhone} (المستلم: ${request.recipientName})"
        )

        _tournamentWinnings.value -= request.amountSDG
        _transactions.value = listOf(tx) + _transactions.value
        return Result.success(tx)
    }

    override suspend fun payTournamentEntryFee(
        tournamentId: String,
        tournamentTitle: String,
        feeSDG: Long
    ): Result<WalletTransaction> {
        if (feeSDG <= 0) {
            // Free tournament
            return Result.success(
                WalletTransaction(
                    id = "tx_free_${UUID.randomUUID().toString().take(8)}",
                    type = TransactionType.TOURNAMENT_ENTRY_FEE,
                    amountSDG = 0,
                    timestamp = dateFormat.format(Date()),
                    status = TransactionStatus.COMPLETED,
                    paymentMethod = null,
                    referenceNumber = "FREE-ENTRY",
                    note = "اشتراك مجاني في بطولة $tournamentTitle",
                    targetTournamentTitle = tournamentTitle
                )
            )
        }

        val depositBal = _depositBalance.value
        val winningsBal = _tournamentWinnings.value
        val totalAvailable = depositBal + winningsBal

        if (totalAvailable < feeSDG) {
            return Result.failure(Exception("الرصيد المتاح غير كافٍ لدفع رسوم الاشتراك ($feeSDG جنيه سوداني). يرجى شحن رصيد الإيداع أولاً."))
        }

        // Deduct from deposit balance first, then winnings balance if needed
        if (depositBal >= feeSDG) {
            _depositBalance.value -= feeSDG
        } else {
            val remainder = feeSDG - depositBal
            _depositBalance.value = 0
            _tournamentWinnings.value -= remainder
        }

        val tx = WalletTransaction(
            id = "tx_fee_${UUID.randomUUID().toString().take(8)}",
            type = TransactionType.TOURNAMENT_ENTRY_FEE,
            amountSDG = feeSDG,
            timestamp = dateFormat.format(Date()),
            status = TransactionStatus.COMPLETED,
            paymentMethod = null,
            referenceNumber = "ENTRY-${System.currentTimeMillis().toString().takeLast(6)}",
            note = "رسوم اشتراك بطولة: $tournamentTitle",
            targetTournamentTitle = tournamentTitle
        )

        _transactions.value = listOf(tx) + _transactions.value
        return Result.success(tx)
    }

    override suspend fun awardTournamentPrize(
        tournamentTitle: String,
        prizeSDG: Long
    ): WalletTransaction {
        val tx = WalletTransaction(
            id = "tx_win_${UUID.randomUUID().toString().take(8)}",
            type = TransactionType.TOURNAMENT_WINNING,
            amountSDG = prizeSDG,
            timestamp = dateFormat.format(Date()),
            status = TransactionStatus.COMPLETED,
            paymentMethod = null,
            referenceNumber = "WIN-${System.currentTimeMillis().toString().takeLast(6)}",
            note = "جائزة بطولة معتمدة: $tournamentTitle",
            targetTournamentTitle = tournamentTitle
        )

        _tournamentWinnings.value += prizeSDG
        _transactions.value = listOf(tx) + _transactions.value
        return tx
    }
}
