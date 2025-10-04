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

class CalculatorFragment : Fragment() {

    private var _binding: FragmentCalculatorBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CalculatorViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalculatorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        viewModel.display.observe(viewLifecycleOwner) { displayValue ->
            binding.txtResultado.text = displayValue
        }

        viewModel.operationDisplay.observe(viewLifecycleOwner) { operationValue ->
            binding.txtOperacao.text = operationValue
        }

        viewModel.toastEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { message ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.memoryButtonsEnabled.observe(viewLifecycleOwner) { isEnabled ->
            binding.btnMC?.isEnabled = isEnabled
            binding.btnMR?.isEnabled = isEnabled
        }

        viewModel.uiEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { uiEvent ->
                handleUiEvent(uiEvent)
            }
        }
    }

    private fun setupClickListeners() {
        val digits = listOf(
            binding.btn0, binding.btn1, binding.btn2, binding.btn3, binding.btn4,
            binding.btn5, binding.btn6, binding.btn7, binding.btn8, binding.btn9, binding.btnPonto
        )

        digits.forEach { button ->
            button.setOnClickListener {
                val maxLength = 11
                viewModel.onDigitPressed((it as Button).text.toString(), maxLength)
            }
        }

        binding.btnSomar.setOnClickListener { viewModel.onOperatorPressed("+") }
        binding.btnSubtrair.setOnClickListener { viewModel.onOperatorPressed("-") }
        binding.btnMultiplicar.setOnClickListener { viewModel.onOperatorPressed("×") }
        binding.btnDividir.setOnClickListener { viewModel.onOperatorPressed("÷") }

        binding.btnIgual.setOnClickListener { viewModel.onEqualsPressed() }
        binding.btnClear.setOnClickListener { viewModel.clearAll() }
        binding.btnClearEntry.setOnClickListener { viewModel.clearEntry() }
        binding.btnBackspace.setOnClickListener { viewModel.backspace() }
        binding.btnMaisMenos.setOnClickListener { viewModel.toggleSign() }

        binding.btnHistory.setOnClickListener { viewModel.onHistoryPressed() }

        binding.btnMC?.setOnClickListener { viewModel.onMemoryOperation("MC") }
        binding.btnMR?.setOnClickListener { viewModel.onMemoryOperation("MR") }
        binding.btnMS?.setOnClickListener { viewModel.onMemoryOperation("MS") }
        binding.btnMAdd?.setOnClickListener { viewModel.onMemoryOperation("M+") }
        binding.btnMSub?.setOnClickListener { viewModel.onMemoryOperation("M-") }

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
        _binding = null
    }
}