package com.quantiumcode.group2k25.data.api

import com.quantiumcode.group2k25.data.api.models.*
import retrofit2.Response
import retrofit2.http.*

interface PortalApiService {

    // Auth
    @POST("portal/auth/send-otp")
    suspend fun sendOtp(@Body request: SendOtpRequest): Response<SendOtpResponse>

    @POST("portal/auth/verify-otp")
    suspend fun verifyOtp(@Body request: VerifyOtpRequest): Response<VerifyOtpResponse>

    @GET("portal/auth/me")
    suspend fun getMe(): Response<MeResponse>

    @POST("portal/auth/logout")
    suspend fun logout(): Response<LogoutResponse>

    // Simulator
    @POST("portal/simulator")
    suspend fun simulate(@Body request: SimulatorRequest): Response<SimulatorResult>

    // Lead
    @GET("portal/lead")
    suspend fun getLeadInfo(): Response<LeadInfo>

    @POST("portal/lead/earnings")
    suspend fun uploadEarnings(@Body request: UploadEarningsRequest): Response<UploadResponse>

    @POST("portal/lead/documents")
    suspend fun uploadDocument(@Body request: UploadDocumentRequest): Response<UploadResponse>

    @GET("portal/lead/proposal")
    suspend fun getProposal(): Response<ProposalResponse>

    @POST("portal/lead/accept")
    suspend fun acceptProposal(): Response<AcceptProposalResponse>

    // Contracts
    @GET("portal/contracts")
    suspend fun getContracts(): Response<List<ContractInfo>>

    @GET("portal/contracts/{id}")
    suspend fun getContractDetail(@Path("id") id: String): Response<ContractDetailResponse>

    // Installments
    @GET("portal/installments")
    suspend fun getInstallments(@Query("status") status: String? = null): Response<List<InstallmentInfo>>

    @POST("portal/installments/{id}/pix")
    suspend fun generatePix(@Path("id") id: String): Response<PixResponse>

    // Devices
    @POST("portal/devices")
    suspend fun registerDevice(@Body request: RegisterDeviceRequest): Response<Unit>

    @HTTP(method = "DELETE", path = "portal/devices", hasBody = true)
    suspend fun removeDevice(@Body request: RemoveDeviceRequest): Response<Unit>
}
