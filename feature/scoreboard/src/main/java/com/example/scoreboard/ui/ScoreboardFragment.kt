package com.example.scoreboard.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}