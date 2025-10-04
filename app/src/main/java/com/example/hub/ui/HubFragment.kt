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

    // Declara a variável de binding. O underscore indica que é privada para esta classe.
    private var _binding: FragmentHubBinding? = null
    // Esta propriedade é apenas um getter e remove a necessidade de usar '?' toda vez.
    // Garante que o binding só seja acessado quando a view do fragment estiver viva.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Infla o layout usando View Binding e o atribui à nossa variável _binding.
        _binding = FragmentHubBinding.inflate(inflater, container, false)
        // Retorna a view raiz do nosso layout.
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Aqui configuramos os listeners de clique.
        // O código dentro das chaves será executado quando o botão for clicado.
        binding.buttonCalculator.setOnClickListener {
            // Ação de navegação para a calculadora virá aqui.
            findNavController().navigate(R.id.action_hubFragment_to_calculatorFragment)
        }

        binding.buttonScoreboard.setOnClickListener {
            // Ação de navegação para o placar virá aqui.
            findNavController().navigate(R.id.action_hubFragment_to_scoreboardFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Limpa a referência ao binding quando a view do fragment é destruída.
        // Isso é crucial para evitar memory leaks (vazamentos de memória).
        _binding = null
    }

}