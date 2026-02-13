package com.quantiumcode.group2k25.ui.contracts

import androidx.lifecycle.*
import com.quantiumcode.group2k25.data.api.models.ContractInfo
import com.quantiumcode.group2k25.data.repository.ContractRepository
import com.quantiumcode.group2k25.util.Result
import kotlinx.coroutines.launch

class ContractsViewModel(private val contractRepository: ContractRepository) : ViewModel() {

    private val _contractsResult = MutableLiveData<Result<List<ContractInfo>>>()
    val contractsResult: LiveData<Result<List<ContractInfo>>> = _contractsResult

    fun loadContracts() {
        _contractsResult.value = Result.Loading
        viewModelScope.launch {
            _contractsResult.value = contractRepository.getContracts()
        }
    }
}

class ContractsViewModelFactory(private val contractRepository: ContractRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ContractsViewModel(contractRepository) as T
    }
}
