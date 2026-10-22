package com.duoc.panalsuite.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.panalsuite.model.Category
import com.duoc.panalsuite.model.Priority
import com.duoc.panalsuite.repository.SessionRepository
import com.duoc.panalsuite.repository.TicketRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TicketFormViewModel(application: Application) : AndroidViewModel(application) {

    private val sessionRepository = SessionRepository.getInstance(application)
    private val ticketRepository = TicketRepository.getInstance(application)

    private val _uiState = MutableStateFlow(TicketFormUiState())
    val uiState: StateFlow<TicketFormUiState> = _uiState.asStateFlow()

    fun onTitleChange(value: String) {
        _uiState.value = _uiState.value.copy(title = value.take(TicketFormUiState.TITLE_MAX_LENGTH))
    }

    fun onDescriptionChange(value: String) {
        _uiState.value = _uiState.value.copy(description = value.take(TicketFormUiState.DESCRIPTION_MAX_LENGTH))
    }

    fun onLocationChange(value: String) {
        _uiState.value = _uiState.value.copy(location = value)
    }

    fun onCategoryChange(value: Category) {
        _uiState.value = _uiState.value.copy(category = value)
    }

    fun onPriorityChange(value: Priority) {
        _uiState.value = _uiState.value.copy(priority = value)
    }

    fun submit(): Boolean {
        _uiState.value = _uiState.value.copy(showErrors = true)
        return _uiState.value.isValid
    }


    fun saveTicket(onSaved: (Long) -> Unit) {
        val state = _uiState.value
        val category = state.category ?: return
        val priority = state.priority ?: return
        val author = sessionRepository.currentUser.value ?: return
        if (state.isSaving) return

        _uiState.value = state.copy(isSaving = true)
        viewModelScope.launch {
            val ticketId = ticketRepository.createTicket(
                title = state.title,
                description = state.description,
                category = category,
                priority = priority,
                location = state.location,
                author = author
            )
            reset()
            onSaved(ticketId)
        }
    }

    fun reset() {
        _uiState.value = TicketFormUiState()
    }
}
