package com.quantiumcode.group2k25.ui.simulator

import androidx.lifecycle.*
import com.quantiumcode.group2k25.data.api.models.SimulatorResult
import com.quantiumcode.group2k25.data.repository.SimulatorRepository
import com.quantiumcode.group2k25.util.Result
import kotlinx.coroutines.launch

class SimulatorViewModel(private val simulatorRepository: SimulatorRepository) : ViewModel() {

    private val _simulatorResult = MutableLiveData<Result<SimulatorResult>>()
    val simulatorResult: LiveData<Result<SimulatorResult>> = _simulatorResult

    fun simulate(weeklyEarnings: Double) {
        _simulatorResult.value = Result.Loading
        viewModelScope.launch {
            _simulatorResult.value = simulatorRepository.simulate(weeklyEarnings)
        }
    }
}

class SimulatorViewModelFactory(private val simulatorRepository: SimulatorRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return SimulatorViewModel(simulatorRepository) as T
    }
}
