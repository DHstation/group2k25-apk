package com.quantiumcode.group2k25.data.repository

import com.google.gson.Gson
import com.quantiumcode.group2k25.data.api.PortalApiService
import com.quantiumcode.group2k25.data.api.models.*
import com.quantiumcode.group2k25.util.Result

class LeadRepository(private val api: PortalApiService) {

    suspend fun getLeadInfo(): Result<LeadInfo> {
        return try {
            val response = api.getLeadInfo()
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

    suspend fun uploadEarnings(base64: String, mimeType: String): Result<UploadResponse> {
        return try {
            val response = api.uploadEarnings(UploadEarningsRequest(base64, mimeType))
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

    suspend fun uploadDocument(type: String, base64: String, mimeType: String): Result<UploadResponse> {
        return try {
            val response = api.uploadDocument(UploadDocumentRequest(type, base64, mimeType))
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

    suspend fun getProposal(): Result<ProposalResponse> {
        return try {
            val response = api.getProposal()
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

    suspend fun acceptProposal(): Result<AcceptProposalResponse> {
        return try {
            val response = api.acceptProposal()
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
