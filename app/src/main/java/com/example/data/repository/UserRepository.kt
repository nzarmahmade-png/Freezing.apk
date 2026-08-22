package com.example.data.repository

import com.example.data.datasource.LocalMockDataSource
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

interface IUserRepository {
    val currentUserFlow: Flow<User>
    val currentTeamFlow: Flow<Team?>

    suspend fun updateGameId(gameType: GameType, inGameId: String, inGameName: String): Result<Unit>
    suspend fun addXpAndPoints(xp: Int, points: Int)
    suspend fun createTeam(name: String, tag: String, primaryGame: GameType, bio: String): Result<Team>
    suspend fun invitePlayerToTeam(usernameOrId: String): Result<TeamMember>
    suspend fun removeTeamMember(userId: String): Result<Unit>
}

class UserRepository : IUserRepository {
    private val _currentUser = MutableStateFlow<User>(LocalMockDataSource.currentUser)
    private val _currentTeam = MutableStateFlow<Team?>(LocalMockDataSource.currentTeam)

    override val currentUserFlow: Flow<User> = _currentUser.asStateFlow()
    override val currentTeamFlow: Flow<Team?> = _currentTeam.asStateFlow()

    override suspend fun updateGameId(
        gameType: GameType,
        inGameId: String,
        inGameName: String
    ): Result<Unit> {
        val user = _currentUser.value
        val updatedProfiles = user.gameProfiles.toMutableMap()
        updatedProfiles[gameType.id] = GameIdProfile(gameType, inGameId, inGameName)
        _currentUser.value = user.copy(gameProfiles = updatedProfiles)
        return Result.success(Unit)
    }

    override suspend fun addXpAndPoints(xp: Int, points: Int) {
        val user = _currentUser.value
        var newXp = user.currentXp + xp
        var newLevel = user.level
        var nextXp = user.nextLevelXp

        while (newXp >= nextXp) {
            newXp -= nextXp
            newLevel += 1
            nextXp = (nextXp * 1.25).toInt()
        }

        _currentUser.value = user.copy(
            currentXp = newXp,
            level = newLevel,
            nextLevelXp = nextXp,
            rewardPoints = user.rewardPoints + points
        )
    }

    override suspend fun createTeam(
        name: String,
        tag: String,
        primaryGame: GameType,
        bio: String
    ): Result<Team> {
        if (name.isBlank() || tag.isBlank()) {
            return Result.failure(Exception("يرجى ملء اسم الفريق والاختصار"))
        }

        val user = _currentUser.value
        val leaderMember = TeamMember(
            userId = user.id,
            username = user.username,
            roleArabic = "قائد ومؤسس الفريق",
            gameId = user.gameProfiles[primaryGame.id]?.inGameId ?: "ID-P1",
            joinedDate = "الآن",
            isLeader = true
        )

        val newTeam = Team(
            id = "team_${UUID.randomUUID().toString().take(8)}",
            name = name.trim(),
            tag = tag.trim().uppercase(),
            logoEmoji = "⚡",
            primaryGame = primaryGame,
            leaderId = user.id,
            leaderName = user.username,
            members = listOf(leaderMember),
            maxMembers = 6,
            bio = bio.ifBlank { "فريق منافس في بطولات دارفور" },
            trophiesCount = 0,
            tournamentsWon = 0,
            matchesPlayed = 0,
            winRate = 0.0,
            region = user.location
        )

        _currentTeam.value = newTeam
        _currentUser.value = user.copy(
            currentTeamId = newTeam.id,
            currentTeamName = "${newTeam.name} [${newTeam.tag}]"
        )

        return Result.success(newTeam)
    }

    override suspend fun invitePlayerToTeam(usernameOrId: String): Result<TeamMember> {
        val team = _currentTeam.value ?: return Result.failure(Exception("لا يوجد فريق حالياً"))
        if (team.members.size >= team.maxMembers) {
            return Result.failure(Exception("الفريق مكتمل العدد (${team.maxMembers} أعضاء كحد أقصى)"))
        }

        val newMember = TeamMember(
            userId = "u_inv_${UUID.randomUUID().toString().take(6)}",
            username = usernameOrId.trim(),
            roleArabic = "عضو في الفريق",
            gameId = "GAME-${(100000..999999).random()}",
            joinedDate = "الآن"
        )

        val updatedTeam = team.copy(members = team.members + newMember)
        _currentTeam.value = updatedTeam
        return Result.success(newMember)
    }

    override suspend fun removeTeamMember(userId: String): Result<Unit> {
        val team = _currentTeam.value ?: return Result.failure(Exception("لا يوجد فريق"))
        val updatedTeam = team.copy(members = team.members.filter { it.userId != userId })
        _currentTeam.value = updatedTeam
        return Result.success(Unit)
    }
}
