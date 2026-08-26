package com.example.data.repository

import com.example.data.datasource.LocalMockDataSource
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

interface ITournamentRepository {
    val tournamentsFlow: Flow<List<Tournament>>
    suspend fun getTournamentById(id: String): Tournament?
    suspend fun registerForTournament(tournamentId: String, userId: String, username: String, gameId: String, teamName: String?): Result<Tournament>
    suspend fun submitMatchResult(tournamentId: String, matchId: String, team1Score: Int, team2Score: Int, screenshotUrl: String?): Result<TournamentMatch>
    suspend fun disputeMatch(tournamentId: String, matchId: String, reason: String): Result<TournamentMatch>
    suspend fun filterTournaments(gameType: GameType?, status: TournamentStatus?, searchQuery: String?): List<Tournament>
}

class TournamentRepository : ITournamentRepository {
    private val _tournaments = MutableStateFlow<List<Tournament>>(LocalMockDataSource.initialTournaments)
    override val tournamentsFlow: Flow<List<Tournament>> = _tournaments.asStateFlow()

    override suspend fun getTournamentById(id: String): Tournament? {
        return _tournaments.value.find { it.id == id }
    }

    override suspend fun registerForTournament(
        tournamentId: String,
        userId: String,
        username: String,
        gameId: String,
        teamName: String?
    ): Result<Tournament> {
        val currentList = _tournaments.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == tournamentId }
        if (index == -1) {
            return Result.failure(Exception("البطولة غير موجودة"))
        }

        val tournament = currentList[index]
        if (tournament.isFull) {
            return Result.failure(Exception("عذراً، المقاعد اكتملت في هذه البطولة"))
        }

        if (tournament.registeredPlayers.any { it.userId == userId }) {
            return Result.failure(Exception("أنت مسجل بالفعل في هذه البطولة"))
        }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val newRegistration = RegisteredPlayer(
            userId = userId,
            username = username,
            gameId = gameId,
            registeredAt = dateFormat.format(Date()),
            teamName = teamName
        )

        val updatedTournament = tournament.copy(
            registeredCount = tournament.registeredCount + 1,
            registeredPlayers = tournament.registeredPlayers + newRegistration
        )

        currentList[index] = updatedTournament
        _tournaments.value = currentList
        return Result.success(updatedTournament)
    }

    override suspend fun submitMatchResult(
        tournamentId: String,
        matchId: String,
        team1Score: Int,
        team2Score: Int,
        screenshotUrl: String?
    ): Result<TournamentMatch> {
        val currentList = _tournaments.value.toMutableList()
        val tIdx = currentList.indexOfFirst { it.id == tournamentId }
        if (tIdx == -1) return Result.failure(Exception("البطولة غير موجودة"))

        val tournament = currentList[tIdx]
        val matches = tournament.matches.toMutableList()
        val mIdx = matches.indexOfFirst { it.id == matchId }
        if (mIdx == -1) return Result.failure(Exception("المباراة غير موجودة"))

        val match = matches[mIdx]
        val winner = if (team1Score > team2Score) match.team1Name else if (team2Score > team1Score) match.team2Name else "تعادل"
        val updatedMatch = match.copy(
            team1Score = team1Score,
            team2Score = team2Score,
            isCompleted = false,
            winnerName = winner,
            submissionStatus = MatchSubmissionStatus.SUBMITTED_PENDING_REVIEW,
            screenshotProofUrl = screenshotUrl ?: "screenshot_proof_darfur.jpg"
        )
        matches[mIdx] = updatedMatch
        currentList[tIdx] = tournament.copy(matches = matches)
        _tournaments.value = currentList
        return Result.success(updatedMatch)
    }

    override suspend fun disputeMatch(
        tournamentId: String,
        matchId: String,
        reason: String
    ): Result<TournamentMatch> {
        val currentList = _tournaments.value.toMutableList()
        val tIdx = currentList.indexOfFirst { it.id == tournamentId }
        if (tIdx == -1) return Result.failure(Exception("البطولة غير موجودة"))

        val tournament = currentList[tIdx]
        val matches = tournament.matches.toMutableList()
        val mIdx = matches.indexOfFirst { it.id == matchId }
        if (mIdx == -1) return Result.failure(Exception("المباراة غير موجودة"))

        val match = matches[mIdx]
        val updatedMatch = match.copy(
            submissionStatus = MatchSubmissionStatus.DISPUTED,
            disputeReason = reason
        )
        matches[mIdx] = updatedMatch
        currentList[tIdx] = tournament.copy(matches = matches)
        _tournaments.value = currentList
        return Result.success(updatedMatch)
    }

    override suspend fun filterTournaments(
        gameType: GameType?,
        status: TournamentStatus?,
        searchQuery: String?
    ): List<Tournament> {
        return _tournaments.value.filter { tourney ->
            val matchGame = gameType == null || gameType == GameType.ALL || tourney.gameType == gameType
            val matchStatus = status == null || tourney.status == status
            val matchQuery = searchQuery.isNullOrBlank() ||
                    tourney.title.contains(searchQuery, ignoreCase = true) ||
                    tourney.description.contains(searchQuery, ignoreCase = true)
            matchGame && matchStatus && matchQuery
        }
    }
}
