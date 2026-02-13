package com.quantiumcode.group2k25.ui.contracts.adapter

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.quantiumcode.group2k25.R
import com.quantiumcode.group2k25.data.api.models.ContractInfo
import com.quantiumcode.group2k25.databinding.ItemContractBinding
import com.quantiumcode.group2k25.util.CurrencyFormatter
import com.quantiumcode.group2k25.util.DateFormatter
import com.quantiumcode.group2k25.util.gone
import com.quantiumcode.group2k25.util.visible

class ContractAdapter(
    private val onItemClick: (ContractInfo) -> Unit
) : ListAdapter<ContractInfo, ContractAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemContractBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemContractBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(contract: ContractInfo) {
            binding.tvContractNumber.text = String.format("Contrato #%d", contract.number)
            binding.tvDate.text = DateFormatter.formatDate(contract.createdAt)
            binding.tvAmount.text = CurrencyFormatter.formatCurrency(contract.totalAmount)
            binding.tvRemaining.text = CurrencyFormatter.formatCurrency(contract.remainingAmount)

            val statusColor = when (contract.status) {
                "ATIVO" -> R.color.status_active
                "ATRASADO" -> R.color.status_overdue
                "QUITADO" -> R.color.status_paid
                "CANCELADO" -> R.color.status_cancelled
                else -> R.color.status_pending
            }
            val statusText = when (contract.status) {
                "ATIVO" -> "Ativo"
                "ATRASADO" -> "Atrasado"
                "QUITADO" -> "Quitado"
                "CANCELADO" -> "Cancelado"
                else -> contract.status
            }
            binding.tvStatus.text = statusText
            val bg = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 40f
                setColor(ContextCompat.getColor(binding.root.context, statusColor))
            }
            binding.tvStatus.background = bg

            val progress = if (contract.installmentsCount > 0)
                (contract.paidCount * 100) / contract.installmentsCount else 0
            binding.progressBar.progress = progress
            binding.tvProgressText.text = "${contract.paidCount}/${contract.installmentsCount} parcelas pagas"

            if (contract.overdueCount > 0) {
                binding.tvOverdueWarning.visible()
                binding.tvOverdueWarning.text = "${contract.overdueCount} parcela(s) atrasada(s)"
            } else {
                binding.tvOverdueWarning.gone()
            }

            binding.root.setOnClickListener { onItemClick(contract) }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<ContractInfo>() {
        override fun areItemsTheSame(oldItem: ContractInfo, newItem: ContractInfo) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ContractInfo, newItem: ContractInfo) = oldItem == newItem
    }
}
