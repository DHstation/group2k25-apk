package com.quantiumcode.group2k25.ui.loan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.quantiumcode.group2k25.App
import com.quantiumcode.group2k25.R
import com.quantiumcode.group2k25.databinding.FragmentProposalBinding
import com.quantiumcode.group2k25.util.CurrencyFormatter
import com.quantiumcode.group2k25.util.Result
import com.quantiumcode.group2k25.util.gone
import com.quantiumcode.group2k25.util.showSnackbar
import com.quantiumcode.group2k25.util.visible

class ProposalFragment : Fragment() {

    private var _binding: FragmentProposalBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ProposalViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProposalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        val app = requireActivity().application as App
        viewModel = ViewModelProvider(this, ProposalViewModelFactory(app.container.leadRepository))
            .get(ProposalViewModel::class.java)

        binding.btnAccept.setOnClickListener {
            viewModel.acceptProposal()
        }

        viewModel.proposalResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> binding.progress.visible()
                is Result.Success -> {
                    binding.progress.gone()
                    binding.tvApprovedAmount.text = CurrencyFormatter.formatCurrency(result.data.approvedAmount)
                    binding.tvDetails.text = result.data.details
                    if (result.data.status != "AGUARDANDO_ACEITE") {
                        binding.btnAccept.isEnabled = false
                        binding.btnAccept.text = getString(R.string.proposal_accepted)
                    }
                }
                is Result.Error -> {
                    binding.progress.gone()
                    binding.root.showSnackbar(result.message)
                }
            }
        }

        viewModel.acceptResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progress.visible()
                    binding.btnAccept.isEnabled = false
                }
                is Result.Success -> {
                    binding.progress.gone()
                    binding.btnAccept.text = getString(R.string.proposal_accepted)
                    binding.root.showSnackbar(result.data.nextStep)
                }
                is Result.Error -> {
                    binding.progress.gone()
                    binding.btnAccept.isEnabled = true
                    binding.root.showSnackbar(result.message)
                }
            }
        }

        viewModel.loadProposal()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
