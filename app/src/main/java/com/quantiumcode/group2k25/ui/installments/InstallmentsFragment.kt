package com.quantiumcode.group2k25.ui.installments

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
import com.quantiumcode.group2k25.databinding.FragmentInstallmentsBinding
import com.quantiumcode.group2k25.ui.installments.adapter.InstallmentListAdapter
import com.quantiumcode.group2k25.util.CurrencyFormatter
import com.quantiumcode.group2k25.util.Result
import com.quantiumcode.group2k25.util.applyTopInsets
import com.quantiumcode.group2k25.util.gone
import com.quantiumcode.group2k25.util.visible

class InstallmentsFragment : Fragment() {

    private var _binding: FragmentInstallmentsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: InstallmentsViewModel
    private lateinit var adapter: InstallmentListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentInstallmentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.contentContainer.applyTopInsets()

        val app = requireActivity().application as App
        viewModel = ViewModelProvider(this, InstallmentsViewModelFactory(app.container.installmentRepository))
            .get(InstallmentsViewModel::class.java)

        adapter = InstallmentListAdapter { installment ->
            val bundle = bundleOf("installmentId" to installment.id)
            findNavController().navigate(R.id.action_installments_to_payment, bundle)
        }
        binding.rvInstallments.layoutManager = LinearLayoutManager(requireContext())
        binding.rvInstallments.adapter = adapter

        binding.swipeRefresh.setOnRefreshListener { viewModel.loadInstallments() }

        binding.chipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            val status = when {
                checkedIds.contains(R.id.chip_pending) -> "pending"
                checkedIds.contains(R.id.chip_overdue) -> "overdue"
                checkedIds.contains(R.id.chip_paid) -> "paid"
                else -> null
            }
            viewModel.setFilter(status)
        }

        viewModel.installmentsResult.observe(viewLifecycleOwner) { result ->
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
                        binding.rvInstallments.gone()
                    } else {
                        binding.emptyState.gone()
                        binding.rvInstallments.visible()
                        adapter.submitList(result.data)
                    }
                    updateSummary(result.data)
                }
                is Result.Error -> {
                    binding.progress.gone()
                    binding.emptyState.visible()
                }
            }
        }

        viewModel.loadInstallments()
    }

    private fun updateSummary(installments: List<com.quantiumcode.group2k25.data.api.models.InstallmentInfo>) {
        val pendingOrOverdue = installments.filter { it.status == "PENDENTE" || it.status == "ATRASADO" }
        val totalDue = pendingOrOverdue.sumOf { it.amount }
        val overdueCount = installments.count { it.status == "ATRASADO" }
        binding.tvTotalDue.text = CurrencyFormatter.formatCurrency(totalDue)
        binding.tvOverdueCount.text = overdueCount.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
