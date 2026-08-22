package com.example.data.model

enum class TransactionType(val titleArabic: String, val isCredit: Boolean) {
    DEPOSIT("إيداع رصيد", true),
    TOURNAMENT_WINNING("أرباح بطولة (معتمدة)", true),
    TOURNAMENT_ENTRY_FEE("رسوم دخول بطولة", false),
    WITHDRAWAL("طلب سحب أرباح", false),
    REWARD_CONVERSION("مكافأة نقاط", true)
}

enum class TransactionStatus(val titleArabic: String) {
    COMPLETED("مكتمل"),
    PENDING("قيد المعالجة"),
    REJECTED("مرفوض")
}

enum class PaymentMethod(val titleArabic: String, val subtitleArabic: String, val iconName: String) {
    BANKAK("بنكك (بنك الخرطوم)", "تحويل فوري عبر رقم الحساب / الموبايل", "account_balance"),
    FAWRY("فوري السودان", "دفع عبر نقاط فوري المعتمدة", "storefront"),
    SUDANI_CASH("سوداني كاش", "محفظة الهاتف - شبكة سوداني", "phone_android"),
    MTN_CASH("إم تي إن كاش", "محفظة الهاتف - شبكة MTN", "phone_android"),
    ZAIN_CASH("زين كاش", "محفظة الهاتف - شبكة زين", "phone_android")
}

data class WalletTransaction(
    val id: String,
    val type: TransactionType,
    val amountSDG: Long,
    val timestamp: String,
    val status: TransactionStatus,
    val paymentMethod: PaymentMethod?,
    val referenceNumber: String,
    val note: String,
    val targetTournamentTitle: String? = null
)

data class DepositRequest(
    val amountSDG: Long,
    val paymentMethod: PaymentMethod,
    val senderPhoneNumberOrAccount: String,
    val transactionRefCode: String
)

data class WithdrawalRequest(
    val amountSDG: Long,
    val payoutMethod: PaymentMethod,
    val recipientName: String,
    val accountNumberOrPhone: String,
    val notes: String = ""
)
