package com.example.data.datasource

import com.example.data.model.*

object LocalMockDataSource {

    val currentUser = User(
        id = "user_darfur_pro_01",
        username = "Azom_Sniper_SD",
        fullName = "محمد نزار الدرديري",
        email = "azom.gamer@gmail.com",
        phone = "+249 91 234 5678",
        location = "نيالا، حي المطار",
        avatarUrl = "",
        level = 14,
        currentXp = 4250,
        nextLevelXp = 6000,
        rewardPoints = 2450,
        depositBalanceSDG = 35000, // For tournament entrance fees
        tournamentWinningsSDG = 150000, // Approved tournament winnings withdrawable to Bankak
        gameProfiles = mapOf(
            GameType.FREE_FIRE.id to GameIdProfile(GameType.FREE_FIRE, "FF-8849201", "Azom_Sniper_SD", "ماستر (Master)"),
            GameType.PUBG_MOBILE.id to GameIdProfile(GameType.PUBG_MOBILE, "510982341", "Nyala_Ghost_SD", "كراون (Crown I)"),
            GameType.EFOOTBALL.id to GameIdProfile(GameType.EFOOTBALL, "EF-992018", "Darfur_Messi", "القسم الأول (Div 1)")
        ),
        stats = UserStats(
            matchesPlayed = 68,
            matchesWon = 32,
            winRatePercent = 47.1,
            totalTournamentsEntered = 22,
            totalWinningsSDG = 285000,
            mvpCount = 14,
            killsTotal = 582
        ),
        currentTeamId = "team_nyala_wolves",
        currentTeamName = "ذئاب نيالا [NW]",
        achievements = listOf(
            Achievement("ach_1", "بطل نيالا الأول", "الفوز بالمركز الأول في بطولة فري فاير الكبرى", "military_tech", true, "12 أغسطس 2026", 500),
            Achievement("ach_2", "قناص دارفور", "تحقيق أكثر من 20 كيل في بطولة واحدة", "gps_fixed", true, "18 أغسطس 2026", 300),
            Achievement("ach_3", "سيد التكتيك", "الفوز بـ 5 مباريات متتالية دون خسارة", "workspace_premium", true, "20 أغسطس 2026", 400),
            Achievement("ach_4", "ملك بيس 2026", "تسجيل 15 هدف في بطولة إي فوتبول", "sports_soccer", false, null, 250),
            Achievement("ach_5", "مؤسس الكلان", "إنشاء فريق وجمع 5 لاعبين نشطين", "groups", true, "05 أغسطس 2026", 200)
        ),
        seasonRank = "المركز 4 - نخبة دارفور (Tier 1)"
    )

    val supportedGames = listOf(
        Game(
            id = "game_ff",
            type = GameType.FREE_FIRE,
            nameArabic = "فري فاير (Free Fire)",
            nameEnglish = "Garena Free Fire",
            descriptionArabic = "اللعبة الأكثر شعبية في دارفور. بطولات كلاش سكواد وباتل رويال يومية بجوائز نقدية.",
            activeTournamentsCount = 6,
            activePlayersCount = 1420,
            teamsCount = 48,
            popularFormat = "سكواد 4 ضد 4 & فردي",
            tags = listOf("باتل رويال", "كلاش سكواد", "جوائز كاش", "تحديات يومية")
        ),
        Game(
            id = "game_pubg",
            type = GameType.PUBG_MOBILE,
            nameArabic = "ببجي موبايل (PUBG Mobile)",
            nameEnglish = "PUBG Mobile",
            descriptionArabic = "بطولات إرانغل ومستودع TDM لفرق ولاعبي دارفور والسودان بنظام رومات مخصصة وسيرفرات سريعة.",
            activeTournamentsCount = 4,
            activePlayersCount = 980,
            teamsCount = 36,
            popularFormat = "سكواد إرانغل & مستودع TDM",
            tags = listOf("إرانغل", "مستودع TDM", "بطولات أسبوعية", "جوائز كبرى")
        ),
        Game(
            id = "game_efootball",
            type = GameType.EFOOTBALL,
            nameArabic = "إي فوتبول / بيس (eFootball)",
            nameEnglish = "eFootball Mobile / PES",
            descriptionArabic = "دوري أبطال نيالا الإلكتروني لمباريات كرة القدم 1 ضد 1 بنظام خروج المغلوب والمجموعات.",
            activeTournamentsCount = 3,
            activePlayersCount = 640,
            teamsCount = 20,
            popularFormat = "1 ضد 1 - خروج المغلوب",
            tags = listOf("كرة قدم", "دوري المحترفين", "مباريات حاسمة", "جوائز فورية")
        )
    )

