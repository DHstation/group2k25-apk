package com.quantiumcode.group2k25.ui.loan

import androidx.lifecycle.*
import com.quantiumcode.group2k25.data.api.models.UploadResponse
import com.quantiumcode.group2k25.data.repository.LeadRepository
import com.quantiumcode.group2k25.util.Result
import kotlinx.coroutines.launch

class DocumentUploadViewModel(private val leadRepository: LeadRepository) : ViewModel() {

    private val _uploadResult = MutableLiveData<Result<UploadResponse>>()
    val uploadResult: LiveData<Result<UploadResponse>> = _uploadResult

    private val _uploadedDocs = MutableLiveData<MutableSet<String>>(mutableSetOf())
    val uploadedDocs: LiveData<MutableSet<String>> = _uploadedDocs

    fun uploadDocument(type: String, base64: String, mimeType: String) {
        _uploadResult.value = Result.Loading
        viewModelScope.launch {
            val result = if (type == "earnings") {
                leadRepository.uploadEarnings(base64, mimeType)
            } else {
                leadRepository.uploadDocument(type, base64, mimeType)
            }
            _uploadResult.value = result
        }
    }

    fun markUploaded(type: String) {
        val current = _uploadedDocs.value ?: mutableSetOf()
        current.add(type)
        _uploadedDocs.value = current
    }
}

class DocumentUploadViewModelFactory(private val leadRepository: LeadRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return DocumentUploadViewModel(leadRepository) as T
    }
}
