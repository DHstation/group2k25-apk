package com.quantiumcode.group2k25.ui.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.quantiumcode.group2k25.App
import com.quantiumcode.group2k25.BuildConfig
import com.quantiumcode.group2k25.MainActivity
import com.quantiumcode.group2k25.R
import com.quantiumcode.group2k25.databinding.FragmentMoreBinding

class MoreFragment : Fragment() {

    private var _binding: FragmentMoreBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MoreViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val app = requireActivity().application as App
        viewModel = ViewModelProvider(this, MoreViewModelFactory(app.container.authRepository))
            .get(MoreViewModel::class.java)

        binding.tvUserName.text = viewModel.getUserName() ?: "Usuário"
        binding.tvUserPhone.text = viewModel.getPhone() ?: ""
        binding.tvVersion.text = String.format(getString(R.string.more_version), BuildConfig.VERSION_NAME)

        binding.menuSimulator.setOnClickListener {
            findNavController().navigate(R.id.action_more_to_simulator)
        }

        binding.menuLoan.setOnClickListener {
            findNavController().navigate(R.id.action_more_to_loan)
        }

        binding.menuPix.setOnClickListener {
            findNavController().navigate(R.id.action_more_to_pix)
        }

        binding.btnLogout.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.more_logout))
                .setMessage(getString(R.string.more_logout_confirm))
                .setPositiveButton(getString(R.string.yes)) { _, _ ->
                    viewModel.logout()
                }
                .setNegativeButton(getString(R.string.no), null)
                .show()
        }

        viewModel.logoutComplete.observe(viewLifecycleOwner) { done ->
            if (done) {
                (activity as? MainActivity)?.onLogout()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
