package com.example.scoreboard.ui

import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.core.util.AppLogger
import com.example.scoreboard.data.Team
import com.example.scoreboard.data.TeamRepository
import kotlin.math.log

class ScoreboardViewModel : ViewModel() {

    private val teams = TeamRepository.getTeams()

    val teamNames: List<String> = listOf("Time A") + teams.map { it.name }

    private val _selectedTeamA = MutableLiveData<Team?>()
    val selectedTeamA: LiveData<Team?> = _selectedTeamA

    private val _selectedTeamB = MutableLiveData<Team?>()
    val selectedTeamB: LiveData<Team?> = _selectedTeamB

    private val _gameControlsVisible = MutableLiveData<Boolean>(false)
    val gameControlsVisible: LiveData<Boolean> = _gameControlsVisible

    private var scoreTeamA = 0
    private var scoreTeamB = 0
    private val logger = AppLogger(ScoreboardViewModel::class.simpleName!!)

    private val _scoreTeamA_LD = MutableLiveData<Int>(0)
    val scoreTeamA_LD: LiveData<Int> = _scoreTeamA_LD

    private val _scoreTeamB_LD = MutableLiveData<Int>(0)
    val scoreTeamB_LD: LiveData<Int> = _scoreTeamB_LD


    fun onTeamSelected(teamIdentifier: String, teamName: String) {
        logger.debug("onTeamSelected chamado. ID: $teamIdentifier, Nome: $teamName")
        val team = teams.find { it.name == teamName }

        if (teamIdentifier == "A") {
            _selectedTeamA.value = team
            logger.info("Time A definido para: ${team?.name ?: "Nenhum"}")
        } else {
            _selectedTeamB.value = team
            logger.info("Time B definido para: ${team?.name ?: "Nenhum"}")
        }

        _gameControlsVisible.value = _selectedTeamA.value != null && _selectedTeamB.value != null
        logger.debug("Visibilidade dos controles definida para: ${_gameControlsVisible.value}")
        onResetPressed()
    }


    fun onAddPointsForTeam(points: Int, team: String) {
        logger.debug("onAddPointsForTeam chamado. Pontos: $points, Time: $team")
        if (team == "A") {
            scoreTeamA += points
            logger.debug("Pontuação atualizada: $scoreTeamA / time $team")
        } else {
            scoreTeamB += points
            logger.debug("Pontuação atualizada: $scoreTeamB / time $team")
        }
        updateScores()
    }

    fun onResetPressed() {
        logger.info("onResetPressed chamado. Zerando placares.")
        scoreTeamA = 0
        scoreTeamB = 0
        updateScores()
    }


    private fun updateScores() {
        logger.debug("updateScores chamado. A=$scoreTeamA, B=$scoreTeamB")
        _scoreTeamA_LD.value = scoreTeamA
        _scoreTeamB_LD.value = scoreTeamB
        logger.verbose("LiveData _scoreTeamA_LD atualizado para: $scoreTeamA")
        logger.verbose("LiveData _scoreTeamB_LD atualizado para: $scoreTeamB")

    }

}