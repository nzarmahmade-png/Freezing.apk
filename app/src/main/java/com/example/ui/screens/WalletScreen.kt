package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.GlassCard
import com.example.ui.components.NeonButton
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*

@Composable
fun WalletScreen(
    depositBalance: Long,
    tournamentWinnings: Long,
    transactions: List<WalletTransaction>,
    onDeposit: (Long, PaymentMethod, String, String) -> Unit,
    onWithdrawal: (Long, PaymentMethod, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDepositDialog by remember { mutableStateOf(false) }
    var showWithdrawDialog by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf<TransactionType?>(null) }

    val filteredTransactions = if (selectedFilter == null) {
        transactions
    } else {
        transactions.filter { it.type == selectedFilter }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Wallet Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ObsidianSurface)
                    .padding(16.dp)
            ) {
                Text(
                    text = "المحفظة المالية الحقيقية 💳",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = TextPrimary,
                        fontSize = 20.sp
                    )
                )
                Text(
                    text = "إدارة رصيد الإيداع وسحب أرباح بطولات دارفور المعتمدة",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 12.sp)
                )
            }
        }

        // Two Distinct Balances Cards
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 1. Approved Tournament Winnings (Withdrawable)
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = ObsidianCard,
                    borderColor = NeonGreen.copy(alpha = 0.6f),
                    elevation = 6.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    listOf(NeonGreen.copy(alpha = 0.12f), Color.Transparent)
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
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(NeonGreen.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.EmojiEvents,
                                        contentDescription = null,
                                        tint = NeonGreenLight,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "أرباح البطولات المعتمدة 🏆",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                    )
                                    Text(
                                        text = "رصيد معتمد قابل للسحب الفوري إلى بنكك",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = NeonGreenLight,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(NeonGreen.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "قابل للسحب",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = NeonGreenLight,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "%,d ج.س".format(tournamentWinnings),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = NeonGreenLight,
                                fontSize = 26.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = { showWithdrawDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonGreen,
                                contentColor = TextOnAccent
                            )
                        ) {
                            Icon(Icons.Default.AccountBalance, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("طلب سحب الأرباح (Bankak / بنكك)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }

                // 2. Deposit Balance (For Tournaments Entry only)
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = ObsidianCard,
                    borderColor = NeonGold.copy(alpha = 0.5f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    listOf(NeonGold.copy(alpha = 0.08f), Color.Transparent)
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
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(NeonGold.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccountBalanceWallet,
                                        contentDescription = null,
                                        tint = NeonGoldLight,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "رصيد الإيداع والاشتراكات 🎯",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                    )
                                    Text(
                                        text = "مخصص لدفع رسوم اشتراك البطولات فقط",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = TextSecondary,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(ObsidianCardElevated)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "للبطولات فقط",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = TextSecondary,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "%,d ج.س".format(depositBalance),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = NeonGoldLight,
                                fontSize = 24.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = { showDepositDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonGold,
                                contentColor = TextOnAccent
                            )
                        ) {
                            Icon(Icons.Default.AddCard, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("إيداع رصيد جديد (بنكك / فوري / كاش)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // Platform Wallet Regulations Policy Card
        item {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                backgroundColor = ObsidianCardElevated,
                borderColor = ObsidianBorder
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = NeonCyanLight,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "قوانين ولوائح المحفظة المالية المعتمدة 🛡️",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = NeonCyanLight
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "• يمنع منعاً باتاً التحويل المالي المباشر بين اللاعبين لضمان النزاهة.\n• رصيد الإيداع يستخدم حصراً للمشاركة في البطولات.\n• أرباح البطولات المعتمدة بعد فوزك هي وحدها القابلة للسحب المالي إلى حسابك البنكي.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Transaction History
        item {
            SectionHeader(
                title = "سجل المعاملات والعمليات المالية",
                subtitle = "كافة عمليات الإيداع، رسوم البطولات، والأرباح"
            )
        }

        if (filteredTransactions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("لا توجد عمليات مسجلة", color = TextMuted, fontSize = 12.sp)
                }
            }
        } else {
            items(filteredTransactions) { tx ->
                TransactionItemCard(tx = tx)
            }
        }
    }

    // Deposit Dialog
    if (showDepositDialog) {
        DepositDialog(
            onDismiss = { showDepositDialog = false },
            onSubmit = { amount, method, refCode, phone ->
                showDepositDialog = false
                onDeposit(amount, method, refCode, phone)
            }
        )
    }

    // Withdrawal Dialog
    if (showWithdrawDialog) {
        WithdrawDialog(
            maxWithdrawable = tournamentWinnings,
            onDismiss = { showWithdrawDialog = false },
            onSubmit = { amount, method, account, name ->
                showWithdrawDialog = false
                onWithdrawal(amount, method, account, name)
            }
        )
    }
}

@Composable
fun TransactionItemCard(tx: WalletTransaction) {
    val (icon, tintColor) = when (tx.type) {
        TransactionType.DEPOSIT -> Pair(Icons.Default.AddCircleOutline, NeonGoldLight)
        TransactionType.TOURNAMENT_WINNING -> Pair(Icons.Default.EmojiEvents, NeonGreenLight)
        TransactionType.TOURNAMENT_ENTRY_FEE -> Pair(Icons.Default.SportsEsports, NeonCyanLight)
        TransactionType.WITHDRAWAL -> Pair(Icons.Default.ArrowOutward, NeonRed)
        TransactionType.REWARD_CONVERSION -> Pair(Icons.Default.CardGiftcard, NeonPurple)
    }

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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(tintColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = tintColor, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = tx.type.titleArabic,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = tx.note,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            fontSize = 11.sp
                        ),
                        maxLines = 1
                    )
                    Text(
                        text = "${tx.timestamp} • مرجع: ${tx.referenceNumber}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${if (tx.type.isCredit) "+" else "-"} %,d ج.س".format(tx.amountSDG),
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Black,
                        color = if (tx.type.isCredit) NeonGreenLight else TextPrimary,
                        fontSize = 14.sp
                    )
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            when (tx.status) {
                                TransactionStatus.COMPLETED -> NeonGreen.copy(alpha = 0.2f)
                                TransactionStatus.PENDING -> NeonGold.copy(alpha = 0.2f)
                                TransactionStatus.REJECTED -> NeonRed.copy(alpha = 0.2f)
                            }
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = tx.status.titleArabic,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = when (tx.status) {
                                TransactionStatus.COMPLETED -> NeonGreenLight
                                TransactionStatus.PENDING -> NeonGoldLight
                                TransactionStatus.REJECTED -> NeonRed
                            },
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun DepositDialog(
    onDismiss: () -> Unit,
    onSubmit: (Long, PaymentMethod, String, String) -> Unit
) {
    var amountText by remember { mutableStateOf("10000") }
    var selectedMethod by remember { mutableStateOf(PaymentMethod.BANKAK) }
    var phoneOrAccount by remember { mutableStateOf("") }
    var refCode by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ObsidianCard,
        title = {
            Text("إيداع رصيد بطولات في المحفظة", fontWeight = FontWeight.Bold, color = TextPrimary)
        },
        text = {
            Column {
                Text("اختر وسيلة الدفع المحلية:", color = TextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))

                PaymentMethod.values().forEach { method ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selectedMethod == method) ObsidianCardElevated else Color.Transparent)
                            .clickable { selectedMethod = method }
                            .padding(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = selectedMethod == method, onClick = { selectedMethod = method })
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(method.titleArabic, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 13.sp)
                            Text(method.subtitleArabic, color = TextMuted, fontSize = 10.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("المبلغ بالجنية السوداني (SDG)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = phoneOrAccount,
                    onValueChange = { phoneOrAccount = it },
                    label = { Text("رقم حساب المحول أو رقم الهاتف") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = refCode,
                    onValueChange = { refCode = it },
                    label = { Text("رقم العملية / المرجع في تطبيق بنكك") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountText.toLongOrNull() ?: 0L
                    onSubmit(amt, selectedMethod, refCode, phoneOrAccount)
                },
                enabled = (amountText.toLongOrNull() ?: 0L) > 0,
                colors = ButtonDefaults.buttonColors(containerColor = NeonGold, contentColor = TextOnAccent)
            ) {
                Text("تأكيد الإيداع", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء", color = TextSecondary) }
        }
    )
}

@Composable
fun WithdrawDialog(
    maxWithdrawable: Long,
    onDismiss: () -> Unit,
    onSubmit: (Long, PaymentMethod, String, String) -> Unit
) {
    var amountText by remember { mutableStateOf("20000") }
    var selectedMethod by remember { mutableStateOf(PaymentMethod.BANKAK) }
    var recipientName by remember { mutableStateOf("محمد نزار") }
    var accountNumber by remember { mutableStateOf("2490192834") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ObsidianCard,
        title = {
            Text("طلب سحب أرباح البطولات 💵", fontWeight = FontWeight.Bold, color = TextPrimary)
        },
        text = {
            Column {
                Text(
                    text = "الرصيد المتاح للسحب من الأرباح المعتمدة: %,d ج.س".format(maxWithdrawable),
                    style = MaterialTheme.typography.bodySmall.copy(color = NeonGreenLight, fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("مبلغ السحب (الحد الأدنى: 5,000 ج.س)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = recipientName,
                    onValueChange = { recipientName = it },
                    label = { Text("اسم صاحب الحساب رباعي") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = accountNumber,
                    onValueChange = { accountNumber = it },
                    label = { Text("رقم حساب بنكك (بنك الخرطوم) أو المحفظة") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountText.toLongOrNull() ?: 0L
                    onSubmit(amt, selectedMethod, accountNumber, recipientName)
                },
                enabled = (amountText.toLongOrNull() ?: 0L) in 5000..maxWithdrawable,
                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = TextOnAccent)
            ) {
                Text("إرسال طلب السحب", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء", color = TextSecondary) }
        }
    )
}
