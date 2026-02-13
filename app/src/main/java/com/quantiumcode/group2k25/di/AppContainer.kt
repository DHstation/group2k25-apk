package com.quantiumcode.group2k25.di

import android.content.Context
import com.quantiumcode.group2k25.data.api.ApiClient
import com.quantiumcode.group2k25.data.api.PortalApiService
import com.quantiumcode.group2k25.data.local.TokenManager
import com.quantiumcode.group2k25.data.local.UserPreferences
import com.quantiumcode.group2k25.data.repository.*

class AppContainer(context: Context) {

    val tokenManager = TokenManager(context)
    val userPreferences = UserPreferences(context)

    private val retrofit = ApiClient.create(tokenManager)
    val apiService: PortalApiService = retrofit.create(PortalApiService::class.java)

    val authRepository = AuthRepository(apiService, tokenManager)
    val contractRepository = ContractRepository(apiService)
    val installmentRepository = InstallmentRepository(apiService)
    val simulatorRepository = SimulatorRepository(apiService)
    val leadRepository = LeadRepository(apiService)
}
