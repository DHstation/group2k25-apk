package com.quantiumcode.group2k25.ui.installments.adapter

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.quantiumcode.group2k25.R
import com.quantiumcode.group2k25.data.api.models.InstallmentInfo
import com.quantiumcode.group2k25.databinding.ItemInstallmentFullBinding
import com.quantiumcode.group2k25.util.CurrencyFormatter
import com.quantiumcode.group2k25.util.DateFormatter
import com.quantiumcode.group2k25.util.gone
import com.quantiumcode.group2k25.util.visible

class InstallmentListAdapter(
    private val onPayClick: (InstallmentInfo) -> Unit
) : ListAdapter<InstallmentInfo, InstallmentListAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemInstallmentFullBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemInstallmentFullBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(installment: InstallmentInfo) {
            binding.tvContractNumber.text = "Contrato #${installment.contractNumber}"
            binding.tvInstallmentNumber.text = "Parcela ${installment.number}"
            binding.tvDueDate.text = "Vencimento: ${DateFormatter.formatDate(installment.dueDate)}"
            binding.tvAmount.text = CurrencyFormatter.formatCurrency(installment.amount)

            val statusColor = when (installment.status) {
                "PAGO" -> R.color.status_paid
                "ATRASADO" -> R.color.status_overdue
                "PENDENTE" -> R.color.status_pending
                else -> R.color.status_cancelled
            }
            val statusText = when (installment.status) {
                "PAGO" -> "Pago"
                "ATRASADO" -> "Atrasado"
                "PENDENTE" -> "Pendente"
                else -> installment.status
            }
            binding.tvStatus.text = statusText
            val bg = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 40f
                setColor(ContextCompat.getColor(binding.root.context, statusColor))
            }
            binding.tvStatus.background = bg

            if (installment.status == "PENDENTE" || installment.status == "ATRASADO") {
                binding.btnPay.visible()
                binding.btnPay.setOnClickListener { onPayClick(installment) }
            } else {
                binding.btnPay.gone()
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<InstallmentInfo>() {
        override fun areItemsTheSame(oldItem: InstallmentInfo, newItem: InstallmentInfo) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: InstallmentInfo, newItem: InstallmentInfo) = oldItem == newItem
    }
}
