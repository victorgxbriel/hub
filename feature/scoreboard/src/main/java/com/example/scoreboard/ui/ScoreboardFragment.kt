package com.example.scoreboard.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.scoreboard.data.Team
import com.example.scoreboard.databinding.FragmentScoreboardBinding

class ScoreboardFragment : Fragment() {

    private var _binding: FragmentScoreboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ScoreboardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScoreboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupClickListeners()
        setupSpinners()
    }

    private fun setupSpinners() {
        val teamAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            viewModel.teamNames
        )
        teamAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spinnerTimeA.adapter = teamAdapter
        binding.spinnerTimeB.adapter = teamAdapter

        binding.spinnerTimeA.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                val selectedName = parent.getItemAtPosition(pos).toString()
                if (selectedName != "Time A") {
                    viewModel.onTeamSelected("A", selectedName)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.spinnerTimeB.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                val selectedName = parent.getItemAtPosition(pos).toString()
                if (selectedName != "Time B") {
                    viewModel.onTeamSelected("B", selectedName)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupClickListeners() {
        // Time A
        binding.tresPontosA.setOnClickListener { viewModel.onAddPointsForTeam(3, "A") }
        binding.doisPontosA.setOnClickListener { viewModel.onAddPointsForTeam(2, "A") }
        binding.tiroLivreA.setOnClickListener { viewModel.onAddPointsForTeam(1, "A") }

        // Time B
        binding.tresPontosB.setOnClickListener { viewModel.onAddPointsForTeam(3, "B") }
        binding.doisPontosB.setOnClickListener { viewModel.onAddPointsForTeam(2, "B") }
        binding.tiroLivreB.setOnClickListener { viewModel.onAddPointsForTeam(1, "B") }

        // Reiniciar
        binding.reiniciarPartida.setOnClickListener { viewModel.onResetPressed() }
    }

    private fun setupObservers() {
        // Observa as pontuações
        viewModel.scoreTeamA_LD.observe(viewLifecycleOwner) { score ->
            binding.placarTimeA.text = score.toString()
        }
        viewModel.scoreTeamB_LD.observe(viewLifecycleOwner) { score ->
            binding.placarTimeB.text = score.toString()
        }

        // Observa as cores
        viewModel.scoreColorTeamA_LD.observe(viewLifecycleOwner) { color ->
            binding.placarTimeA.setTextColor(color)
        }
        viewModel.scoreColorTeamB_LD.observe(viewLifecycleOwner) { color ->
            binding.placarTimeB.setTextColor(color)
        }

        viewModel.selectedTeamA.observe(viewLifecycleOwner) { team ->
            team?.let {
                val buttons = listOf(binding.tresPontosA, binding.doisPontosA, binding.tiroLivreA)
                aplicarEstiloTime(it, buttons)
            }
        }

        // Observador para o Time B
        viewModel.selectedTeamB.observe(viewLifecycleOwner) { team ->
            team?.let {
                val buttons = listOf(binding.tresPontosB, binding.doisPontosB, binding.tiroLivreB)
                aplicarEstiloTime(it, buttons)
            }
        }

        viewModel.gameControlsVisible.observe(viewLifecycleOwner) { isVisible ->
            val visibility = if (isVisible) View.VISIBLE else View.GONE

            // Cria uma lista de todas as views que devem ser afetadas
            val viewsToToggle = listOf(
                binding.placarTimeA, binding.placarTimeB,
                binding.tresPontosA, binding.doisPontosA, binding.tiroLivreA,
                binding.tresPontosB, binding.doisPontosB, binding.tiroLivreB,
                binding.reiniciarPartida
            )

            // Aplica a visibilidade a todas elas de uma vez
            viewsToToggle.forEach { it.visibility = visibility }
        }
    }

    private fun aplicarEstiloTime(team: Team, buttons: List<AppCompatButton>) {
        val resourceName = "custom_button_${team.name.lowercase().replace(" ", "_")}"
        val resId = resources.getIdentifier(resourceName, "drawable", requireContext().packageName)
        val textColor = team.textColor

        if (resId != 0) {
            buttons.forEach { button ->
                button.setBackgroundResource(resId)
                button.setTextColor(textColor)
            }
        } else {
            // Fallback caso o drawable não seja encontrado
            buttons.forEach { button ->
                // Aplica um estilo padrão para não quebrar a UI
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}