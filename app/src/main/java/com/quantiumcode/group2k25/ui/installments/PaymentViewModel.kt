package com.quantiumcode.group2k25.ui.installments

import android.os.CountDownTimer
import androidx.lifecycle.*
import com.quantiumcode.group2k25.data.api.models.PixResponse
import com.quantiumcode.group2k25.data.repository.InstallmentRepository
import com.quantiumcode.group2k25.util.DateFormatter
import com.quantiumcode.group2k25.util.Result
import kotlinx.coroutines.launch

class PaymentViewModel(private val installmentRepository: InstallmentRepository) : ViewModel() {

    private val _pixResult = MutableLiveData<Result<PixResponse>>()
    val pixResult: LiveData<Result<PixResponse>> = _pixResult

    private val _countdown = MutableLiveData<String>()
    val countdown: LiveData<String> = _countdown

    private val _isExpired = MutableLiveData<Boolean>()
    val isExpired: LiveData<Boolean> = _isExpired

    private var countDownTimer: CountDownTimer? = null

    fun generatePix(installmentId: String) {
        _pixResult.value = Result.Loading
        viewModelScope.launch {
            _pixResult.value = installmentRepository.generatePix(installmentId)
        }
    }

    fun startCountdown(expiresAt: String) {
        countDownTimer?.cancel()
        val expiresDate = DateFormatter.parseIso(expiresAt) ?: return
        val now = System.currentTimeMillis()
        val diff = expiresDate.time - now

        if (diff <= 0) {
            _isExpired.value = true
            return
        }

        _isExpired.value = false
        countDownTimer = object : CountDownTimer(diff, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val totalSeconds = millisUntilFinished / 1000
                val hours = totalSeconds / 3600
                val minutes = (totalSeconds % 3600) / 60
                val seconds = totalSeconds % 60
                _countdown.value = when {
                    hours > 0 -> String.format("Expira em %dh %dm", hours, minutes)
                    minutes > 0 -> String.format("Expira em %dm %ds", minutes, seconds)
                    else -> String.format("Expira em %ds", seconds)
                }
            }

            override fun onFinish() {
                _isExpired.value = true
            }
        }.start()
    }

    fun stopCountdown() {
        countDownTimer?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        countDownTimer?.cancel()
    }
}

class PaymentViewModelFactory(private val installmentRepository: InstallmentRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return PaymentViewModel(installmentRepository) as T
    }
}
