package com.quantiumcode.group2k25.ui.auth

import android.os.CountDownTimer
import androidx.lifecycle.*
import com.quantiumcode.group2k25.data.api.models.VerifyOtpResponse
import com.quantiumcode.group2k25.data.repository.AuthRepository
import com.quantiumcode.group2k25.util.Result
import kotlinx.coroutines.launch

class OtpViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _verifyResult = MutableLiveData<Result<VerifyOtpResponse>>()
    val verifyResult: LiveData<Result<VerifyOtpResponse>> = _verifyResult

    private val _countdown = MutableLiveData<Int>()
    val countdown: LiveData<Int> = _countdown

    private var countDownTimer: CountDownTimer? = null

    fun startCountdown() {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _countdown.value = (millisUntilFinished / 1000).toInt()
            }
            override fun onFinish() {
                _countdown.value = 0
            }
        }.start()
    }

    fun verifyOtp(phone: String, code: String) {
        _verifyResult.value = Result.Loading
        viewModelScope.launch {
            _verifyResult.value = authRepository.verifyOtp(phone, code)
        }
    }

    fun resendOtp(phone: String) {
        viewModelScope.launch {
            authRepository.sendOtp(phone)
            startCountdown()
        }
    }

    override fun onCleared() {
        super.onCleared()
        countDownTimer?.cancel()
    }
}

class OtpViewModelFactory(private val authRepository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return OtpViewModel(authRepository) as T
    }
}
