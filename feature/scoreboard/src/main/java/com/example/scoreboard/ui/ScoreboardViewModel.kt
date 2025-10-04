package com.example.scoreboard.ui

import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ScoreboardViewModel : ViewModel() {

    private var scoreTeamA = 0
    private var scoreTeamB = 0

    // --- LIVEDATA (O que a UI vai observar) ---
    private val _scoreTeamA_LD = MutableLiveData<Int>(0)
    val scoreTeamA_LD: LiveData<Int> = _scoreTeamA_LD

    private val _scoreTeamB_LD = MutableLiveData<Int>(0)
    val scoreTeamB_LD: LiveData<Int> = _scoreTeamB_LD

    // O ViewModel também decide a COR, pois é parte da lógica do placar.
    private val _scoreColorTeamA_LD = MutableLiveData<Int>(Color.BLACK)
    val scoreColorTeamA_LD: LiveData<Int> = _scoreColorTeamA_LD

    private val _scoreColorTeamB_LD = MutableLiveData<Int>(Color.BLACK)
    val scoreColorTeamB_LD: LiveData<Int> = _scoreColorTeamB_LD


    // --- AÇÕES (O que a UI vai chamar) ---

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

    // --- LÓGICA INTERNA ---

    private fun updateScores() {
        // Atualiza os LiveData com os novos valores de pontuação.
        _scoreTeamA_LD.value = scoreTeamA
        _scoreTeamB_LD.value = scoreTeamB

        // A lógica de cor agora vive aqui, totalmente separada da UI.
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