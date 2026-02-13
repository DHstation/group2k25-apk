package com.quantiumcode.group2k25.data.api.models

data class SimulatorRequest(
    val weeklyEarnings: Double
)

data class SimulatorResult(
    val qualified: Boolean,
    val approvedAmount: Double?,
    val weeklyEarnings: Double,
    val minRequired: Double,
    val message: String
)
