package com.example.timer.ui

import android.content.res.ColorStateList
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.timer.R
import com.example.timer.databinding.FragmentTimerBinding

class TimerFragment : Fragment() {

    companion object {
        fun newInstance() = TimerFragment()
    }

    private var _binding: FragmentTimerBinding? = null
    private val binding get() = _binding!!
    private lateinit var lapAdapter: LapAdapter

    private val viewModel: TimerViewModel by viewModels()

    /*
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }
     */

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTimerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        lapAdapter = LapAdapter()
        binding.lapsRecyclerView.adapter = lapAdapter
    }

    private fun setupClickListeners() {
        binding.startStopBtn.setOnClickListener { viewModel.onStartStopPressed() }
        binding.lapResetBtn.setOnClickListener { viewModel.onLapResetPressed() }
    }

    private fun setupObservers() {
        viewModel.timerText.observe(viewLifecycleOwner) {
            binding.timerText.text = it
        }
        viewModel.startStopButtonText.observe(viewLifecycleOwner) {
            binding.startStopBtn.text = it
        }
        viewModel.lapsList.observe(viewLifecycleOwner) { laps ->
            lapAdapter.submitList(laps)
        }
        viewModel.lapResetButtonText.observe(viewLifecycleOwner) { text ->
            binding.lapResetBtn.text = text
        }

        viewModel.startStopButtonColor.observe(viewLifecycleOwner) { color ->
            binding.startStopBtn.backgroundTintList = ColorStateList.valueOf(color)
        }

        viewModel.lapResetButtonColor.observe(viewLifecycleOwner) { color ->
            binding.lapResetBtn.backgroundTintList = ColorStateList.valueOf(color)
        }
    }
}