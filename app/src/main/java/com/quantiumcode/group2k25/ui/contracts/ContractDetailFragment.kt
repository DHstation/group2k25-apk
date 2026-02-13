package com.quantiumcode.group2k25.ui.contracts

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.quantiumcode.group2k25.App
import com.quantiumcode.group2k25.R
import com.quantiumcode.group2k25.databinding.FragmentContractDetailBinding
import com.quantiumcode.group2k25.ui.contracts.adapter.InstallmentAdapter
import com.quantiumcode.group2k25.util.CurrencyFormatter
import com.quantiumcode.group2k25.util.Result
import com.quantiumcode.group2k25.util.gone
import com.quantiumcode.group2k25.util.visible

class ContractDetailFragment : Fragment() {

    private var _binding: FragmentContractDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ContractDetailViewModel
    private lateinit var adapter: InstallmentAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentContractDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        val contractId = arguments?.getString("contractId") ?: return
        val app = requireActivity().application as App
        viewModel = ViewModelProvider(this, ContractDetailViewModelFactory(app.container.contractRepository))
            .get(ContractDetailViewModel::class.java)

        adapter = InstallmentAdapter { installment ->
            if (installment.status != "PAGO") {
                val bundle = bundleOf("installmentId" to installment.id)
                findNavController().navigate(R.id.action_detail_to_payment, bundle)
            }
        }
        binding.rvInstallments.layoutManager = LinearLayoutManager(requireContext())
        binding.rvInstallments.adapter = adapter

        binding.swipeRefresh.setOnRefreshListener { viewModel.loadDetail(contractId) }

        viewModel.detailResult.observe(viewLifecycleOwner) { result ->
            binding.swipeRefresh.isRefreshing = false
            when (result) {
                is Result.Loading -> binding.progress.visible()
                is Result.Success -> {
                    binding.progress.gone()
                    val c = result.data.contract
                    binding.tvContractNumber.text = String.format("Contrato #%d", c.number)
                    binding.tvPrincipal.text = CurrencyFormatter.formatCurrency(c.principalAmount)
                    binding.tvTotal.text = CurrencyFormatter.formatCurrency(c.totalAmount)
                    binding.tvInterest.text = String.format("%.1f%%", c.interestRate)
                    binding.tvRemaining.text = CurrencyFormatter.formatCurrency(c.remainingAmount)

                    val statusColor = when (c.status) {
                        "ATIVO" -> R.color.status_active
                        "ATRASADO" -> R.color.status_overdue
                        "QUITADO" -> R.color.status_paid
                        else -> R.color.status_cancelled
                    }
                    val statusText = when (c.status) {
                        "ATIVO" -> "Ativo"
                        "ATRASADO" -> "Atrasado"
                        "QUITADO" -> "Quitado"
                        "CANCELADO" -> "Cancelado"
                        else -> c.status
                    }
                    binding.tvStatus.text = statusText
                    val bg = GradientDrawable().apply {
                        shape = GradientDrawable.RECTANGLE
                        cornerRadius = 40f
                        setColor(ContextCompat.getColor(requireContext(), statusColor))
                    }
                    binding.tvStatus.background = bg

                    val progress = if (c.installmentsCount > 0) (c.paidCount * 100) / c.installmentsCount else 0
                    binding.progressBar.progress = progress
                    binding.tvProgressText.text = "${c.paidCount}/${c.installmentsCount} parcelas pagas"

                    adapter.submitList(result.data.installments)
                }
                is Result.Error -> binding.progress.gone()
            }
        }

        viewModel.loadDetail(contractId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
