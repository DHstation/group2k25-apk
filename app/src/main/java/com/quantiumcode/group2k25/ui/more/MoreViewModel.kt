package com.quantiumcode.group2k25.ui.more

import androidx.lifecycle.*
import com.quantiumcode.group2k25.data.repository.AuthRepository
import kotlinx.coroutines.launch

class MoreViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _logoutComplete = MutableLiveData<Boolean>()
    val logoutComplete: LiveData<Boolean> = _logoutComplete

    fun getUserName(): String? = authRepository.getUserName()
    fun getPhone(): String? = authRepository.getPhone()

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _logoutComplete.value = true
        }
    }
}

class MoreViewModelFactory(private val authRepository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return MoreViewModel(authRepository) as T
    }
}