    val initialTournaments = listOf(
        Tournament(
            id = "tourn_ff_grand_nyala",
            title = "كأس أبطال نيالا الكبرى - فري فاير سكواد",
            gameType = GameType.FREE_FIRE,
            format = TournamentFormat.SQUAD,
            status = TournamentStatus.REGISTRATION_OPEN,
            entryFeeSDG = 5000,
            entryFeePoints = 500,
            totalPrizePoolSDG = 250000,
            firstPlacePrizeSDG = 150000,
            secondPlacePrizeSDG = 70000,
            thirdPlacePrizeSDG = 30000,
            maxParticipants = 32,
            registeredCount = 24,
            startDateArabic = "الليلة، 22 أغسطس",
            startTimeArabic = "09:00 مساءً",
            serverRegion = "نيالا / جنوب دارفور",
            organizer = "إدارة منصة عازم الرسمية",
            description = "أقوى بطولة كلاش سكواد لفرق دارفور بمشاركة نخبة الفرق. يتم توزيع الروم وكلمة المرور للمسجلين قبل المباراة بـ 15 دقيقة.",
            rules = listOf(
                "يمنع استخدام أي برامج مساعدة أو هكر (حظر فوري ودائم)",
                "تسجيل دخول كامل الفريق قبل موعد المباراة بـ 10 دقائق",
                "المباراة بنظام كلاش سكواد 7 جولات الفائز",
                "يحظر تغيير أسماء اللاعبين بعد إتمام التسجيل",
                "يتم تحويل الجوائز مباشرة إلى محفظة الأرباح بعد اعتماد النتيجة فوراً"
            ),
            customRoomId = "AZOM-FF-994",
            customRoomPassword = "NYALA-CHAMP-2026",
            registeredPlayers = listOf(
                RegisteredPlayer("u_01", "ذئاب نيالا [NW]", "FF-8849201", "2026-08-22 14:00", "ذئاب نيالا"),
                RegisteredPlayer("u_02", "صقور الفاشر [SF]", "FF-1029384", "2026-08-22 15:30", "صقور الفاشر"),
                RegisteredPlayer("u_03", "فرسان الجنينة [FG]", "FF-5544332", "2026-08-22 16:10", "فرسان الجنينة")
            ),
            matches = listOf(
                TournamentMatch("m_1", "ربع النهائي 1", "ذئاب نيالا", "نمور الضعين", 7, 4, "09:00 م", true, "ذئاب نيالا"),
                TournamentMatch("m_2", "ربع النهائي 2", "صقور الفاشر", "فرسان الجنينة", null, null, "09:30 م", false),
                TournamentMatch("m_3", "نصف النهائي", "ذئاب نيالا", "الفائز من م2", null, null, "10:15 م", false),
                TournamentMatch("m_4", "النهائي الكبير", "المتأهل 1", "المتأهل 2", null, null, "11:00 م", false)
            ),
            standings = listOf(
                TournamentStanding(1, "ذئاب نيالا", "NW", 45, 28, 150000),
                TournamentStanding(2, "صقور الفاشر", "SF", 38, 22, 70000),
                TournamentStanding(3, "فرسان الجنينة", "FG", 30, 18, 30000)
            ),
            isFeatured = true
        ),
        Tournament(
            id = "tourn_pubg_erangel_night",
            title = "بطولة درع دارفور - ببجي موبايل إرانغل",
            gameType = GameType.PUBG_MOBILE,
            format = TournamentFormat.SQUAD,
            status = TournamentStatus.REGISTRATION_OPEN,
            entryFeeSDG = 4000,
            entryFeePoints = 400,
            totalPrizePoolSDG = 200000,
            firstPlacePrizeSDG = 120000,
            secondPlacePrizeSDG = 50000,
            thirdPlacePrizeSDG = 30000,
            maxParticipants = 25,
            registeredCount = 19,
            startDateArabic = "غداً، 23 أغسطس",
            startTimeArabic = "08:30 مساءً",
            serverRegion = "غرب السودان - روم مخصص",
            organizer = "رابطة لاعبي ببجي نيالا",
            description = "مواجهة 25 سكواد على خريطة إرانغل الكلاسيكية. النقاط تحتسب على الترتيب وعدد الكيلات مع حكام معتمدين.",
            rules = listOf(
                "ممنوع استخدام المحاكي (الهواتف والأجهزة اللوحية فقط)",
                "الالتزام بالمقعد المخصص لكل سكواد في الروم",
                "نقطة واحدة لكل كيل + نقاط الترتيب المعتمدة دولياً",
                "تسجيل شاشة اللعب إلزامي في حال الشكوى والاعتراض"
            ),
            customRoomId = "PUBG-DARFUR-882",
            customRoomPassword = "DARFUR-STORM-01",
            isFeatured = true
        ),
        Tournament(
            id = "tourn_efootball_cup",
            title = "دوري أبطال نيالا - إي فوتبول بيس",
            gameType = GameType.EFOOTBALL,
            format = TournamentFormat.ONE_VS_ONE,
            status = TournamentStatus.LIVE,
            entryFeeSDG = 2000,
            entryFeePoints = 200,
            totalPrizePoolSDG = 100000,
            firstPlacePrizeSDG = 60000,
            secondPlacePrizeSDG = 30000,
            thirdPlacePrizeSDG = 10000,
            maxParticipants = 16,
            registeredCount = 16,
            startDateArabic = "جارية الآن",
            startTimeArabic = "07:00 مساءً",
            serverRegion = "مباريات ودية أونلاين",
            organizer = "مجتمع بيس دارفور",
            description = "مواجهات حامية بين أقوى 16 لاعباً في دارفور. وقت المباراة 10 دقائق مع أشواط إضافية وركلات ترجيح في حال التعادل.",
            rules = listOf(
                "اتصال إنترنت مستقر (4G / واي فاي قوي)",
                "إرسال لقطة شاشة لنتيجة المباراة فور انتهائها في قروب البطولة",
                "في حال انقطاع الاتصال خلال أول 5 دقائق تعاد المباراة"
            ),
            isFeatured = false
        ),
        Tournament(
            id = "tourn_ff_solo_king",
            title = "تحدي ملك الفردي (Solo King) - فري فاير",
            gameType = GameType.FREE_FIRE,
            format = TournamentFormat.SOLO,
            status = TournamentStatus.UPCOMING,
            entryFeeSDG = 1500,
            entryFeePoints = 150,
            totalPrizePoolSDG = 80000,
            firstPlacePrizeSDG = 50000,
            secondPlacePrizeSDG = 20000,
            thirdPlacePrizeSDG = 10000,
            maxParticipants = 48,
            registeredCount = 38,
            startDateArabic = "السبت، 24 أغسطس",
            startTimeArabic = "10:00 مساءً",
            serverRegion = "نيالا ودارفور",
            organizer = "إدارة عازم",
            description = "تحدي البقاء للأقوى فردياً على خريطة برمودا. كل لاعب يقاتل بمفرده بدون فرق أو مساعدة.",
            rules = listOf(
                "يمنع التحالف تماماً ويعتبر صاحبه خاسراً ومحظوراً",
                "لعب نزيه واحترافي بدون برامج طرف ثالث"
            ),
            isFeatured = false
        )
    )

