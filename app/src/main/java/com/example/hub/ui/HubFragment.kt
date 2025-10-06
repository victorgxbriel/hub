package com.example.hub.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.hub.R
import com.example.hub.databinding.FragmentHubBinding

class HubFragment : Fragment() {

    private var _binding: FragmentHubBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHubBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val hubAdapter = HubAdapter { appItem ->
            findNavController().navigate(appItem.navigationAction)
        }

        binding.recyclerViewApps.adapter = hubAdapter

        val appList = listOf(
            AppItem(
                name = "Calculadora",
                iconRes = R.drawable.ic_hub_calculator,
                navigationAction = R.id.action_hubFragment_to_calculatorFragment
            ),
            AppItem(
                name = "Placar",
                iconRes = R.drawable.ic_hub_scoreboard,
                navigationAction = R.id.action_hubFragment_to_scoreboardFragment
            ),
            AppItem(
                name = "Cronometro",
                iconRes = R.drawable.ic_hub_timer,
                navigationAction = R.id.action_hubFragment_to_timerFragment
            )
        )

        hubAdapter.submitList(appList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}