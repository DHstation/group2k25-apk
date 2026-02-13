package com.quantiumcode.group2k25.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.quantiumcode.group2k25.App
import com.quantiumcode.group2k25.R
import com.quantiumcode.group2k25.databinding.FragmentLoginBinding
import com.quantiumcode.group2k25.util.PhoneFormatter
import com.quantiumcode.group2k25.util.Result
import com.quantiumcode.group2k25.util.applyTopInsets
import com.quantiumcode.group2k25.util.centerInScrollView
import com.quantiumcode.group2k25.util.gone
import com.quantiumcode.group2k25.util.visible

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.scrollView.applyTopInsets()
        binding.content.centerInScrollView(binding.scrollView)

        startAnimations()

        val app = requireActivity().application as App
        viewModel = ViewModelProvider(this, LoginViewModelFactory(app.container.authRepository))
            .get(LoginViewModel::class.java)

        binding.etPhone.addTextChangedListener(PhoneFormatter(binding.etPhone))

        binding.btnContinue.setOnClickListener {
            val phone = PhoneFormatter.stripPhone(binding.etPhone.text.toString())
            viewModel.sendOtp(phone)
        }

        viewModel.sendOtpResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progress.visible()
                    binding.btnContinue.isClickable = false
                    binding.btnContinue.alpha = 0.5f
                    binding.tilPhone.error = null
                }
                is Result.Success -> {
                    binding.progress.gone()
                    binding.btnContinue.isClickable = true
                    binding.btnContinue.alpha = 1f
                    viewModel.clearResult()
                    val phone = PhoneFormatter.stripPhone(binding.etPhone.text.toString())
                    val bundle = bundleOf("phone" to phone)
                    findNavController().navigate(R.id.action_login_to_otp, bundle)
                }
                is Result.Error -> {
                    binding.progress.gone()
                    binding.btnContinue.isClickable = true
                    binding.btnContinue.alpha = 1f
                    binding.tilPhone.error = result.message
                }
                null -> { /* resultado limpo, ignorar */ }
            }
        }
    }

    private fun startAnimations() {
        // Logo glow pulsing
        val glowPulse = AnimationUtils.loadAnimation(requireContext(), R.anim.glow_pulse)
        binding.logoGlow.startAnimation(glowPulse)

        // Logo floating
        val logoFloat = AnimationUtils.loadAnimation(requireContext(), R.anim.logo_float)
        binding.logo.startAnimation(logoFloat)

        // Shine sweep on button
        val shineSweep = AnimationUtils.loadAnimation(requireContext(), R.anim.shine_sweep)
        binding.shineView.visibility = View.VISIBLE
        binding.shineView.startAnimation(shineSweep)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