    val initialTransactions = listOf(
        WalletTransaction(
            id = "tx_win_8819",
            type = TransactionType.TOURNAMENT_WINNING,
            amountSDG = 85000,
            timestamp = "2026-08-21 22:45",
            status = TransactionStatus.COMPLETED,
            paymentMethod = null,
            referenceNumber = "WIN-FF-882193",
            note = "المركز الأول - بطولة كلاش سكواد نيالا الأسبوعية",
            targetTournamentTitle = "بطولة كلاش سكواد الأسبوعية"
        ),
        WalletTransaction(
            id = "tx_dep_7721",
            type = TransactionType.DEPOSIT,
            amountSDG = 20000,
            timestamp = "2026-08-20 16:30",
            status = TransactionStatus.COMPLETED,
            paymentMethod = PaymentMethod.BANKAK,
            referenceNumber = "BNK-983719028",
            note = "إيداع عبر تطبيق بنكك (رقم الحساب: 2490192***)"
        ),
        WalletTransaction(
            id = "tx_fee_6610",
            type = TransactionType.TOURNAMENT_ENTRY_FEE,
            amountSDG = 5000,
            timestamp = "2026-08-20 18:00",
            status = TransactionStatus.COMPLETED,
            paymentMethod = null,
            referenceNumber = "REG-FF-GRAND-01",
            note = "رسوم اشتراك: كأس أبطال نيالا الكبرى",
            targetTournamentTitle = "كأس أبطال نيالا الكبرى"
        ),
        WalletTransaction(
            id = "tx_wth_5512",
            type = TransactionType.WITHDRAWAL,
            amountSDG = 50000,
            timestamp = "2026-08-18 11:20",
            status = TransactionStatus.COMPLETED,
            paymentMethod = PaymentMethod.BANKAK,
            referenceNumber = "WTH-BNK-449102",
            note = "سحب أرباح معتمدة إلى حساب بنكك باسم محمد نزار"
        ),
        WalletTransaction(
            id = "tx_dep_4401",
            type = TransactionType.DEPOSIT,
            amountSDG = 15000,
            timestamp = "2026-08-15 09:10",
            status = TransactionStatus.COMPLETED,
            paymentMethod = PaymentMethod.SUDANI_CASH,
            referenceNumber = "SUD-2819401",
            note = "إيداع عبر سوداني كاش"
        )
    )

