package com.example.calculator.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.calculator.util.Event
import java.text.DecimalFormat

class CalculatorViewModel : ViewModel() {

    private var currentInput: String = "0"
    private var operand: Double? = null
    private var pendingOp: String? = null
    private var isAfterEquals = false
    private var historyList = ArrayList<String>()
    private var memoryValue: Double? = null

    // 2. LiveData para a UI observar (Fragment irá escutar essas mudanças)
    private val _display = MutableLiveData<String>("0")
    val display: LiveData<String> = _display

    private val _operationDisplay = MutableLiveData<String>("")
    val operationDisplay: LiveData<String> = _operationDisplay

    private val _toastEvent = MutableLiveData<Event<String>>()
    val toastEvent: LiveData<Event<String>> = _toastEvent

    // LiveData para o estado dos botões de memória
    private val _memoryButtonsEnabled = MutableLiveData<Boolean>(false)
    val memoryButtonsEnabled: LiveData<Boolean> = _memoryButtonsEnabled

    private val _uiEvent = MutableLiveData<Event<CalculatorUiEvent>>()
    val uiEvent: LiveData<Event<CalculatorUiEvent>> = _uiEvent

    // UI EVENTS

    fun onDigitPressed(digit: String, maxLength: Int) {
        if (currentInput.length >= maxLength) {
            _toastEvent.value = Event("Limite de $maxLength dígitos atingido")
            return
        }

        if (isAfterEquals) {
            clearAll()
        }
        isAfterEquals = false

        if (digit == "." && currentInput.contains(".")) return
        currentInput = if (currentInput == "0" && digit != ".") digit else currentInput + digit
        updateDisplay()
    }

    fun onOperatorPressed(op: String) {
        if (isAfterEquals) {
            operand = currentInput.toDoubleOrNull()
            isAfterEquals = false
        }

        if (currentInput.isNotEmpty()) {
            val value = currentInput.toDoubleOrNull() ?: return
            operand = if (operand == null) value else performOperation(operand!!, value, pendingOp)
        }
        pendingOp = op
        currentInput = ""
        updateDisplay()
    }

    fun onEqualsPressed() {
        val op1 = operand
        val op = pendingOp
        if (op1 != null && op != null && currentInput.isNotEmpty()) {
            val op2 = currentInput.toDoubleOrNull() ?: return
            val result = performOperation(op1, op2, op)

            val expression = "${formatDouble(op1)} $op ${formatDouble(op2)} ="
            val resultString = formatDouble(result)
            historyList.add("$expression $resultString")

            _operationDisplay.value = expression
            _display.value = resultString

            currentInput = resultString
            operand = null
            pendingOp = null
            isAfterEquals = true
        }
    }

    fun onMemoryOperation(memOp: String) {
        val displayValue = _display.value?.toDoubleOrNull() ?: return

        when (memOp) {
            "MS" -> memoryValue = displayValue
            "MC" -> memoryValue = null
            "MR" -> {
                memoryValue?.let {
                    currentInput = formatDouble(it)
                    operand = null
                    pendingOp = null
                    updateDisplay()
                }
            }
            "M+" -> {
                memoryValue = (memoryValue ?: 0.0) + displayValue
                isAfterEquals = true
            }
            "M-" -> {
                memoryValue = (memoryValue ?: 0.0) - displayValue
                isAfterEquals = true
            }
        }
        _memoryButtonsEnabled.value = memoryValue != null
    }


    fun toggleSign() {
        if(currentInput.isNotEmpty()) {
            isAfterEquals = false

            val number = currentInput.toDoubleOrNull()
            if(number != null && number != 0.0) {
                currentInput = formatDouble(number * -1)
                updateDisplay()
            }
        }
    }
    fun clearEntry() {
        currentInput = "0"
        isAfterEquals = false
        updateDisplay()
    }

    private fun formatDouble(number: Double): String {
        return if(number % 1.0 == 0.0) {
            number.toLong().toString()
        } else {
            DecimalFormat("0.##########").format(number)
        }
    }

    private fun updateDisplay(rotate: Boolean = false, expression: String = "") {
        if (isAfterEquals && !rotate) return

        _display.value = if (currentInput.isNotEmpty()) {
            currentInput.toDoubleOrNull()?.let { formatDouble(it) } ?: currentInput
        } else {
            operand?.let { formatDouble(it) } ?: "0"
        }

        val operationText = buildString {
            if (operand != null) {
                append(formatDouble(operand!!))
            }
            if (pendingOp != null) {
                append(" $pendingOp ")
            }
        }
        _operationDisplay.value = if (expression.isNotEmpty()) expression else operationText
    }

    fun clearAll() {
        currentInput = ""
        operand = null
        pendingOp = null
        isAfterEquals = false
        updateDisplay()
    }

    fun backspace() {
        if (currentInput.isNotEmpty()) {
            currentInput = currentInput.dropLast(1)
            updateDisplay()
        }
    }

    private fun performOperation(a: Double,b: Double, op: String?): Double {
        return when (op) {
            "+" -> a + b
            "-" -> a - b
            "×" -> a * b
            "÷" -> if (b == 0.0) {
                _toastEvent.value = Event("Não é possível dividir por zero.")
                a
            } else a / b
            else -> b
        }
    }

    private fun performUnaryOperation(symbol: String, operation: (Double) -> Double) {
        if (currentInput.isNotEmpty()) {
            val value = currentInput.toDoubleOrNull() ?: return

            val result = operation(value)

            val expression = "$symbol(${formatDouble(value)})"
            val resultString = formatDouble(result)

            historyList.add("$expression = $resultString")

            _operationDisplay.value = expression
            _display.value = resultString

            currentInput = resultString
            operand = null
            pendingOp = null
            isAfterEquals = true
        }
    }

    fun onHistoryPressed(){
        if(historyList.isEmpty()) {
            _uiEvent.value = Event(CalculatorUiEvent.ShowToast("O histórico está vazio."))
        } else {
            _uiEvent.value = Event(CalculatorUiEvent.ShowHistoryDialog(historyList.reversed()))
        }
    }

}