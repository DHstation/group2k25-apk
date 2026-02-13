package com.quantiumcode.group2k25.ui.auth

import androidx.lifecycle.*
import com.quantiumcode.group2k25.data.api.models.SendOtpResponse
import com.quantiumcode.group2k25.data.repository.AuthRepository
import com.quantiumcode.group2k25.util.Result
import kotlinx.coroutines.launch

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _sendOtpResult = MutableLiveData<Result<SendOtpResponse>?>()
    val sendOtpResult: LiveData<Result<SendOtpResponse>?> = _sendOtpResult

    fun clearResult() {
        _sendOtpResult.value = null
    }

    fun sendOtp(phone: String) {
        if (phone.length < 10 || phone.length > 11) {
            _sendOtpResult.value = Result.Error("Número de telefone inválido")
            return
        }
        _sendOtpResult.value = Result.Loading
        viewModelScope.launch {
            _sendOtpResult.value = authRepository.sendOtp(phone)
        }
    }
}

class LoginViewModelFactory(private val authRepository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return LoginViewModel(authRepository) as T
    }
}
