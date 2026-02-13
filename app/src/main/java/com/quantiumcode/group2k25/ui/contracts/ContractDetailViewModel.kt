package com.quantiumcode.group2k25.ui.contracts

import androidx.lifecycle.*
import com.quantiumcode.group2k25.data.api.models.ContractDetailResponse
import com.quantiumcode.group2k25.data.repository.ContractRepository
import com.quantiumcode.group2k25.util.Result
import kotlinx.coroutines.launch

class ContractDetailViewModel(private val contractRepository: ContractRepository) : ViewModel() {

    private val _detailResult = MutableLiveData<Result<ContractDetailResponse>>()
    val detailResult: LiveData<Result<ContractDetailResponse>> = _detailResult

    fun loadDetail(contractId: String) {
        _detailResult.value = Result.Loading
        viewModelScope.launch {
            _detailResult.value = contractRepository.getContractDetail(contractId)
        }
    }
}

class ContractDetailViewModelFactory(private val contractRepository: ContractRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ContractDetailViewModel(contractRepository) as T
    }
}
