package com.quantiumcode.group2k25.data.api.models

import com.google.gson.annotations.SerializedName

data class SendOtpRequest(
    val phone: String
)

data class SendOtpResponse(
    val success: Boolean,
    val expiresIn: Int,
    val message: String
)

data class VerifyOtpRequest(
    val phone: String,
    val code: String
)

data class VerifyOtpResponse(
    val success: Boolean,
    val token: String,
    val user: PortalUser
)

data class PortalUser(
    val type: String,
    val phone: String,
    val leadId: String? = null,
    val customerId: String? = null,
    val name: String? = null,
    val status: String? = null
)

data class MeResponse(
    val user: PortalUser
)

data class LogoutResponse(
    val success: Boolean
)

data class ApiError(
    val error: String,
    val code: String? = null
)