    val rewardItems = listOf(
        RewardItem(
            id = "rew_ff_diamonds_500",
            titleArabic = "شحن 530 جوهرة فري فاير (Free Fire)",
            descriptionArabic = "كود شحن فوري لحسابك في لعبة فري فاير عبر المعرف الرسمي.",
            category = RewardCategory.GAME_CARDS,
            pointsCost = 1200,
            estimatedValueSDG = 15000,
            stockCount = 45,
            badgeArabic = "الأكثر طلباً 🔥",
            redemptionInstructions = "سيظهر لك كود الشحن فوراً ويمكن استبداله في موقع Garena الرسمي عبر معرف اللعبة."
        ),
        RewardItem(
            id = "rew_pubg_uc_300",
            titleArabic = "شحن 325 شدة ببجي (PUBG UC)",
            descriptionArabic = "شحن شدات ببجي موبايل فورية لفتح الرويال باس والصناديق المميزة.",
            category = RewardCategory.GAME_CARDS,
            pointsCost = 1500,
            estimatedValueSDG = 18000,
            stockCount = 30,
            badgeArabic = "مميز",
            redemptionInstructions = "كود رقمي فوري يشحن عبر موقع Midasbuy الرسمي بوضع الـ Player ID الخاص بك."
        ),
        RewardItem(
            id = "rew_tournament_gold_pass",
            titleArabic = "تذكرة دخول مجانية لأي بطولة ذهبية",
            descriptionArabic = "تذكرة VIP تتيح لك التسجيل في أي بطولة كبرى في المنصة دون دفع رسوم نقدية.",
            category = RewardCategory.TOURNAMENT_PASS,
            pointsCost = 800,
            estimatedValueSDG = 10000,
            stockCount = 100,
            badgeArabic = "حصري للمنصة",
            redemptionInstructions = "يتم تفعيل التذكرة تلقائياً في حسابك لاستخدامها عند التسجيل في البطولات."
        ),
        RewardItem(
            id = "rew_azom_headset_voucher",
            titleArabic = "قسيمة خصم 40% من متجر عازم للإلكترونيات",
            descriptionArabic = "خصم على سماعات الألعاب واكسسوارات الموبايل من فرع متجر عازم بنيالا.",
            category = RewardCategory.AZOM_STORE,
            pointsCost = 600,
            estimatedValueSDG = 12000,
            stockCount = 20,
            badgeArabic = "متجر عازم 🛍️",
            redemptionInstructions = "أظهر الكود الرقمي لموظف الفرع في نيala - شارع السينما للاستفادة من الخصم."
        ),
        RewardItem(
            id = "rew_efootball_coins",
            titleArabic = "حزمة كوينز إي فوتبول 1000 كوينز",
            descriptionArabic = "كوينز لشراء بطاقات اللاعبين والمدربين في بيس موبايل.",
            category = RewardCategory.GAME_CARDS,
            pointsCost = 2000,
            estimatedValueSDG = 25000,
            stockCount = 15,
            badgeArabic = "عشاق بيس ⚽",
            redemptionInstructions = "يتم إرسال بطاقة الشحن الإلكترونية لاستبدالها داخل متجر اللعبة."
        )
    )

