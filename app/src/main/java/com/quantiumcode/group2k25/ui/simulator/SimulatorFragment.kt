package com.quantiumcode.group2k25.ui.simulator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.quantiumcode.group2k25.App
import com.quantiumcode.group2k25.R
import com.quantiumcode.group2k25.databinding.FragmentSimulatorBinding
import com.quantiumcode.group2k25.util.CurrencyFormatter
import com.quantiumcode.group2k25.util.Result
import com.quantiumcode.group2k25.util.applyTopInsets
import com.quantiumcode.group2k25.util.gone
import com.quantiumcode.group2k25.util.visible

class SimulatorFragment : Fragment() {

    private var _binding: FragmentSimulatorBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SimulatorViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSimulatorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.applyTopInsets()
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        val app = requireActivity().application as App
        viewModel = ViewModelProvider(this, SimulatorViewModelFactory(app.container.simulatorRepository))
            .get(SimulatorViewModel::class.java)

        binding.btnSimulate.setOnClickListener {
            val earningsText = binding.etEarnings.text.toString().replace(",", ".")
            val earnings = earningsText.toDoubleOrNull()
            if (earnings == null || earnings <= 0) {
                binding.tilEarnings.error = "Informe um valor válido"
                return@setOnClickListener
            }
            binding.tilEarnings.error = null
            viewModel.simulate(earnings)
        }

        viewModel.simulatorResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progress.visible()
                    binding.cardResult.gone()
                    binding.btnSimulate.isEnabled = false
                    binding.btnSimulate.alpha = 0.4f
                }
                is Result.Success -> {
                    binding.progress.gone()
                    binding.cardResult.visible()
                    binding.btnSimulate.isEnabled = true
                    binding.btnSimulate.alpha = 1f
                    val data = result.data
                    if (data.qualified) {
                        binding.tvResultTitle.text = getString(R.string.simulator_qualified)
                        binding.tvResultTitle.setTextColor(resources.getColor(R.color.status_active, null))
                        binding.tvResultAmount.visible()
                        binding.tvResultAmount.text = CurrencyFormatter.formatCurrency(data.approvedAmount ?: 0.0)
                    } else {
                        binding.tvResultTitle.text = getString(R.string.simulator_not_qualified)
                        binding.tvResultTitle.setTextColor(resources.getColor(R.color.warning, null))
                        binding.tvResultAmount.gone()
                    }
                    binding.tvResultMessage.text = data.message
                }
                is Result.Error -> {
                    binding.progress.gone()
                    binding.btnSimulate.isEnabled = true
                    binding.btnSimulate.alpha = 1f
                    binding.tilEarnings.error = result.message
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
