package com.quantiumcode.group2k25.ui.installments

import androidx.lifecycle.*
import com.quantiumcode.group2k25.data.api.models.InstallmentInfo
import com.quantiumcode.group2k25.data.repository.InstallmentRepository
import com.quantiumcode.group2k25.util.Result
import kotlinx.coroutines.launch

class InstallmentsViewModel(private val installmentRepository: InstallmentRepository) : ViewModel() {

    private val _installmentsResult = MutableLiveData<Result<List<InstallmentInfo>>>()
    val installmentsResult: LiveData<Result<List<InstallmentInfo>>> = _installmentsResult

    private var currentFilter: String? = null

    fun loadInstallments() {
        _installmentsResult.value = Result.Loading
        viewModelScope.launch {
            _installmentsResult.value = installmentRepository.getInstallments(currentFilter)
        }
    }

    fun setFilter(status: String?) {
        currentFilter = status
        loadInstallments()
    }
}

class InstallmentsViewModelFactory(private val installmentRepository: InstallmentRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return InstallmentsViewModel(installmentRepository) as T
    }
}
