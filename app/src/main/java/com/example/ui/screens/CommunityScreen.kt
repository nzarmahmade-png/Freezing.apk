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
import com.example.ui.theme.*

@Composable
fun CommunityScreen(
    currentUser: User,
    posts: List<CommunityPost>,
    playerSearchResults: List<PlayerSearchResult>,
    onLikePost: (String) -> Unit,
    onFollowUser: (String) -> Unit,
    onAddComment: (String, String) -> Unit,
    onCreatePost: (String, GameType?) -> Unit,
    onReportPost: (String, String) -> Unit,
    onBlockUser: (String) -> Unit,
    onSearchPlayers: (String) -> Unit,
    onSearchPosts: (String) -> Unit,
    onInvitePlayerToTeam: (String) -> Unit,
    onOpenDirectChat: (String, String, GameType) -> Unit,
    onOpenGeneralChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Feed (منشورات المجتمع), 1: Search Players & Teams
    var showCreatePostDialog by remember { mutableStateOf(false) }
    var selectedPostForComments by remember { mutableStateOf<CommunityPost?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var feedFilterTag by remember { mutableStateOf<String?>(null) }
    var postToReport by remember { mutableStateOf<CommunityPost?>(null) }

    val hashtags = listOf("الكل", "#بطولة_دارفور", "#فري_فاير", "#ببجي_موبايل", "#إي_فوتبول", "#نيالا", "#ذئاب_نيالا")

    val displayedPosts = remember(posts, feedFilterTag, searchQuery) {
        posts.filter { post ->
            val matchesTag = if (feedFilterTag == null || feedFilterTag == "الكل") true
            else post.hashtags.contains(feedFilterTag) || (post.gameTag?.titleArabic?.let { "#$it" } == feedFilterTag)
            val matchesSearch = if (searchQuery.isBlank() || selectedTab != 0) true
            else post.content.contains(searchQuery, ignoreCase = true) || post.authorName.contains(searchQuery, ignoreCase = true)
            matchesTag && matchesSearch
        }
    }

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
                        text = "المجتمع الموحد لجميع الألعاب واللاعبين في دارفور",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 12.sp)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Open Messages / Chat FAB
                    IconButton(
                        onClick = onOpenGeneralChat,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(ObsidianCardElevated)
                            .border(1.dp, NeonCyan.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = "الرسائل الخاصة",
                            tint = NeonCyanLight,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Create Post Button
                    IconButton(
                        onClick = { showCreatePostDialog = true },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(NeonCyan)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "إنشاء منشور",
                            tint = Color.Black,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = ObsidianCard,
                contentColor = NeonCyan,
                divider = {}
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("منشورات اللاعبين", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                        onSearchPlayers(searchQuery)
                    },
                    text = { Text("البحث عن لاعبين وفرق", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                )
            }
        }

        // Tab 0: Feed
        if (selectedTab == 0) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 8.dp, bottom = 90.dp)
            ) {
                // Hashtags & Search Row
                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("ابحث في المنشورات والهاشتاجات...", color = TextMuted, fontSize = 11.sp) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
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

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(hashtags) { tag ->
                                val isSelected = (feedFilterTag == tag) || (feedFilterTag == null && tag == "الكل")
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) NeonCyan else ObsidianCard,
                                    modifier = Modifier.clickable {
                                        feedFilterTag = if (tag == "الكل") null else tag
                                    }
                                ) {
                                    Text(
                                        text = tag,
                                        color = if (isSelected) Color.Black else TextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                items(displayedPosts) { post ->
                    CommunityPostCard(
                        post = post,
                        onLike = { onLikePost(post.id) },
                        onFollow = { onFollowUser(post.authorId) },
                        onMessage = {
                            onOpenDirectChat(
                                post.authorId,
                                post.authorName,
                                post.gameTag ?: GameType.FREE_FIRE
                            )
                        },
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
                    placeholder = { Text("ابحث باسم اللاعب، المعرف (UID)، أو المدينة...", fontSize = 12.sp, color = TextMuted) },
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
                            onInvite = { onInvitePlayerToTeam(player.username) },
                            onFollow = { onFollowUser(player.id) },
                            onMessage = { onOpenDirectChat(player.id, player.username, player.primaryGame) }
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
    onFollow: () -> Unit,
    onMessage: () -> Unit,
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
        backgroundColor = if (post.isPinnedByAdmin) ObsidianCardElevated else ObsidianCard,
        borderColor = if (post.isPinnedByAdmin) NeonGold.copy(alpha = 0.6f) else if (post.isAdvertisement) NeonCyan.copy(alpha = 0.5f) else ObsidianBorder
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Pinned Banner or Ad Banner
            if (post.isPinnedByAdmin) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.PushPin, contentDescription = null, tint = NeonGoldLight, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "منشور مثبت من إدارة المنصة الرسمية 🛡️",
                        style = MaterialTheme.typography.labelSmall.copy(color = NeonGoldLight, fontWeight = FontWeight.Bold)
                    )
                }
            } else if (post.isAdvertisement) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(NeonCyan.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("إعلان مميز 📢", color = NeonCyanLight, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                    Text("شريك منصة عازم", color = TextMuted, fontSize = 9.sp)
                }
            }

            // Post Author Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(ObsidianCardElevated)
                            .border(1.dp, if (post.isPinnedByAdmin) NeonGoldLight else NeonCyanLight, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = post.authorName.take(1),
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (post.isPinnedByAdmin) NeonGoldLight else NeonCyanLight
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
                                    .background(if (post.isPinnedByAdmin) NeonGold.copy(alpha = 0.2f) else NeonCyan.copy(alpha = 0.15f))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = post.authorBadge,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (post.isPinnedByAdmin) NeonGoldLight else NeonCyanLight,
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

                // Follow / Message / Options Buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!post.isAdvertisement && !post.isPinnedByAdmin) {
                        IconButton(onClick = onMessage, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Send, contentDescription = "مراسلة خاصة", tint = NeonCyanLight, modifier = Modifier.size(16.dp))
                        }

                        TextButton(
                            onClick = onFollow,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (post.isFollowingAuthor) "متابع ✓" else "+ متابعة",
                                color = if (post.isFollowingAuthor) TextMuted else NeonCyanLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Box {
                        IconButton(onClick = { showMenu = true }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.MoreVert, contentDescription = "خيارات", tint = TextSecondary, modifier = Modifier.size(18.dp))
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
    onInvite: () -> Unit,
    onFollow: () -> Unit,
    onMessage: () -> Unit
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
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
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

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = player.fullName,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "@${player.username} • UID: ${player.inGameId}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = NeonCyanLight,
                            fontSize = 11.sp
                        )
                    )
                    Text(
                        text = "📍 ${player.location} • نسبة الفوز: %.1f%%".format(player.winRate),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onMessage, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Send, contentDescription = "مراسلة", tint = NeonCyanLight, modifier = Modifier.size(16.dp))
                }

                TextButton(onClick = onFollow) {
                    Text(
                        text = if (player.isFollowing) "متابع ✓" else "+ متابعة",
                        color = if (player.isFollowing) TextMuted else NeonCyanLight,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(onClick = onInvite, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.GroupAdd, contentDescription = "دعوة للفريق", tint = NeonGoldLight, modifier = Modifier.size(18.dp))
                }
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
    var selectedGame by remember { mutableStateOf<GameType?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ObsidianCard,
        title = {
            Text(
                text = "نشر في مجتمع دارفور ✍️",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    placeholder = { Text("ماذا يدور في ذهنك؟ ابحث عن سكواد، شارك نتائجك، أو تحدى الفرق...", color = TextMuted, fontSize = 12.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = ObsidianBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))
                Text("حدد اللعبة المرتبطة (اختياري):", color = TextSecondary, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(listOf(GameType.FREE_FIRE, GameType.PUBG_MOBILE, GameType.EFOOTBALL)) { game ->
                        val isSelected = selectedGame == game
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSelected) game.brandColor else ObsidianCardElevated,
                            modifier = Modifier.clickable {
                                selectedGame = if (isSelected) null else game
                            }
                        ) {
                            Text(
                                text = game.titleArabic,
                                color = if (isSelected) Color.White else TextPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(content, selectedGame) },
                enabled = content.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black)
            ) {
                Text("نشر الآن", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء", color = TextSecondary) }
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
    var commentText by remember { mutableStateOf("") }

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
                text = "التعليقات (${post.commentsCount}) 💬",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
            )
            Spacer(modifier = Modifier.height(10.dp))

            if (post.comments.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(30.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("لا توجد تعليقات حتى الآن. كن أول من يعلق!", color = TextMuted, fontSize = 12.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .heightIn(max = 280.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(post.comments) { comment ->
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            backgroundColor = ObsidianCard,
                            borderColor = ObsidianBorder
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = comment.authorName,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = NeonCyanLight)
                                    )
                                    Text(
                                        text = comment.timestamp,
                                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 9.sp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = comment.content,
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary, fontSize = 12.sp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Add comment input
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    placeholder = { Text("اكتب تعليقك...", fontSize = 11.sp, color = TextMuted) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = ObsidianBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (commentText.isNotBlank()) {
                            onAddComment(commentText)
                            commentText = ""
                        }
                    },
                    enabled = commentText.isNotBlank(),
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (commentText.isNotBlank()) NeonCyan else ObsidianCardElevated)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "إرسال",
                        tint = if (commentText.isNotBlank()) Color.Black else TextMuted
                    )
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
    var selectedReason by remember { mutableStateOf("محتوى غير لائق أو مسيء") }
    val reasons = listOf(
        "محتوى غير لائق أو مسيء",
        "احتيال أو ترويج لمواقع هكر",
        "سبام وإعلانات مزعجة",
        "انتحال شخصية لاعب أو كلان"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ObsidianCard,
        title = {
            Text("إبلاغ عن المنشور", fontWeight = FontWeight.Bold, color = TextPrimary)
        },
        text = {
            Column {
                Text("حدد سبب الإبلاغ للمشرفين:", color = TextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))
                reasons.forEach { reason ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedReason = reason }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedReason == reason,
                            onClick = { selectedReason = reason },
                            colors = RadioButtonDefaults.colors(selectedColor = NeonRed)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(reason, color = TextPrimary, fontSize = 12.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmitReport(selectedReason) },
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
