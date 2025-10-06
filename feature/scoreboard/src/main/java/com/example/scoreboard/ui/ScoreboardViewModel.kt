package com.example.scoreboard.ui

import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.scoreboard.data.Team
import com.example.scoreboard.data.TeamRepository

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

    private val _scoreTeamA_LD = MutableLiveData<Int>(0)
    val scoreTeamA_LD: LiveData<Int> = _scoreTeamA_LD

    private val _scoreTeamB_LD = MutableLiveData<Int>(0)
    val scoreTeamB_LD: LiveData<Int> = _scoreTeamB_LD

    private val _scoreColorTeamA_LD = MutableLiveData<Int>(Color.BLACK)
    val scoreColorTeamA_LD: LiveData<Int> = _scoreColorTeamA_LD

    private val _scoreColorTeamB_LD = MutableLiveData<Int>(Color.BLACK)
    val scoreColorTeamB_LD: LiveData<Int> = _scoreColorTeamB_LD


    fun onTeamSelected(teamIdentifier: String, teamName: String) {
        val team = teams.find { it.name == teamName }

        if (teamIdentifier == "A") {
            _selectedTeamA.value = team
        } else {
            _selectedTeamB.value = team
        }

        _gameControlsVisible.value = _selectedTeamA.value != null && _selectedTeamB.value != null
        onResetPressed()
    }


    fun onAddPointsForTeam(points: Int, team: String) {
        if (team == "A") {
            scoreTeamA += points
        } else {
            scoreTeamB += points
        }
        updateScores()
    }

    fun onResetPressed() {
        scoreTeamA = 0
        scoreTeamB = 0
        updateScores()
    }


    private fun updateScores() {
        _scoreTeamA_LD.value = scoreTeamA
        _scoreTeamB_LD.value = scoreTeamB

        when {
            scoreTeamA > scoreTeamB -> {
                _scoreColorTeamA_LD.value = Color.BLUE
                _scoreColorTeamB_LD.value = Color.RED
            }
            scoreTeamB > scoreTeamA -> {
                _scoreColorTeamB_LD.value = Color.BLUE
                _scoreColorTeamA_LD.value = Color.RED
            }
            else -> {
                _scoreColorTeamA_LD.value = Color.BLACK
                _scoreColorTeamB_LD.value = Color.BLACK
            }
        }
    }

}