    val defaultEconomyConfig = RewardEconomyConfig(
        scoreTier1Max = 499,
        scoreTier1Points = 0,
        scoreTier2Max = 999,
        scoreTier2Points = 5,
        scoreTier3Max = 1999,
        scoreTier3Points = 10,
        scoreTier4Points = 20,
        dailyGamePointsCap = 60,
        rewardedAdBasePoints = 50,
        rewardedAdMultiplier = 3,
        sessionCooldownSeconds = 30
    )

    val miniGames = listOf(
        MiniGameItem(
            id = "pinball",
            titleArabic = "عازم بينبول السيبراني",
            shortDescriptionArabic = "اضغط لتشغيل المضارب المغناطيسية وحقق أعلى رصيد نقاط وتحدي الكرات الثلاث",
            categoryNameArabic = "ألعاب أركيد ونقر 🕹️",
            assetUrl = "file:///android_asset/games/pinball/index.html",
            maxRewardPoints = 20,
            dailyLimitSessions = 5,
            currentDailySessionsPlayed = 0,
            badgeArabic = "اللعبة المميزة 🔥",
            accentColorHex = "#FFB800"
        ),
        MiniGameItem(
            id = "reflex",
            titleArabic = "تحدي سرعة الاستجابة (Cyber Reflex)",
            shortDescriptionArabic = "اختبر سرعة رد فعلك واضغط على الأهداف الذهبية والنيونية قبل انقضاء 30 ثانية",
            categoryNameArabic = "سرعة وتركيز ⚡",
            assetUrl = "file:///android_asset/games/reflex/index.html",
            maxRewardPoints = 20,
            dailyLimitSessions = 5,
            currentDailySessionsPlayed = 0,
            badgeArabic = "تحدي السرعة ⚡",
            accentColorHex = "#00E5FF"
        ),
        MiniGameItem(
            id = "space_glide",
            titleArabic = "المناور النيوني (Neon Space Glide)",
            shortDescriptionArabic = "حلق بسفينتك النيونية بين البوابات الكهرومغناطيسية واجمع الجواهر لأطول مسافة",
            categoryNameArabic = "مغامرات طيران 🚀",
            assetUrl = "file:///android_asset/games/space_glide/index.html",
            maxRewardPoints = 20,
            dailyLimitSessions = 5,
            currentDailySessionsPlayed = 0,
            badgeArabic = "جديد 🚀",
            accentColorHex = "#10B981"
        )
    )

    val earnOpportunities = listOf(
        EarnOpportunity(
            id = "earn_daily_checkin",
            titleArabic = "تسجيل الدخول اليومي للمنصة",
            descriptionArabic = "افتح التطبيق يومياً واحصل على نقاط مجانية ومكافأة XP لرفع مستواك.",
            pointsReward = 50,
            xpReward = 100,
            actionType = EarnActionType.DAILY_LOGIN,
            isClaimedToday = false
        ),
        EarnOpportunity(
            id = "earn_watch_ad",
            titleArabic = "مشاهدة إعلان داعم للمجتمع",
            descriptionArabic = "شاهد مقطع فيديو إعلاني قصير لدعم بطولات دارفور واكسب نقاط إضافية.",
            pointsReward = 30,
            xpReward = 50,
            actionType = EarnActionType.WATCH_REWARD_AD,
            isClaimedToday = false
        ),
        EarnOpportunity(
            id = "earn_tournament_win",
            titleArabic = "الفوز في مباراة رسمية",
            descriptionArabic = "حقق الانتصار في أي مباراة بطولة لكسب مكافآت نقاط إضافية على كل فوز.",
            pointsReward = 150,
            xpReward = 300,
            actionType = EarnActionType.WIN_TOURNAMENT_MATCH,
            isClaimedToday = false
        ),
        EarnOpportunity(
            id = "earn_invite_friend",
            titleArabic = "دعوة لاعب جديد لمنصة عازم",
            descriptionArabic = "شارك رابط التطبيق مع أصدقائك في نيالا ودارفور واكسب 100 نقطة عند تسجيلهم.",
            pointsReward = 100,
            xpReward = 150,
            actionType = EarnActionType.INVITE_FRIEND,
            isClaimedToday = false
        )
    )

