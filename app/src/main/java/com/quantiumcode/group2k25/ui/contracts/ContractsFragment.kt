package com.quantiumcode.group2k25.ui.contracts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.quantiumcode.group2k25.App
import com.quantiumcode.group2k25.R
import com.quantiumcode.group2k25.databinding.FragmentContractsBinding
import com.quantiumcode.group2k25.ui.contracts.adapter.ContractAdapter
import com.quantiumcode.group2k25.util.Result
import com.quantiumcode.group2k25.util.applyTopInsets
import com.quantiumcode.group2k25.util.gone
import com.quantiumcode.group2k25.util.visible

class ContractsFragment : Fragment() {

    private var _binding: FragmentContractsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ContractsViewModel
    private lateinit var adapter: ContractAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentContractsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvContracts.applyTopInsets()

        val app = requireActivity().application as App
        viewModel = ViewModelProvider(this, ContractsViewModelFactory(app.container.contractRepository))
            .get(ContractsViewModel::class.java)

        adapter = ContractAdapter { contract ->
            val bundle = bundleOf("contractId" to contract.id)
            findNavController().navigate(R.id.action_contracts_to_detail, bundle)
        }
        binding.rvContracts.layoutManager = LinearLayoutManager(requireContext())
        binding.rvContracts.adapter = adapter

        binding.swipeRefresh.setOnRefreshListener { viewModel.loadContracts() }

        viewModel.contractsResult.observe(viewLifecycleOwner) { result ->
            binding.swipeRefresh.isRefreshing = false
            when (result) {
                is Result.Loading -> {
                    binding.progress.visible()
                    binding.emptyState.gone()
                }
                is Result.Success -> {
                    binding.progress.gone()
                    if (result.data.isEmpty()) {
                        binding.emptyState.visible()
                        binding.rvContracts.gone()
                    } else {
                        binding.emptyState.gone()
                        binding.rvContracts.visible()
                        adapter.submitList(result.data)
                    }
                }
                is Result.Error -> {
                    binding.progress.gone()
                    binding.emptyState.visible()
                }
            }
        }

        viewModel.loadContracts()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
