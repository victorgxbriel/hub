package com.example.scoreboard.ui

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.AttrRes
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.scoreboard.data.Team
import com.example.scoreboard.databinding.FragmentScoreboardBinding
import com.example.core.R
import com.example.core.util.AppLogger

class ScoreboardFragment : Fragment() {

    private var _binding: FragmentScoreboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ScoreboardViewModel by viewModels()
    private val logger = AppLogger(ScoreboardFragment::class.simpleName!!)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        logger.debug("onCreateView chamado.")
        _binding = FragmentScoreboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logger.debug("onViewCreated chamado.")

        setupObservers()
        setupClickListeners()
        setupSpinners()
    }

    private fun setupSpinners() {
        logger.debug("Configurando Spinners.")
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
                logger.debug("Spinner A: Item selecionado '$selectedName'")
                if (selectedName != "Time A") {
                    viewModel.onTeamSelected("A", selectedName)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                logger.warn("Spinner A: Nada selecionado(onNothingSelected)")
            }
        }

        binding.spinnerTimeB.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                val selectedName = parent.getItemAtPosition(pos).toString()
                logger.debug("Spinner B: Item selecionado '$selectedName'")
                if (selectedName != "Time B") {
                    viewModel.onTeamSelected("B", selectedName)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                logger.warn("Spinner B: Nada selecionado(onNothingSelected)")
            }
        }
    }

    private fun setupClickListeners() {
        logger.debug("Configurando click listeners")
        // Time A
        binding.tresPontosA.setOnClickListener { logger.debug("Botão +3 A clicado"); viewModel.onAddPointsForTeam(3, "A") }
        binding.doisPontosA.setOnClickListener { logger.debug("Botão +2 A clicado"); viewModel.onAddPointsForTeam(2, "A") }
        binding.tiroLivreA.setOnClickListener { logger.debug("Botão +1 A clicado"); viewModel.onAddPointsForTeam(1, "A") }

        // Time B
        binding.tresPontosB.setOnClickListener { logger.debug("Botão +3 B clicado"); viewModel.onAddPointsForTeam(3, "B") }
        binding.doisPontosB.setOnClickListener { logger.debug("Botão +2 B clicado"); viewModel.onAddPointsForTeam(2, "B") }
        binding.tiroLivreB.setOnClickListener { logger.debug("Botão +1 B clicado"); viewModel.onAddPointsForTeam(1, "B") }

        // Reiniciar
        binding.reiniciarPartida.setOnClickListener { logger.debug("Botão Reiniciar clicado"); viewModel.onResetPressed() }
    }

    private fun setupObservers() {
        logger.debug("Configurando observers")
        // Observa as pontuações
        viewModel.scoreTeamA_LD.observe(viewLifecycleOwner) { score ->
            logger.debug("Observer socreTeamA_Ld recebeu: $score")
            binding.placarTimeA.text = score.toString()
            updateScoreColors()
        }
        viewModel.scoreTeamB_LD.observe(viewLifecycleOwner) { score ->
            logger.debug("Observer scoreTeamB_LD recebeu: $score")
            binding.placarTimeB.text = score.toString()
            updateScoreColors()
        }

        viewModel.selectedTeamA.observe(viewLifecycleOwner) { team ->
            logger.debug("Observer selectedTeamA recebeu: ${team?.name ?: "null"}")
            team?.let {
                val buttons = listOf(binding.tresPontosA, binding.doisPontosA, binding.tiroLivreA)
                aplicarEstiloTime(it, buttons)
            }
        }

        // Observador para o Time B
        viewModel.selectedTeamB.observe(viewLifecycleOwner) { team ->
            logger.debug("Observer selectedTeamB recebeu: ${team?.name ?: "null"}")
            team?.let {
                val buttons = listOf(binding.tresPontosB, binding.doisPontosB, binding.tiroLivreB)
                aplicarEstiloTime(it, buttons)
            }
        }

        viewModel.gameControlsVisible.observe(viewLifecycleOwner) { isVisible ->
            logger.debug("Observer gameControlsVisible recebeu: $isVisible")
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
        logger.debug("Aplicando estilo para ${team.name}. Buscando drawable: $resourceName")
        val resId = resources.getIdentifier(resourceName, "drawable", requireContext().packageName)
        val textColor = team.textColor

        if (resId != 0) {
            logger.verbose("Drawable '$resourceName' encontrado (ID: $resId). Aplicando...")
            buttons.forEach { button ->
                button.setBackgroundResource(resId)
                button.setTextColor(textColor)
            }
        } else {
            logger.warn("Drawable '$resourceName' NÃO encontrado! Botões não serão estilizados.")
            // Fallback caso o drawable não seja encontrado
            buttons.forEach { button ->
                // Aplica um estilo padrão para não quebrar a UI
            }
        }
    }

    private fun updateScoreColors() {
        logger.verbose("updateScoreColors chamado.")
        val scoreA = viewModel.scoreTeamA_LD.value ?: 0
        val scoreB = viewModel.scoreTeamB_LD.value ?: 0

        val winningColor = ContextCompat.getColor(requireContext(), R.color.score_winning_color)
        val losingColor = ContextCompat.getColor(requireContext(), R.color.score_losing_color)

        val tiedColor = requireContext().getColorFromAttr(com.google.android.material.R.attr.colorOnBackground)

        when {
            scoreA > scoreB -> {
                binding.placarTimeA.setTextColor(winningColor)
                binding.placarTimeB.setTextColor(losingColor)
            }
            scoreB > scoreA -> {
                binding.placarTimeB.setTextColor(winningColor)
                binding.placarTimeA.setTextColor(losingColor)
            }
            else -> {
                binding.placarTimeA.setTextColor(tiedColor)
                binding.placarTimeB.setTextColor(tiedColor)
            }

        }
    }

    private fun Context.getColorFromAttr(
        @AttrRes attrRes: Int
    ): Int {
        val typedValue = TypedValue()
        theme.resolveAttribute(attrRes, typedValue, true)
        return typedValue.data
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        logger.debug("onDestroyView chamado")
    }

}