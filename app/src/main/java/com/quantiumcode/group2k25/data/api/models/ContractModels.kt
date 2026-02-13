package com.quantiumcode.group2k25.data.api.models

data class ContractInfo(
    val id: String,
    val number: Int,
    val principalAmount: Double,
    val totalAmount: Double,
    val interestRate: Double,
    val installmentsCount: Int,
    val installmentAmount: Double,
    val status: String,
    val paidCount: Int,
    val overdueCount: Int,
    val remainingAmount: Double,
    val nextDueDate: String?,
    val createdAt: String
)

data class ContractDetailResponse(
    val contract: ContractInfo,
    val installments: List<InstallmentInfo>
)
