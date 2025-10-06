package com.example.timer.ui

import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timer.ui.model.LapUiModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class TimerViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    private var running = false
    private var startTime: Long = 0L
    private var elapsedTime: Long = 0L
    private var laps = mutableListOf<Pair<Int, Long>>()
    private var lastLapTotalElapsed: Long = 0L
    private var timerJob: Job? = null

    private val _timerText = MutableLiveData<String>("00:00:00:00")
    val timerText: LiveData<String> = _timerText

    private val _lapsList = MutableLiveData<List<LapUiModel>>()
    val lapsList: LiveData<List<LapUiModel>> = _lapsList
    private val _startStopButtonText = MutableLiveData<String>("Iniciar")
    val startStopButtonText: LiveData<String> = _startStopButtonText
    private val _lapResetButtonText = MutableLiveData<String>("Zerar")
    val lapResetButtonText: LiveData<String> = _lapResetButtonText

    private val _startStopButtonColor = MutableLiveData<Int>(Color.parseColor("#2E7D32"))
    val startStopButtonColor: LiveData<Int> = _startStopButtonColor

    private val _lapResetButtonColor = MutableLiveData<Int>(Color.parseColor("#2962FF"))
    val lapResetButtonColor: LiveData<Int> = _lapResetButtonColor

    fun onStartStopPressed() {
        if (running) {
            stopTimer()
        } else {
            startTimer()
        }
    }

    fun onLapResetPressed() {
        if (running) {
            recordLap()
        } else {
            resetTimer()
        }
    }

    // --- LÓGICA DO CRONÔMETRO COM CORROTINAS ---
    private fun startTimer() {
        running = true
        startTime = System.currentTimeMillis()
        updateButtonState()

        // Se for a primeira vez, já cria a "Volta 1"
        if (laps.isEmpty()) {
            laps.add(0, 1 to 0L)
        }

        // Lança a corrotina que atualiza o tempo
        timerJob?.cancel()
        timerJob = flow {
            while (true) {
                emit(Unit) // Emite um "tick"
                delay(10) // A cada 10 milissegundos
            }
        }.onEach {
            val totalElapsed = elapsedTime + (System.currentTimeMillis() - startTime)
            _timerText.postValue(formatTime(totalElapsed))

            // Atualiza o tempo da volta atual (que está sempre na posição 0)
            val currentLapTime = totalElapsed - lastLapTotalElapsed
            laps[0] = laps[0].copy(second = currentLapTime)
            updateLapsList()

        }.launchIn(viewModelScope)
    }

    private fun stopTimer() {
        running = false
        elapsedTime += System.currentTimeMillis() - startTime
        timerJob?.cancel()
        updateButtonState()
    }

    private fun recordLap() {
        val totalElapsed = elapsedTime + (System.currentTimeMillis() - startTime)
        lastLapTotalElapsed = totalElapsed
        laps.add(0, (laps.size + 1) to 0L)
    }

    private fun resetTimer() {
        stopTimer()
        elapsedTime = 0
        laps.clear()
        lastLapTotalElapsed = 0
        _timerText.value = formatTime(0)
        updateLapsList()
        updateButtonState()
    }

    private fun updateLapsList() {
        if (laps.isEmpty()) {
            _lapsList.value = emptyList()
            return
        }

        val lapTimes = laps.map { it.second }.drop(1)
        val fastestTime = lapTimes.minOrNull()
        val slowestTime = lapTimes.maxOrNull()

        val uiLaps = laps.map { lap ->
            val time = lap.second
            val color = when {
                laps.size <= 2 -> Color.WHITE
                time == fastestTime -> Color.GREEN
                time == slowestTime -> Color.RED
                else -> Color.WHITE
            }
            LapUiModel(
                number = lap.first,
                time = time,
                formattedTime = formatTime(time),
                textColor = color
            )
        }
        _lapsList.value = uiLaps.asReversed()
    }

    private fun updateButtonState() {
        if (running) {
            _startStopButtonText.value = "Parar"
            _startStopButtonColor.value = Color.parseColor("#D32F2F")
            _lapResetButtonText.value = "Volta"
        } else {
            _startStopButtonText.value = "Iniciar"
            _startStopButtonColor.value = Color.parseColor("#2E7D32")
            _lapResetButtonText.value = "Zerar"
        }
        _lapResetButtonColor.value = Color.parseColor("#2962FF")
    }

    private fun formatTime(totalMillis: Long): String {
        val hours = totalMillis / 3600000
        val minutes = (totalMillis % 3600000) / 60000
        val seconds = (totalMillis % 60000) / 1000
        val millis = (totalMillis % 1000) / 10
        return String.format("%02d:%02d:%02d:%02d", hours, minutes, seconds, millis)
    }
}