    val communityPosts = listOf(
        CommunityPost(
            id = "post_1",
            authorId = "user_darfur_pro_01",
            authorName = "محمد نزار (Azom_Sniper)",
            authorAvatar = "",
            authorBadge = "بطل الموسم 🏆",
            authorLocation = "نيالا",
            content = "جاهزين لبطولة كلاش سكواد الليلة؟ فريق ذئاب نيالا مستعد لكأس دارفور! منو من الفرق بيتحدى في النهائي؟ 🔥🎮",
            gameTag = GameType.FREE_FIRE,
            timestamp = "منذ ساعة",
            likesCount = 38,
            commentsCount = 12,
            isLikedByMe = true,
            comments = listOf(
                Comment("c_1", "u_02", "أحمد صقر الفاشر", "", "صقور الفاشر جاهزين ونازلين بقوة الليلة! الوعد في النهائي 🦅", "منذ 45 دقيقة", 5),
                Comment("c_2", "u_03", "عثمان الجنينة", "", "بالتوفيق للجميع، تنظيم ممتاز يا إدارة عازم 👏", "منذ 20 دقيقة", 2)
            )
        ),
        CommunityPost(
            id = "post_2",
            authorId = "user_pubg_master",
            authorName = "طارق قوست (Nyala_Ghost)",
            authorAvatar = "",
            authorBadge = "قناص محترف 🎯",
            authorLocation = "نيالا - حي الوادي",
            content = "محتاجين لاعب رابع سكواد لببجي موبايل لبطولة درع دارفور غداً. الشروط: تقييم كراون فما فوق ومايك شغال. تواصلوا معي خاص أو علقوا بالـ ID.",
            gameTag = GameType.PUBG_MOBILE,
            timestamp = "منذ 3 ساعات",
            likesCount = 24,
            commentsCount = 8,
            isLikedByMe = false,
            comments = listOf(
                Comment("c_3", "u_pubg_04", "ياسر الدرع", "", "أنا جاهز يا كابتن! الـ ID: 518829103 - تقييم آيس ومايك جاهز.", "منذ ساعتين", 3)
            )
        ),
        CommunityPost(
            id = "post_3",
            authorId = "user_pes_champ",
            authorName = "إبراهيم ميسي دارفور",
            authorAvatar = "",
            authorBadge = "بطل بيس ⚽",
            authorLocation = "الفاشر",
            content = "تأهلت لنصف نهائي دوري إي فوتبول! مواجهة قوية قادمة في تمام الساعة 8:30 م. دعواتكم يا شباب بالفوز بالكأس 🏆⚽",
            gameTag = GameType.EFOOTBALL,
            timestamp = "منذ 5 ساعات",
            likesCount = 45,
            commentsCount = 15,
            isLikedByMe = true
        )
    )

    val currentTeam = Team(
        id = "team_nyala_wolves",
        name = "ذئاب نيالا (Nyala Wolves)",
        tag = "NW",
        logoEmoji = "🐺",
        primaryGame = GameType.FREE_FIRE,
        leaderId = "user_darfur_pro_01",
        leaderName = "محمد نزار (Azom_Sniper)",
        members = listOf(
            TeamMember("user_darfur_pro_01", "محمد نزار (Azom_Sniper)", "قائد الفريق ومهاجم", "FF-8849201", "01-08-2026", true),
            TeamMember("u_mem_2", "عمر الرهيب (Omar_Shadow)", "قناص أساسي (Sniper)", "FF-7719203", "03-08-2026"),
            TeamMember("u_mem_3", "خالد المدمر (Destroyer_K)", "مساند دفاعي (Support)", "FF-6655441", "05-08-2026"),
            TeamMember("u_mem_4", "سامي الصاروخ (Rocket_Sam)", "مهاجم سريع (Rusher)", "FF-9988772", "10-08-2026")
        ),
        maxMembers = 6,
        bio = "فريق النخبة الممثل لمدينة نيالا في بطولات دارفور والسودان لفري فاير. شعارنا الفوز والانضباط التكتيكي.",
        trophiesCount = 6,
        tournamentsWon = 4,
        matchesPlayed = 34,
        winRate = 70.5,
        region = "نيالا - جنوب دارفور",
        isRecruiting = true
    )

