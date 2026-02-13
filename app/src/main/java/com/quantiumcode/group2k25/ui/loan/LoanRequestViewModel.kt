package com.quantiumcode.group2k25.ui.loan

import androidx.lifecycle.*
import com.quantiumcode.group2k25.data.api.models.LeadInfo
import com.quantiumcode.group2k25.data.repository.LeadRepository
import com.quantiumcode.group2k25.util.Result
import kotlinx.coroutines.launch

class LoanRequestViewModel(private val leadRepository: LeadRepository) : ViewModel() {

    private val _leadResult = MutableLiveData<Result<LeadInfo>>()
    val leadResult: LiveData<Result<LeadInfo>> = _leadResult

    fun loadLeadInfo() {
        _leadResult.value = Result.Loading
        viewModelScope.launch {
            _leadResult.value = leadRepository.getLeadInfo()
        }
    }
}

class LoanRequestViewModelFactory(private val leadRepository: LeadRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return LoanRequestViewModel(leadRepository) as T
    }
}
