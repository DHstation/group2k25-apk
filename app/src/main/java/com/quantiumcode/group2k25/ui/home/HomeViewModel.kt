package com.quantiumcode.group2k25.ui.home

import androidx.lifecycle.*
import com.quantiumcode.group2k25.data.api.models.ContractInfo
import com.quantiumcode.group2k25.data.api.models.InstallmentInfo
import com.quantiumcode.group2k25.data.api.models.PortalUser
import com.quantiumcode.group2k25.data.repository.AuthRepository
import com.quantiumcode.group2k25.data.repository.ContractRepository
import com.quantiumcode.group2k25.data.repository.InstallmentRepository
import com.quantiumcode.group2k25.util.Result
import kotlinx.coroutines.launch

class HomeViewModel(
    private val authRepository: AuthRepository,
    private val contractRepository: ContractRepository,
    private val installmentRepository: InstallmentRepository
) : ViewModel() {

    private val _user = MutableLiveData<PortalUser?>()
    val user: LiveData<PortalUser?> = _user

    private val _contracts = MutableLiveData<List<ContractInfo>>()
    val contracts: LiveData<List<ContractInfo>> = _contracts

    private val _overdueCount = MutableLiveData<Int>()
    val overdueCount: LiveData<Int> = _overdueCount

    private val _nextPayment = MutableLiveData<String?>()
    val nextPayment: LiveData<String?> = _nextPayment

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadData() {
        _isLoading.value = true
        viewModelScope.launch {
            val meResult = authRepository.getMe()
            if (meResult is Result.Success) {
                _user.value = meResult.data
                if (meResult.data.type == "customer") {
                    loadCustomerData()
                }
            }
            _isLoading.value = false
        }
    }

    private suspend fun loadCustomerData() {
        val contractsResult = contractRepository.getContracts()
        if (contractsResult is Result.Success) {
            _contracts.value = contractsResult.data
            val totalOverdue = contractsResult.data.sumOf { it.overdueCount }
            _overdueCount.value = totalOverdue
            val nextDue = contractsResult.data.mapNotNull { it.nextDueDate }.minOrNull()
            _nextPayment.value = nextDue
        }
    }

    fun getUserType(): String? = authRepository.getUserType()
    fun getUserName(): String? = authRepository.getUserName()
}

class HomeViewModelFactory(
    private val authRepository: AuthRepository,
    private val contractRepository: ContractRepository,
    private val installmentRepository: InstallmentRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return HomeViewModel(authRepository, contractRepository, installmentRepository) as T
    }
}