    val currentSeason = Season(
        seasonNumber = 1,
        seasonTitleArabic = "الموسم الأول: عمالقة دارفور 2026",
        seasonThemeArabic = "صيف المنافسات الكبرى في نيالا وإقليم دارفور",
        daysRemaining = 14,
        totalPrizePoolSDG = 1500000,
        currentTierArabic = "الماستر الذهبي (Gold Master)",
        userSeasonXp = 4250,
        maxSeasonXp = 6000,
        rewards = listOf(
            SeasonTierReward("البرونزي", 1000, 200, 10000, "درع البرونز", isClaimed = true, isUnlocked = true),
            SeasonTierReward("الفضي", 2500, 500, 25000, "سيف الفضة", isClaimed = true, isUnlocked = true),
            SeasonTierReward("الذهبي", 4000, 1000, 50000, "تاج الذهب", isClaimed = false, isUnlocked = true),
            SeasonTierReward("البلاتينيوم", 5000, 1500, 80000, "شعلة النخبة", isClaimed = false, isUnlocked = false),
            SeasonTierReward("جراند ماستر دارفور", 6000, 3000, 150000, "كأس أسطورة دارفور", isClaimed = false, isUnlocked = false)
        ),
        topLeaderboard = listOf(
            LeaderboardEntry(1, "u_top_1", "أسد دارفور (Lion_DF)", "نيالا", "جراند ماستر", 5890, 48, 78.4),
            LeaderboardEntry(2, "u_top_2", "شبح الفاشر (Ghost_FAS)", "الفاشر", "جراند ماستر", 5620, 44, 72.1),
            LeaderboardEntry(3, "u_top_3", "قاهر الصحراء (Desert_King)", "الجنينة", "بلاتينيوم", 4980, 39, 65.0),
            LeaderboardEntry(4, "user_darfur_pro_01", "محمد نزار (Azom_Sniper)", "نيالا", "الماستر الذهبي", 4250, 32, 47.1, isCurrentUser = true),
            LeaderboardEntry(5, "u_top_5", "صقر الضعين (Falcon_DA)", "الضعين", "الماستر الذهبي", 4120, 29, 58.0),
            LeaderboardEntry(6, "u_top_6", "نجم زالنجي (Star_ZAL)", "زالنجي", "الماستر الذهبي", 3890, 26, 52.3)
        )
    )

    val notifications = listOf(
        NotificationItem(
            id = "notif_1",
            titleArabic = "🎉 تم اعتماد فوزك بجائزة البطولة!",
            messageArabic = "تهانينا! تم إضافة 85,000 جنيه سوداني لأرباحك المعتمدة بعد فوزك بكأس كلاش سكواد نيالا.",
            type = NotificationType.WINNING,
            timestamp = "منذ 30 دقيقة",
            isRead = false,
            targetId = "tx_win_8819"
        ),
        NotificationItem(
            id = "notif_2",
            titleArabic = "🔑 بيانات غرفة المباراة متاحة الآن",
            messageArabic = "تم فتح روم بطولة 'كأس أبطال نيالا الكبرى'. معرف الروم: AZOM-FF-994 وكلمة المرور: NYALA-CHAMP-2026",
            type = NotificationType.MATCH,
            timestamp = "منذ ساعة",
            isRead = false,
            targetId = "tourn_ff_grand_nyala"
        ),
        NotificationItem(
            id = "notif_3",
            titleArabic = "✅ تم تأكيد إيداع الرصيد بنجاح",
            messageArabic = "تم شحن رصيد الإيداع بمبلغ 20,000 جنيه سوداني عبر تطبيق بنكك. يمكنك استخدامه لدخول البطولات.",
            type = NotificationType.DEPOSIT,
            timestamp = "منذ يومين",
            isRead = true,
            targetId = "tx_dep_7721"
        ),
        NotificationItem(
            id = "notif_4",
            titleArabic = "🎁 مكافأة تسجيل الدخول اليومي جاهزة",
            messageArabic = "ادخل لقسم المكافآت واحصل على 50 نقطة و 100 XP مجاناً اليوم!",
            type = NotificationType.REWARD,
            timestamp = "اليوم، 09:00 ص",
            isRead = false
        ),
        NotificationItem(
            id = "notif_5",
            titleArabic = "📢 انطلاق الموسم الأول لبطولات دارفور",
            messageArabic = "مجموع جوائز الموسم الأول تتجاوز 1,500,000 جنيه سوداني! نافس الآن وارفع ترتيبك.",
            type = NotificationType.ANNOUNCEMENT,
            timestamp = "منذ 3 أيام",
            isRead = true
        )
    )
}
