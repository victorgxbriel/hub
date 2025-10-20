package com.example.calculator.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.calculator.databinding.FragmentCalculatorBinding
import com.example.core.util.AppLogger

class CalculatorFragment : Fragment() {

    private var _binding: FragmentCalculatorBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CalculatorViewModel by viewModels()
    private val logger = AppLogger(CalculatorFragment::class.simpleName!!)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        logger.debug("onCreateView chamado.")
        _binding = FragmentCalculatorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logger.debug("onViewCreated chamado.")

        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        logger.debug("Configurando observers.")
        viewModel.display.observe(viewLifecycleOwner) { displayValue ->
            logger.verbose("Observer display recebeu : $displayValue")
            binding.txtResultado.text = displayValue
        }

        viewModel.operationDisplay.observe(viewLifecycleOwner) { operationValue ->
            logger.verbose("Observer operationDisplay recebeu: $operationValue")
            binding.txtOperacao.text = operationValue
        }

        viewModel.toastEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { message ->
                logger.info("Observer toastEvent acionado. Mostrando Toast: '$message'")
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.memoryButtonsEnabled.observe(viewLifecycleOwner) { isEnabled ->
            logger.debug("Observer memoryButtonsEnabled recebeu: $isEnabled")
            binding.btnMC?.isEnabled = isEnabled
            binding.btnMR?.isEnabled = isEnabled
        }

        viewModel.uiEvent.observe(viewLifecycleOwner) { event ->
            logger.debug("Observer uiEvent recebeu: ${event.peekContent()}")
            event.getContentIfNotHandled()?.let { uiEvent ->
                logger.info("UI event: $uiEvent")
                handleUiEvent(uiEvent)
            }
        }
    }

    private fun setupClickListeners() {
        logger.debug("Configurando click Listeners")
        val digits = listOf(
            binding.btn0, binding.btn1, binding.btn2, binding.btn3, binding.btn4,
            binding.btn5, binding.btn6, binding.btn7, binding.btn8, binding.btn9, binding.btnPonto
        )

        digits.forEach { button ->
            button.setOnClickListener {
                logger.debug("Botão '${(it as Button).text.toString()} clicado.")
                val maxLength = 11
                viewModel.onDigitPressed((it as Button).text.toString(), maxLength)
            }
        }

        binding.btnSomar.setOnClickListener { logger.debug("Botão de operação '+' clicked."); viewModel.onOperatorPressed("+") }
        binding.btnSubtrair.setOnClickListener { logger.debug("Botão de operação '-' clicked."); viewModel.onOperatorPressed("-") }
        binding.btnMultiplicar.setOnClickListener { logger.debug("Botão de operação '×' clicked."); viewModel.onOperatorPressed("×") }
        binding.btnDividir.setOnClickListener { logger.debug("Botão de operação '÷' clicked."); viewModel.onOperatorPressed("÷") }

        binding.btnIgual.setOnClickListener { logger.debug("Botão '=' clicado"); viewModel.onEqualsPressed() }
        binding.btnClear.setOnClickListener { logger.debug("Botão 'C' clicado"); viewModel.clearAll() }
        binding.btnClearEntry.setOnClickListener { logger.debug("Botão 'CE' clicado"); viewModel.clearEntry() }
        binding.btnBackspace.setOnClickListener { logger.debug("Botão '⌫' clicado"); viewModel.backspace() }
        binding.btnMaisMenos.setOnClickListener { logger.debug("Botão '±' clicado"); viewModel.toggleSign() }

        binding.btnHistory.setOnClickListener { logger.debug("Botão 'Historico' clicado"); viewModel.onHistoryPressed() }

        binding.btnMC?.setOnClickListener { logger.debug("Botão 'MC' clicado"); viewModel.onMemoryOperation("MC") }
        binding.btnMR?.setOnClickListener { logger.debug("Botão 'MR' clicado"); viewModel.onMemoryOperation("MR") }
        binding.btnMS?.setOnClickListener { logger.debug("Botão 'MS' clicado"); viewModel.onMemoryOperation("MS") }
        binding.btnMAdd?.setOnClickListener { logger.debug("Botão 'M+' clicado"); viewModel.onMemoryOperation("M+") }
        binding.btnMSub?.setOnClickListener { logger.debug("Botão 'M-' clicado"); viewModel.onMemoryOperation("M-") }

        binding.btnSquare?.setOnClickListener { logger.debug("Botão 'sqr' clicado"); viewModel.onSquare() }
        binding.btnReverse?.setOnClickListener { logger.debug("Botão 'reverse' clicado"); viewModel.onReverse() }

    }

    private fun handleUiEvent(uiEvent: CalculatorUiEvent) {
        when (uiEvent) {
            is CalculatorUiEvent.ShowToast -> {
                Toast.makeText(requireContext(), uiEvent.message, Toast.LENGTH_SHORT).show()
            }
            is CalculatorUiEvent.ShowHistoryDialog -> {
                showHistoryDialog(uiEvent.history)
            }
        }
    }

    private fun showHistoryDialog(history: List<String>) {
        logger.debug("Mostrando o dialog de historico com ${history.size} itens.")
        val historyArray = history.toTypedArray()

        AlertDialog.Builder(requireContext())
            .setTitle("Histórico de calculos")
            .setItems(historyArray, null)
            .setPositiveButton("Fechar") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        logger.debug("onDestroyView chamado.")
        _binding = null
    }
}