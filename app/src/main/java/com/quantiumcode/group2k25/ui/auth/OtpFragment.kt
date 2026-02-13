package com.quantiumcode.group2k25.ui.auth

import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.quantiumcode.group2k25.App
import com.quantiumcode.group2k25.MainActivity
import com.quantiumcode.group2k25.R
import com.quantiumcode.group2k25.databinding.FragmentOtpBinding
import com.quantiumcode.group2k25.util.Result
import com.quantiumcode.group2k25.util.applyTopInsets
import com.quantiumcode.group2k25.util.centerInScrollView
import com.quantiumcode.group2k25.util.gone
import com.quantiumcode.group2k25.util.visible

class OtpFragment : Fragment() {

    private var _binding: FragmentOtpBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: OtpViewModel
    private lateinit var otpFields: List<EditText>
    private var phone: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOtpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.scrollView.applyTopInsets()
        binding.content.centerInScrollView(binding.scrollView)

        phone = arguments?.getString("phone") ?: ""
        val app = requireActivity().application as App
        viewModel = ViewModelProvider(this, OtpViewModelFactory(app.container.authRepository))
            .get(OtpViewModel::class.java)

        val formattedPhone = if (phone.length == 11) {
            "(${phone.substring(0, 2)}) ${phone.substring(2, 7)}-${phone.substring(7)}"
        } else phone
        binding.tvOtpSubtitle.text = "${getString(R.string.otp_subtitle)}\n$formattedPhone"

        otpFields = listOf(binding.otp1, binding.otp2, binding.otp3, binding.otp4, binding.otp5, binding.otp6)
        setupOtpFields()

        viewModel.startCountdown()

        binding.btnResend.setOnClickListener {
            viewModel.resendOtp(phone)
        }

        binding.btnAnotherNumber.setOnClickListener {
            findNavController().popBackStack()
        }

        viewModel.countdown.observe(viewLifecycleOwner) { seconds ->
            if (seconds > 0) {
                binding.btnResend.isEnabled = false
                binding.btnResend.text = String.format(getString(R.string.otp_wait), seconds)
            } else {
                binding.btnResend.isEnabled = true
                binding.btnResend.text = getString(R.string.otp_resend)
            }
        }

        viewModel.verifyResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progress.visible()
                    binding.tvError.gone()
                    otpFields.forEach { it.isEnabled = false }
                }
                is Result.Success -> {
                    binding.progress.gone()
                    (activity as? MainActivity)?.onLoginSuccess()
                }
                is Result.Error -> {
                    binding.progress.gone()
                    binding.tvError.visible()
                    binding.tvError.text = result.message
                    otpFields.forEach { it.isEnabled = true }
                    clearOtpFields()
                }
            }
        }

        otpFields[0].requestFocus()
    }

    private fun setupOtpFields() {
        for (i in otpFields.indices) {
            otpFields[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (s != null && s.length == 1) {
                        if (i < otpFields.size - 1) {
                            otpFields[i + 1].requestFocus()
                        }
                        checkAndSubmit()
                    }
                }
            })

            otpFields[i].setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                    if (otpFields[i].text.isEmpty() && i > 0) {
                        otpFields[i - 1].text.clear()
                        otpFields[i - 1].requestFocus()
                        return@setOnKeyListener true
                    }
                }
                false
            }

            // Handle paste
            otpFields[i].setOnLongClickListener {
                val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = clipboard.primaryClip
                if (clip != null && clip.itemCount > 0) {
                    val pastedText = clip.getItemAt(0).text?.toString()?.replace(Regex("[^\\d]"), "") ?: ""
                    if (pastedText.length == 6) {
                        for (j in otpFields.indices) {
                            otpFields[j].setText(pastedText[j].toString())
                        }
                        otpFields.last().requestFocus()
                        checkAndSubmit()
                        return@setOnLongClickListener true
                    }
                }
                false
            }
        }
    }

    private fun checkAndSubmit() {
        val code = otpFields.joinToString("") { it.text.toString() }
        if (code.length == 6) {
            viewModel.verifyOtp(phone, code)
        }
    }

    private fun clearOtpFields() {
        otpFields.forEach { it.text.clear() }
        otpFields[0].requestFocus()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
