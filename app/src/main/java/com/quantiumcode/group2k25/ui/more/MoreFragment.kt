package com.quantiumcode.group2k25.ui.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.quantiumcode.group2k25.App
import com.quantiumcode.group2k25.BuildConfig
import com.quantiumcode.group2k25.MainActivity
import com.quantiumcode.group2k25.R
import com.quantiumcode.group2k25.databinding.FragmentMoreBinding
import com.quantiumcode.group2k25.util.applyTopInsets

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

        binding.contentContainer.applyTopInsets()

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
            val dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.dialog_logout)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            dialog.window?.setDimAmount(0.6f)

            dialog.findViewById<View>(R.id.btn_cancel).setOnClickListener {
                dialog.dismiss()
            }
            dialog.findViewById<View>(R.id.btn_confirm).setOnClickListener {
                dialog.dismiss()
                viewModel.logout()
            }
            dialog.show()
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
