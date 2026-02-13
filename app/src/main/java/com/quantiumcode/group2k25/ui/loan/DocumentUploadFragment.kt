package com.quantiumcode.group2k25.ui.loan

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.quantiumcode.group2k25.App
import com.quantiumcode.group2k25.R
import com.quantiumcode.group2k25.databinding.FragmentDocumentUploadBinding
import com.quantiumcode.group2k25.util.FileUtils
import com.quantiumcode.group2k25.util.Result
import com.quantiumcode.group2k25.util.applyTopInsets
import com.quantiumcode.group2k25.util.gone
import com.quantiumcode.group2k25.util.showSnackbar
import com.quantiumcode.group2k25.util.visible

class DocumentUploadFragment : Fragment() {

    private var _binding: FragmentDocumentUploadBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: DocumentUploadViewModel
    private var currentUploadType: String = ""

    private val pickFile = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                val base64 = FileUtils.uriToBase64(requireContext(), uri)
                val mimeType = FileUtils.getMimeType(requireContext(), uri)
                if (base64 != null) {
                    viewModel.uploadDocument(currentUploadType, base64, mimeType)
                } else {
                    binding.root.showSnackbar("Erro ao ler arquivo")
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDocumentUploadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.applyTopInsets()
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        val app = requireActivity().application as App
        viewModel = ViewModelProvider(this, DocumentUploadViewModelFactory(app.container.leadRepository))
            .get(DocumentUploadViewModel::class.java)

        binding.btnUploadCnh.setOnClickListener { selectFile("cnh") }
        binding.btnUploadAddress.setOnClickListener { selectFile("addressProof") }
        binding.btnUploadUber.setOnClickListener { selectFile("uberProfile") }

        viewModel.uploadResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> binding.progress.visible()
                is Result.Success -> {
                    binding.progress.gone()
                    binding.root.showSnackbar("Documento enviado!")
                    updateButtonState(currentUploadType)
                }
                is Result.Error -> {
                    binding.progress.gone()
                    binding.root.showSnackbar(result.message)
                }
            }
        }

        viewModel.uploadedDocs.observe(viewLifecycleOwner) { docs ->
            binding.tvCnhStatus.text = if (docs.contains("cnh")) getString(R.string.doc_uploaded) else "Pendente"
            binding.tvAddressStatus.text = if (docs.contains("addressProof")) getString(R.string.doc_uploaded) else "Pendente"
            binding.tvUberStatus.text = if (docs.contains("uberProfile")) getString(R.string.doc_uploaded) else "Pendente"

            if (docs.contains("cnh")) { binding.btnUploadCnh.isEnabled = false; binding.btnUploadCnh.alpha = 0.4f }
            if (docs.contains("addressProof")) { binding.btnUploadAddress.isEnabled = false; binding.btnUploadAddress.alpha = 0.4f }
            if (docs.contains("uberProfile")) { binding.btnUploadUber.isEnabled = false; binding.btnUploadUber.alpha = 0.4f }
        }
    }

    private fun selectFile(type: String) {
        currentUploadType = type
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            this.type = "image/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        pickFile.launch(intent)
    }

    private fun updateButtonState(type: String) {
        viewModel.markUploaded(type)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
