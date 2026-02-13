package com.quantiumcode.group2k25.data.api.models

data class LeadInfo(
    val id: String,
    val phone: String,
    val name: String?,
    val status: String,
    val weeklyEarnings: Double?,
    val approvedAmount: Double?,
    val documents: LeadDocuments,
    val createdAt: String
)

data class LeadDocuments(
    val earningsReport: Boolean,
    val cnh: Boolean,
    val addressProof: Boolean,
    val uberProfile: Boolean
)

data class UploadEarningsRequest(
    val base64: String,
    val mimeType: String
)

data class UploadDocumentRequest(
    val type: String,
    val base64: String,
    val mimeType: String
)

data class UploadResponse(
    val url: String,
    val status: String? = null
)

data class ProposalResponse(
    val weeklyEarnings: Double,
    val approvedAmount: Double,
    val status: String,
    val details: String
)

data class AcceptProposalResponse(
    val success: Boolean,
    val nextStep: String
)
