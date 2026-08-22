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
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.GlassCard
import com.example.ui.components.NeonButton
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*

@Composable
fun CommunityScreen(
    currentUser: User,
    posts: List<CommunityPost>,
    playerSearchResults: List<PlayerSearchResult>,
    onLikePost: (String) -> Unit,
    onAddComment: (String, String) -> Unit,
    onCreatePost: (String, GameType?) -> Unit,
    onReportPost: (String, String) -> Unit,
    onBlockUser: (String) -> Unit,
    onSearchPlayers: (String) -> Unit,
    onInvitePlayerToTeam: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Feed (منشورات المجتمع), 1: Search Players (البحث عن لاعبين)
    var showCreatePostDialog by remember { mutableStateOf(false) }
    var selectedPostForComments by remember { mutableStateOf<CommunityPost?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var postToReport by remember { mutableStateOf<CommunityPost?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
    ) {
        // Community Top Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(ObsidianSurface)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "مجتمع دارفور للألعاب 💬",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = TextPrimary,
                            fontSize = 20.sp
                        )
                    )
                    Text(
                        text = "تواصل مع لاعبي نيالا ودارفور وكون فريقك",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 12.sp)
                    )
                }

                IconButton(
                    onClick = { showCreatePostDialog = true },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(NeonGold)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "إنشاء منشور",
                        tint = TextOnAccent
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = ObsidianCard,
                contentColor = NeonGold,
                divider = {}
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("منشورات اللاعبين", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("البحث عن لاعبين وفرق", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                )
            }
        }

        // Tab 0: Feed
        if (selectedTab == 0) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 0.dp, top = 8.dp, end = 0.dp, bottom = 90.dp)
            ) {
                items(posts) { post ->
                    CommunityPostCard(
                        post = post,
                        onLike = { onLikePost(post.id) },
                        onCommentClick = { selectedPostForComments = post },
                        onReportClick = { postToReport = post },
                        onBlockUser = { onBlockUser(post.authorId) }
                    )
                }
            }
        }

        // Tab 1: Search Players & Teams
        if (selectedTab == 1) {
            Column(modifier = Modifier.fillMaxSize()) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        onSearchPlayers(it)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    placeholder = { Text("ابحث بالاسم، المعرف، أو المدينة...", fontSize = 12.sp, color = TextMuted) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = ObsidianCard,
                        unfocusedContainerColor = ObsidianCard,
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = ObsidianBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 90.dp, top = 0.dp)
                ) {
                    items(playerSearchResults) { player ->
                        PlayerSearchItemCard(
                            player = player,
                            onInvite = { onInvitePlayerToTeam(player.username) }
                        )
                    }
                }
            }
        }
    }

    // Create Post Dialog
    if (showCreatePostDialog) {
        CreatePostDialog(
            onDismiss = { showCreatePostDialog = false },
            onSubmit = { content, gameTag ->
                showCreatePostDialog = false
                onCreatePost(content, gameTag)
            }
        )
    }

    // Comments Bottom Sheet / Dialog
    if (selectedPostForComments != null) {
        CommentsSheet(
            post = selectedPostForComments!!,
            currentUser = currentUser,
            onDismiss = { selectedPostForComments = null },
            onAddComment = { content ->
                onAddComment(selectedPostForComments!!.id, content)
            }
        )
    }

    // Report Post Dialog
    if (postToReport != null) {
        ReportPostDialog(
            post = postToReport!!,
            onDismiss = { postToReport = null },
            onSubmitReport = { reason ->
                onReportPost(postToReport!!.id, reason)
                postToReport = null
            }
        )
    }
}

@Composable
fun CommunityPostCard(
    post: CommunityPost,
    onLike: () -> Unit,
    onCommentClick: () -> Unit,
    onReportClick: () -> Unit,
    onBlockUser: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        backgroundColor = ObsidianCard,
        borderColor = ObsidianBorder
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Post Author Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(ObsidianCardElevated)
                            .border(1.dp, NeonGoldLight, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = post.authorName.take(1),
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = NeonGoldLight
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = post.authorName,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(NeonGold.copy(alpha = 0.2f))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = post.authorBadge,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = NeonGoldLight,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                        Text(
                            text = "${post.authorLocation} • ${post.timestamp}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                // Options Menu (Report & Block)
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "خيارات", tint = TextSecondary)
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier.background(ObsidianCardElevated)
                    ) {
                        DropdownMenuItem(
                            text = { Text("إبلاغ عن المنشور", color = NeonRed, fontSize = 13.sp) },
                            onClick = {
                                showMenu = false
                                onReportClick()
                            },
                            leadingIcon = { Icon(Icons.Default.Report, contentDescription = null, tint = NeonRed) }
                        )
                        DropdownMenuItem(
                            text = { Text("حظر هذا المستخدم", color = TextSecondary, fontSize = 13.sp) },
                            onClick = {
                                showMenu = false
                                onBlockUser()
                            },
                            leadingIcon = { Icon(Icons.Default.Block, contentDescription = null, tint = TextSecondary) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Post Content
            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextPrimary,
                    lineHeight = 20.sp,
                    fontSize = 14.sp
                )
            )

            // Game Tag if available
            if (post.gameTag != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(post.gameTag.brandColor.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "#${post.gameTag.titleArabic}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = post.gameTag.brandColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = ObsidianBorder.copy(alpha = 0.6f))
            Spacer(modifier = Modifier.height(8.dp))

            // Actions: Like & Comment
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = onLike)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (post.isLikedByMe) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "إعجاب",
                        tint = if (post.isLikedByMe) NeonRed else TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${post.likesCount} إعجاب",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (post.isLikedByMe) NeonRed else TextSecondary,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = onCommentClick)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ChatBubbleOutline,
                        contentDescription = "تعليق",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${post.commentsCount} تعليق",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                    )
                }
            }
        }
    }
}

