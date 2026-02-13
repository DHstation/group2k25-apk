package com.quantiumcode.group2k25.ui.pix

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.quantiumcode.group2k25.R
import com.quantiumcode.group2k25.databinding.FragmentPixKeyBinding
import com.quantiumcode.group2k25.util.ClipboardHelper
import com.quantiumcode.group2k25.util.applyTopInsets

class PixKeyFragment : Fragment() {

    private var _binding: FragmentPixKeyBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPixKeyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.applyTopInsets()
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        binding.btnCopy.setOnClickListener {
            ClipboardHelper.copyToClipboard(requireContext(), getString(R.string.pix_key_value), "Chave PIX")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
