package com.quantiumcode.group2k25.ui.installments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.quantiumcode.group2k25.App
import com.quantiumcode.group2k25.databinding.FragmentPaymentBinding
import com.quantiumcode.group2k25.util.ClipboardHelper
import com.quantiumcode.group2k25.util.CurrencyFormatter
import com.quantiumcode.group2k25.util.Result
import com.quantiumcode.group2k25.util.applyTopInsets
import com.quantiumcode.group2k25.util.gone
import com.quantiumcode.group2k25.util.visible

class PaymentFragment : Fragment() {

    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: PaymentViewModel
    private var installmentId: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.applyTopInsets()
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        installmentId = arguments?.getString("installmentId") ?: return
        val app = requireActivity().application as App
        viewModel = ViewModelProvider(this, PaymentViewModelFactory(app.container.installmentRepository))
            .get(PaymentViewModel::class.java)

        binding.btnCopy.setOnClickListener {
            val code = binding.tvPixCode.text.toString()
            if (code.isNotEmpty()) {
                ClipboardHelper.copyToClipboard(requireContext(), code, "PIX")
            }
        }

        binding.btnRegenerate.setOnClickListener {
            viewModel.generatePix(installmentId)
        }

        viewModel.pixResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progress.visible()
                    binding.cardPix.gone()
                }
                is Result.Success -> {
                    binding.progress.gone()
                    binding.cardPix.visible()
                    binding.tvAmount.text = CurrencyFormatter.formatCurrency(result.data.amount)
                    binding.tvPixCode.text = result.data.pixCopiaECola
                    binding.btnRegenerate.gone()
                    binding.btnCopy.isEnabled = true
                    binding.btnCopy.alpha = 1f
                    viewModel.startCountdown(result.data.expiresAt)
                }
                is Result.Error -> {
                    binding.progress.gone()
                    binding.cardPix.visible()
                    binding.tvPixCode.text = result.message
                    binding.btnCopy.isEnabled = false
                    binding.btnCopy.alpha = 0.4f
                }
            }
        }

        viewModel.countdown.observe(viewLifecycleOwner) { text ->
            binding.tvCountdown.text = text
        }

        viewModel.isExpired.observe(viewLifecycleOwner) { expired ->
            if (expired) {
                binding.tvCountdown.text = "Expirado"
                binding.btnCopy.isEnabled = false
                binding.btnRegenerate.visible()
            }
        }

        viewModel.generatePix(installmentId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.stopCountdown()
        _binding = null
    }
}
