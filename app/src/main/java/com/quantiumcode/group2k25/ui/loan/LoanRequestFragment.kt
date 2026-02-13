package com.quantiumcode.group2k25.ui.loan

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.quantiumcode.group2k25.App
import com.quantiumcode.group2k25.R
import com.quantiumcode.group2k25.data.api.models.LeadInfo
import com.quantiumcode.group2k25.databinding.FragmentLoanRequestBinding
import com.quantiumcode.group2k25.util.Result
import com.quantiumcode.group2k25.util.gone
import com.quantiumcode.group2k25.util.visible

class LoanRequestFragment : Fragment() {

    private var _binding: FragmentLoanRequestBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: LoanRequestViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLoanRequestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val app = requireActivity().application as App
        viewModel = ViewModelProvider(this, LoanRequestViewModelFactory(app.container.leadRepository))
            .get(LoanRequestViewModel::class.java)

        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        binding.swipeRefresh.setOnRefreshListener { viewModel.loadLeadInfo() }

        binding.btnUploadEarnings.setOnClickListener {
            findNavController().navigate(R.id.action_loan_to_documents)
        }

        binding.btnViewProposal.setOnClickListener {
            findNavController().navigate(R.id.action_loan_to_proposal)
        }

        binding.btnUploadDocuments.setOnClickListener {
            findNavController().navigate(R.id.action_loan_to_documents)
        }

        viewModel.leadResult.observe(viewLifecycleOwner) { result ->
            binding.swipeRefresh.isRefreshing = false
            when (result) {
                is Result.Loading -> binding.progress.visible()
                is Result.Success -> {
                    binding.progress.gone()
                    updateUI(result.data)
                }
                is Result.Error -> {
                    binding.progress.gone()
                }
            }
        }

        viewModel.loadLeadInfo()
    }

    private fun updateUI(lead: LeadInfo) {
        val statusColor = when (lead.status) {
            "NOVO" -> R.color.status_pending
            "RELATORIO_RECEBIDO" -> R.color.status_active
            "AGUARDANDO_ACEITE" -> R.color.primary
            "PROPOSTA_ACEITA" -> R.color.status_active
            "AGUARDANDO_DOCUMENTOS" -> R.color.warning
            "DOCUMENTOS_RECEBIDOS" -> R.color.status_active
            "CONVERTIDO" -> R.color.status_paid
            else -> R.color.status_cancelled
        }
        val statusText = when (lead.status) {
            "NOVO" -> "Nova solicitação"
            "RELATORIO_RECEBIDO" -> "Relatório recebido"
            "AGUARDANDO_ACEITE" -> "Proposta disponível"
            "PROPOSTA_ACEITA" -> "Proposta aceita"
            "AGUARDANDO_DOCUMENTOS" -> "Aguardando documentos"
            "DOCUMENTOS_RECEBIDOS" -> "Documentos recebidos"
            "CONVERTIDO" -> "Convertido em cliente"
            else -> lead.status
        }
        binding.tvStatus.text = statusText
        val bg = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 40f
            setColor(ContextCompat.getColor(requireContext(), statusColor))
        }
        binding.tvStatus.background = bg

        val statusDesc = when (lead.status) {
            "NOVO" -> "Envie seu relatório de ganhos para iniciar a análise."
            "RELATORIO_RECEBIDO" -> "Seu relatório está sendo analisado. Aguarde a proposta."
            "AGUARDANDO_ACEITE" -> "Você tem uma proposta disponível! Clique para ver."
            "PROPOSTA_ACEITA" -> "Proposta aceita! Agora envie seus documentos."
            "AGUARDANDO_DOCUMENTOS" -> "Envie os documentos necessários para finalizar."
            "DOCUMENTOS_RECEBIDOS" -> "Documentos em verificação. Aguarde a aprovação final."
            "CONVERTIDO" -> "Parabéns! Seu empréstimo foi aprovado. Verifique seus contratos."
            else -> ""
        }
        binding.tvStatusDescription.text = statusDesc

        // Show/hide action buttons based on status
        binding.btnUploadEarnings.visibility = if (lead.status == "NOVO") View.VISIBLE else View.GONE
        binding.btnViewProposal.visibility = if (lead.status == "AGUARDANDO_ACEITE") View.VISIBLE else View.GONE
        binding.btnUploadDocuments.visibility = if (lead.status == "AGUARDANDO_DOCUMENTOS" || lead.status == "PROPOSTA_ACEITA") View.VISIBLE else View.GONE

        // Show documents card if relevant
        if (lead.status == "AGUARDANDO_DOCUMENTOS" || lead.status == "PROPOSTA_ACEITA" || lead.status == "DOCUMENTOS_RECEBIDOS") {
            binding.cardDocuments.visible()
            val check = "\u2705"
            val pending = "\u23F3"
            binding.tvDocEarnings.text = "${if (lead.documents.earningsReport) check else pending} ${getString(R.string.doc_earnings)}"
            binding.tvDocCnh.text = "${if (lead.documents.cnh) check else pending} ${getString(R.string.doc_cnh)}"
            binding.tvDocAddress.text = "${if (lead.documents.addressProof) check else pending} ${getString(R.string.doc_address_proof)}"
            binding.tvDocUber.text = "${if (lead.documents.uberProfile) check else pending} ${getString(R.string.doc_uber_profile)}"
        } else {
            binding.cardDocuments.gone()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
