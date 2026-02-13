package com.quantiumcode.group2k25.data.repository

import com.google.gson.Gson
import com.quantiumcode.group2k25.data.api.PortalApiService
import com.quantiumcode.group2k25.data.api.models.*
import com.quantiumcode.group2k25.util.Result

class ContractRepository(private val api: PortalApiService) {

    suspend fun getContracts(): Result<List<ContractInfo>> {
        return try {
            val response = api.getContracts()
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

    suspend fun getContractDetail(id: String): Result<ContractDetailResponse> {
        return try {
            val response = api.getContractDetail(id)
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

    private fun parseError(errorBody: String?): String {
        return try {
            val apiError = Gson().fromJson(errorBody, ApiError::class.java)
            apiError.error
        } catch (e: Exception) {
            "Erro desconhecido"
        }
    }
}
