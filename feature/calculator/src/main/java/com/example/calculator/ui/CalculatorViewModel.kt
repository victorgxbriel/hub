package com.example.calculator.ui

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.calculator.util.Event
import com.example.core.util.AppLogger
import java.text.DecimalFormat

class CalculatorViewModel : ViewModel() {

    private var currentInput: String = "0"
    private var operand: Double? = null
    private var pendingOp: String? = null
    private var isAfterEquals = false
    private var historyList = ArrayList<String>()
    private var memoryValue: Double? = null

    private val logger = AppLogger(CalculatorViewModel::class.simpleName!!)

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
        logger.debug("onDigitPressed chamado. Digito: '$digit', maxLenght: $maxLength")
        if (currentInput.length >= maxLength) {
            logger.warn("Tamanho máximo($maxLength) atingido. Entrada ignorada")
            _toastEvent.value = Event("Limite de $maxLength dígitos atingido")
            return
        }

        if (isAfterEquals) {
            logger.verbose("Entrada após igual. Limpando estado.")
            clearAll()
        }
        isAfterEquals = false

        if (digit == "." && currentInput.contains(".")) {
            logger.debug("Ponto decimal já está inserido. Entrada ignorada")
            return
        }
        currentInput = if (currentInput == "0" && digit != ".") digit else currentInput + digit
        logger.verbose("Entrada atual atualizada: '$currentInput'")
        updateDisplay()
    }

    fun onOperatorPressed(op: String) {
        logger.debug("onOperatorPressed chamado. Operador: '$op'")
        if (isAfterEquals) {
            logger.verbose("Operador após 'igual'. Definindo o operando da entrada atual.")
            operand = currentInput.toDoubleOrNull()
            isAfterEquals = false
        }

        if (currentInput.isNotEmpty()) {
            val value = currentInput.toDoubleOrNull()
            if( value == null ) {
                logger.error("Número invalido no currentInput: '$currentInput'. Operador ignorado.")
                return
            }
            operand = if (operand == null) {
                logger.verbose("Primeiro operando setado: '$value")
                value
            } else performOperation(operand!!, value, pendingOp)
        }
        pendingOp = op
        currentInput = ""
        logger.info("Operador '$op' setado. Aguardando próximo operando.")
        updateDisplay()
    }

    fun onEqualsPressed() {
        logger.debug("onEqualsPressed chamado.")
        val op1 = operand
        val op = pendingOp
        if (op1 != null && op != null && currentInput.isNotEmpty()) {
            val op2 = currentInput.toDoubleOrNull()
            if( op2 == null ) {
                logger.error("Número invalido em currentInput: '$currentInput'. Igual ignorado.")
                return
            }
            logger.verbose("Calculando: ${formatDouble(op1)} $op ${formatDouble(op2)}")
            val result = performOperation(op1, op2, op)

            val expression = "${formatDouble(op1)} $op ${formatDouble(op2)} ="
            val resultString = formatDouble(result)
            historyList.add("$expression $resultString")
            logger.info("Calculo completo: $expression $resultString. Adicionado ao histórico.")

            _operationDisplay.value = expression
            _display.value = resultString

            currentInput = resultString
            operand = null
            pendingOp = null
            isAfterEquals = true
            logger.debug("Estado resetado após o igual.")
        } else {
            logger.warn("Igual acionado porém operandos or operação faltando. Estado: operando=$op1, pendingOp=$op, currentInput=$currentInput")
        }
    }

    fun onMemoryOperation(memOp: String) {
        logger.debug("onMemoryOperation chamado. Operação: '$memOp'")
        val displayValue = _display.value?.toDoubleOrNull()
        if( displayValue == null ) {
            logger.warn("Operação de memoria '$memOp' requer um displayValue valido, porém pegou '${_display.value}'. Ignorado")
            return
        }

        when (memOp) {
            "MS" ->  {
                memoryValue = displayValue
                logger.info("MS: Valor guardado $memoryValue")
            }
            "MC" -> {
                memoryValue = null
                logger.info("MC: Mémoria limpa.")
            }
            "MR" -> {
                memoryValue?.let {
                    logger.info("MR: Retornando valor $it")
                    currentInput = formatDouble(it)
                    operand = null
                    pendingOp = null
                    updateDisplay()
                } ?: logger.warn("MR acionado porém a memoria está vazia.")
            }
            "M+" -> {
                memoryValue = (memoryValue ?: 0.0) + displayValue
                isAfterEquals = true
                logger.info("M+: Adicionado $displayValue. Novo valor da memoria: $memoryValue")
            }
            "M-" -> {
                memoryValue = (memoryValue ?: 0.0) - displayValue
                isAfterEquals = true
                logger.info("M-: Subtraido $displayValue. Novo valor da memoria: $memoryValue")
            }
        }
        _memoryButtonsEnabled.value = memoryValue != null
        logger.debug("Estado de habilitação dos Botões 'M' atualizado: ${_memoryButtonsEnabled.value}")
    }


    fun toggleSign() {
        logger.debug("toggleSin chamado. currentInput: '$currentInput'")
        if(currentInput.isNotEmpty()) {
            isAfterEquals = false

            val number = currentInput.toDoubleOrNull()
            if(number != null && number != 0.0) {
                currentInput = formatDouble(number * -1)
                logger.verbose("Sinal trocado. Nova entrada: $currentInput")
                updateDisplay()
            } else {
                logger.warn("Não foi possivel fazer o parser do currentInput '$currentInput' para Double para efetuar o toggleSign.")
            }
        } else {
            logger.debug("toggleSign ignorado(entrada vazia ou zero).")
        }
    }
    fun clearEntry() {
        logger.info("clearEntry chamado.")
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
        logger.verbose("updateDisplay chamado.")
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
        logger.info("clearAll chamado.")
        currentInput = ""
        operand = null
        pendingOp = null
        isAfterEquals = false
        updateDisplay()
    }

    fun backspace() {
        logger.debug("backspace chamado. currentInput: '$currentInput'")
        if (currentInput.isNotEmpty()) {
            currentInput = currentInput.dropLast(1)
            logger.verbose("backspace aplicado. Nova entrada: '$currentInput'")
            updateDisplay()
        } else {
            logger.debug("backspace ignorado(entrada vazia)")
        }
    }

    private fun performOperation(a: Double,b: Double, op: String?): Double {
        logger.debug("performOperation chamado: $a $b $op")
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

    fun onSquare() {
        logger.debug("onSquare chamado.")
        performUnaryOperation("sqr") { it * it }
    }

    fun onReverse() {
        logger.debug("onReverse chamado.")
        performUnaryOperation("1/") {
            if (it == 0.0) {
                _toastEvent.value = Event("Não é possível dividir por zero.")
                it
            } else {
                1 / it
            }
        }
    }

    private fun performUnaryOperation(symbol: String, operation: (Double) -> Double) {
        logger.debug("performeUnaryOperation chamado. Simbolo: '$symbol'")
        if (currentInput.isNotEmpty()) {
            val value = currentInput.toDoubleOrNull()
            if( value == null ) {
                logger.error("Entrada invalida '$currentInput' para operação unaria '$symbol'.")
                return
            }

            val result = operation(value)

            val expression = "$symbol(${formatDouble(value)})"
            val resultString = formatDouble(result)

            historyList.add("$expression = $resultString")
            logger.info("Operação unaria '$symbol' completa: $expression = $resultString. Adicionado ao histórico")

            _operationDisplay.value = expression
            _display.value = resultString

            currentInput = resultString
            operand = null
            pendingOp = null
            isAfterEquals = true
        } else {
            logger.warn("Operação unaria '$symbol' ignorada, entrada vazia.")
        }
    }

    fun onHistoryPressed(){
        logger.debug("onHistoryPressed chamado.")
        if(historyList.isEmpty()) {
            logger.info("Histórico está vazio. Enviando evento de ShowToast")
            _uiEvent.value = Event(CalculatorUiEvent.ShowToast("O histórico está vazio."))
        } else {
            logger.info("Historico chamado. Enviando o dialog com ${historyList.size} itens.")
            _uiEvent.value = Event(CalculatorUiEvent.ShowHistoryDialog(historyList.reversed()))
        }
    }

}