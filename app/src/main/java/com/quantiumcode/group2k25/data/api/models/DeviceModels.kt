package com.quantiumcode.group2k25.data.api.models

data class RegisterDeviceRequest(
    val token: String,
    val platform: String = "android"
)

data class RemoveDeviceRequest(
    val token: String
)
