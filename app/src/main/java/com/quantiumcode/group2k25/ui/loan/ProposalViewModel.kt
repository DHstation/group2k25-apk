package com.quantiumcode.group2k25.ui.loan

import androidx.lifecycle.*
import com.quantiumcode.group2k25.data.api.models.AcceptProposalResponse
import com.quantiumcode.group2k25.data.api.models.ProposalResponse
import com.quantiumcode.group2k25.data.repository.LeadRepository
import com.quantiumcode.group2k25.util.Result
import kotlinx.coroutines.launch

class ProposalViewModel(private val leadRepository: LeadRepository) : ViewModel() {

    private val _proposalResult = MutableLiveData<Result<ProposalResponse>>()
    val proposalResult: LiveData<Result<ProposalResponse>> = _proposalResult

    private val _acceptResult = MutableLiveData<Result<AcceptProposalResponse>>()
    val acceptResult: LiveData<Result<AcceptProposalResponse>> = _acceptResult

    fun loadProposal() {
        _proposalResult.value = Result.Loading
        viewModelScope.launch {
            _proposalResult.value = leadRepository.getProposal()
        }
    }

    fun acceptProposal() {
        _acceptResult.value = Result.Loading
        viewModelScope.launch {
            _acceptResult.value = leadRepository.acceptProposal()
        }
    }
}

class ProposalViewModelFactory(private val leadRepository: LeadRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ProposalViewModel(leadRepository) as T
    }
}
