package com.quantiumcode.group2k25.data.repository

import com.google.gson.Gson
import com.quantiumcode.group2k25.data.api.PortalApiService
import com.quantiumcode.group2k25.data.api.models.*
import com.quantiumcode.group2k25.data.local.TokenManager
import com.quantiumcode.group2k25.util.Result

class AuthRepository(
    private val api: PortalApiService,
    private val tokenManager: TokenManager
) {

    suspend fun sendOtp(phone: String): Result<SendOtpResponse> {
        return try {
            val response = api.sendOtp(SendOtpRequest(phone))
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                val error = parseError(response.errorBody()?.string())
                Result.Error(error, response.code())
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Erro de conexao")
        }
    }

    suspend fun verifyOtp(phone: String, code: String): Result<VerifyOtpResponse> {
        return try {
            val response = api.verifyOtp(VerifyOtpRequest(phone, code))
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                tokenManager.saveToken(body.token)
                tokenManager.saveUserPhone(body.user.phone)
                tokenManager.saveUserType(body.user.type)
                tokenManager.saveUserName(body.user.name)
                tokenManager.saveCustomerId(body.user.customerId)
                tokenManager.saveLeadId(body.user.leadId)
                Result.Success(body)
            } else {
                val error = parseError(response.errorBody()?.string())
                Result.Error(error, response.code())
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Erro de conexao")
        }
    }

    suspend fun getMe(): Result<PortalUser> {
        return try {
            val response = api.getMe()
            if (response.isSuccessful && response.body() != null) {
                val user = response.body()!!.user
                tokenManager.saveUserType(user.type)
                tokenManager.saveUserName(user.name)
                tokenManager.saveCustomerId(user.customerId)
                tokenManager.saveLeadId(user.leadId)
                Result.Success(user)
            } else {
                Result.Error("Sessao invalida", response.code())
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Erro de conexao")
        }
    }

    suspend fun logout(): Result<Unit> {
        return try {
            api.logout()
            clearSession()
            Result.Success(Unit)
        } catch (e: Exception) {
            clearSession()
            Result.Success(Unit)
        }
    }

    fun clearSession() {
        tokenManager.clearAll()
    }

    fun isLoggedIn(): Boolean = tokenManager.hasToken()

    fun getUserType(): String? = tokenManager.getUserType()

    fun getUserName(): String? = tokenManager.getUserName()

    fun getCustomerId(): String? = tokenManager.getCustomerId()

    fun getLeadId(): String? = tokenManager.getLeadId()

    fun getPhone(): String? = tokenManager.getUserPhone()

    private fun parseError(errorBody: String?): String {
        return try {
            val apiError = Gson().fromJson(errorBody, ApiError::class.java)
            apiError.error
        } catch (e: Exception) {
            "Erro desconhecido"
        }
    }
}
