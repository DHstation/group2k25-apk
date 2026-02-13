package com.quantiumcode.group2k25.data.api.models

data class InstallmentInfo(
    val id: String,
    val contractId: String,
    val contractNumber: Int,
    val number: Int,
    val amount: Double,
    val dueDate: String,
    val status: String,
    val paidAmount: Double?,
    val paidAt: String?,
    val pixCopiaECola: String?,
    val pixExpiresAt: String?
)

data class PixResponse(
    val pixCopiaECola: String,
    val expiresAt: String,
    val amount: Double
)
