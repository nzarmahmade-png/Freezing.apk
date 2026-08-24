package com.example.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.GameRewardResult
import com.example.data.model.GameSessionSubmission
import com.example.data.model.MiniGameItem
import com.example.ui.theme.*

class AndroidGameBridge(
    private val onGameSessionComplete: (GameSessionSubmission) -> Unit
) {
    private val mainHandler = Handler(Looper.getMainLooper())

    @JavascriptInterface
    fun onGameFinished(
        gameId: String,
        rawScore: Int,
        claimedPoints: Int,
        durationSeconds: Int,
        tapCount: Int,
        sessionChallengeToken: String
    ) {
        val submission = GameSessionSubmission(
            gameId = gameId,
            rawScore = rawScore,
            claimedPoints = claimedPoints,
            durationSeconds = durationSeconds,
            tapCount = tapCount,
            sessionChallengeToken = sessionChallengeToken
        )
        mainHandler.post {
            onGameSessionComplete(submission)
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun MiniGamePlayerDialog(
    game: MiniGameItem,
    onDismiss: () -> Unit,
    onSubmitGameSession: (GameSessionSubmission, Boolean) -> Unit,
    lastRewardResult: GameRewardResult?,
    isWatchingAd: Boolean,
    adCountdown: Int,
    onWatchAdForMultiplier: (GameSessionSubmission) -> Unit,
    dailyPointsEarned: Int,
    dailyPointsCap: Int,
    modifier: Modifier = Modifier
) {
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var pendingSubmission by remember { mutableStateOf<GameSessionSubmission?>(null) }
    var showPostGameClaimSheet by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = {
            webViewInstance?.destroy()
            onDismiss()
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(ObsidianBg)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
            ) {
                // Top Game Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ObsidianSurface)
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(NeonGold.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SportsEsports,
                                contentDescription = null,
                                tint = NeonGoldLight,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = game.titleArabic,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    fontSize = 16.sp
                                )
                            )
                            Text(
                                text = "النقاط اليومية من الألعاب: $dailyPointsEarned / $dailyPointsCap نقطة",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (dailyPointsEarned >= dailyPointsCap) NeonRedLight else NeonGreenLight,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    // Close Button
                    IconButton(
                        onClick = {
                            webViewInstance?.destroy()
                            onDismiss()
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(ObsidianCard)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "إغلاق اللعبة",
                            tint = TextSecondary
                        )
                    }
                }

                // Advertisement Banner Slot (Top/Integrated)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    color = ObsidianCard,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = NeonGoldLight, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "مكافأة الألعاب: حتى 20 نقطة/جولة • شاهد إعلاناً لمضاعفة 3x!",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontSize = 10.sp)
                            )
                        }
                        Text(
                            text = "إعلان ترويجي 📢",
                            style = MaterialTheme.typography.labelSmall.copy(color = NeonCyanLight, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                        )
                    }
                }

                // Embedded High-Performance HTML5 WebView
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF0D0F14))
                ) {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { ctx ->
                            WebView(ctx).apply {
                                webViewInstance = this
                                setLayerType(View.LAYER_TYPE_HARDWARE, null)
                                settings.apply {
                                    javaScriptEnabled = true
                                    domStorageEnabled = true
                                    mediaPlaybackRequiresUserGesture = false
                                    cacheMode = WebSettings.LOAD_DEFAULT
                                    loadWithOverviewMode = true
                                    useWideViewPort = true
                                    builtInZoomControls = false
                                    displayZoomControls = false
                                }
                                webChromeClient = WebChromeClient()
                                webViewClient = WebViewClient()

                                // Bridge Javascript to Kotlin Anti-Abuse Handler
                                addJavascriptInterface(
                                    AndroidGameBridge { submission ->
                                        pendingSubmission = submission
                                        showPostGameClaimSheet = true
                                    },
                                    "AndroidGameBridge"
                                )

                                loadUrl(game.assetUrl)
                            }
                        }
                    )
                }
            }

            // Post-Game Rewards & Ad Multiplier Overlay
            AnimatedVisibility(
                visible = showPostGameClaimSheet && pendingSubmission != null,
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                pendingSubmission?.let { sub ->
                    val calculatedBasePoints = when {
                        sub.rawScore < 500 -> 0
                        sub.rawScore < 1000 -> 5
                        sub.rawScore < 2000 -> 10
                        else -> 20
                    }

                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        backgroundColor = ObsidianCardElevated,
                        borderColor = NeonGold
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🏆 نتيجة جولة اللعب",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = NeonGoldLight,
                                    fontSize = 18.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "السكور المحقق: %,d نقطة (المدة: %d ثانية)".format(sub.rawScore, sub.durationSeconds),
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Points Breakdown Card
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = ObsidianBg,
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("نقاط اللعب المستحقة:", color = TextSecondary, fontSize = 12.sp)
                                        Text(
                                            text = "+$calculatedBasePoints نقطة",
                                            color = NeonGoldLight,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("الحد اليومي المتبقي للألعاب:", color = TextSecondary, fontSize = 12.sp)
                                        Text(
                                            text = "${(dailyPointsCap - dailyPointsEarned).coerceAtLeast(0)} نقطة",
                                            color = NeonGreenLight,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // 1. Rewarded Ad Multiplier Button (Major Source of Revenue)
                            Button(
                                onClick = {
                                    onWatchAdForMultiplier(sub)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = NeonCyan,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.PlayCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (calculatedBasePoints > 0)
                                        "شاهد إعلان لمضاعفة النقاط 3x! (+${calculatedBasePoints * 3} نقطة) 🎬"
                                    else
                                        "شاهد إعلان للحصول على +50 نقطة فورية! 🎬",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // 2. Standard Claim Button (Without Ad)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        onSubmitGameSession(sub, false)
                                        showPostGameClaimSheet = false
                                        pendingSubmission = null
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonGoldLight)
                                ) {
                                    Text(
                                        text = "استلام النقاط العادية (+$calculatedBasePoints)",
                                        color = NeonGoldLight,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }

                                TextButton(
                                    onClick = {
                                        showPostGameClaimSheet = false
                                        pendingSubmission = null
                                        webViewInstance?.reload()
                                    },
                                    modifier = Modifier.height(44.dp)
                                ) {
                                    Text("إعادة اللعب 🔄", color = TextSecondary, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Watching Ad Simulation Overlay
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
                                text = "سيتم مضاعفة مكافأة اللعبة خلال: $adCountdown ثواني",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = NeonGoldLight)
                            )
                        }
                    },
                    confirmButton = {}
                )
            }
        }
    }
}
