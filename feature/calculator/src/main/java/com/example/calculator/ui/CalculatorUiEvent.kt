package com.example.calculator.ui

// Sealed class para representar todos os eventos de UI que o ViewModel pode enviar.
sealed class CalculatorUiEvent {
    data class ShowToast(val message: String) : CalculatorUiEvent()

    data class ShowHistoryDialog(val history: List<String>) : CalculatorUiEvent()
}