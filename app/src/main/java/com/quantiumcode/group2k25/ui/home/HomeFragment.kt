package com.quantiumcode.group2k25.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.quantiumcode.group2k25.App
import com.quantiumcode.group2k25.R
import com.quantiumcode.group2k25.databinding.FragmentHomeBinding
import com.quantiumcode.group2k25.util.DateFormatter
import com.quantiumcode.group2k25.util.gone
import com.quantiumcode.group2k25.util.visible

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val app = requireActivity().application as App
        viewModel = ViewModelProvider(this, HomeViewModelFactory(
            app.container.authRepository,
            app.container.contractRepository,
            app.container.installmentRepository
        )).get(HomeViewModel::class.java)

        setupUI()
        observeData()

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadData()
        }

        viewModel.loadData()
    }

    private fun setupUI() {
        val name = viewModel.getUserName()
        binding.tvWelcome.text = if (name != null) getString(R.string.welcome_message, name) else getString(R.string.welcome_default)

        val userType = viewModel.getUserType()
        binding.tvUserType.text = if (userType == "customer") "Cliente" else "Solicitante"

        if (userType == "customer") {
            binding.customerStats.visible()
            binding.cardContracts.visible()
            binding.cardInstallments.visible()
        } else {
            binding.customerStats.gone()
        }

        binding.cardContracts.setOnClickListener {
            findNavController().navigate(R.id.navigation_contracts)
        }
        binding.cardInstallments.setOnClickListener {
            findNavController().navigate(R.id.navigation_installments)
        }
        binding.cardSimulator.setOnClickListener {
            findNavController().navigate(R.id.simulatorFragment)
        }
        binding.cardLoanRequest.setOnClickListener {
            findNavController().navigate(R.id.loanRequestFragment)
        }
    }

    private fun observeData() {
        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.swipeRefresh.isRefreshing = loading
        }

        viewModel.user.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                binding.tvWelcome.text = if (user.name != null) getString(R.string.welcome_message, user.name) else getString(R.string.welcome_default)
                binding.tvUserType.text = if (user.type == "customer") "Cliente" else "Solicitante"
                if (user.type == "customer") {
                    binding.customerStats.visible()
                } else {
                    binding.customerStats.gone()
                }
            }
        }

        viewModel.overdueCount.observe(viewLifecycleOwner) { count ->
            binding.tvOverdueCount.text = if (count > 0) "$count atrasada(s)" else getString(R.string.home_no_overdue)
            if (count > 0) binding.tvOverdueCount.setTextColor(resources.getColor(R.color.status_overdue, null))
        }

        viewModel.nextPayment.observe(viewLifecycleOwner) { date ->
            binding.tvNextPayment.text = DateFormatter.formatDate(date)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