@Composable
fun PlayerSearchItemCard(
    player: PlayerSearchResult,
    onInvite: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
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
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(player.primaryGame.brandColor.copy(alpha = 0.2f))
                        .border(1.dp, player.primaryGame.brandColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "LV${player.level}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = player.primaryGame.brandColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = player.username,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "${player.location} • ID: ${player.inGameId} • فوز ${player.winRate}%",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp)
                    )
                }
            }

            Button(
                onClick = onInvite,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan.copy(alpha = 0.2f)),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text("دعوة للفريق", color = NeonCyanLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun CreatePostDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, GameType?) -> Unit
) {
    var content by remember { mutableStateOf("") }
    var selectedGameTag by remember { mutableStateOf<GameType?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ObsidianCard,
        title = {
            Text("نشر منشور جديد في المجتمع", fontWeight = FontWeight.Bold, color = TextPrimary)
        },
        text = {
            Column {
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    placeholder = { Text("شارك أخبارك، ابحث عن سكواد، أو تحدى الفرق في نيالا ودارفور...", fontSize = 12.sp, color = TextMuted) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = ObsidianCardElevated,
                        unfocusedContainerColor = ObsidianCardElevated,
                        focusedBorderColor = NeonGold,
                        unfocusedBorderColor = ObsidianBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text("وسم اللعبة (اختياري):", color = TextSecondary, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    item {
                        FilterChip(
                            selected = selectedGameTag == null,
                            onClick = { selectedGameTag = null },
                            label = { Text("بدون وسم", fontSize = 11.sp) }
                        )
                    }
                    items(listOf(GameType.FREE_FIRE, GameType.PUBG_MOBILE, GameType.EFOOTBALL)) { game ->
                        FilterChip(
                            selected = selectedGameTag == game,
                            onClick = { selectedGameTag = game },
                            label = { Text(game.titleArabic, fontSize = 11.sp) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(content, selectedGameTag) },
                enabled = content.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = NeonGold, contentColor = TextOnAccent)
            ) {
                Text("نشر الآن", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = TextSecondary)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsSheet(
    post: CommunityPost,
    currentUser: User,
    onDismiss: () -> Unit,
    onAddComment: (String) -> Unit
) {
    var newCommentText by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = ObsidianSurface,
        dragHandle = { BottomSheetDefaults.DragHandle(color = ObsidianBorder) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "التعليقات (${post.commentsCount})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
            )
            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
            ) {
                if (post.comments.isEmpty()) {
                    item {
                        Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                            Text("لا توجد تعليقات بعد. كن أول من يعلق!", color = TextMuted, fontSize = 12.sp)
                        }
                    }
                } else {
                    items(post.comments) { comment ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(ObsidianCardElevated)
                                .padding(10.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = comment.authorName,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = NeonCyanLight
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = comment.timestamp,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = TextMuted,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = comment.content,
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Add comment input
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newCommentText,
                    onValueChange = { newCommentText = it },
                    placeholder = { Text("اكتب تعليقك هنا...", fontSize = 12.sp, color = TextMuted) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = ObsidianCard,
                        unfocusedContainerColor = ObsidianCard,
                        focusedBorderColor = NeonGold,
                        unfocusedBorderColor = ObsidianBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (newCommentText.isNotBlank()) {
                            onAddComment(newCommentText)
                            newCommentText = ""
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(NeonGold)
                ) {
                    Icon(Icons.Default.Send, contentDescription = "إرسال", tint = TextOnAccent)
                }
            }
        }
    }
}

@Composable
fun ReportPostDialog(
    post: CommunityPost,
    onDismiss: () -> Unit,
    onSubmitReport: (String) -> Unit
) {
    var reason by remember { mutableStateOf("محتوى غير لائق") }
    val reasons = listOf("محتوى غير لائق", "سب أو إساءة", "احتيال أو روابط مشبوهة", "غش في الألعاب")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ObsidianCard,
        title = { Text("إبلاغ عن المنشور", fontWeight = FontWeight.Bold, color = TextPrimary) },
        text = {
            Column {
                Text("اختر سبب البلاغ:", color = TextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))
                reasons.forEach { r ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { reason = r }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = reason == r, onClick = { reason = r })
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = r, color = TextPrimary, fontSize = 13.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmitReport(reason) },
                colors = ButtonDefaults.buttonColors(containerColor = NeonRed, contentColor = Color.White)
            ) {
                Text("إرسال البلاغ", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء", color = TextSecondary) }
        }
    )
}
