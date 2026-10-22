package com.duoc.panalsuite.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.panalsuite.model.TicketStatus
import com.duoc.panalsuite.repository.SessionRepository
import com.duoc.panalsuite.repository.TicketRepository
import com.duoc.panalsuite.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class TicketDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val sessionRepository = SessionRepository.getInstance(application)
    private val ticketRepository = TicketRepository.getInstance(application)
    private val userRepository = UserRepository()

    private val selectedTicketId = MutableStateFlow<Long?>(null)
    private val commentText = MutableStateFlow("")
    private val showCommentError = MutableStateFlow(false)

    private val ticketState: Flow<TicketDetailUiState> = selectedTicketId
        .filterNotNull()
        .flatMapLatest { ticketId ->
            combine(
                ticketRepository.observeTicket(ticketId),
                ticketRepository.observeComments(ticketId),
                sessionRepository.currentUser
            ) { ticket, comments, user ->
                TicketDetailUiState(
                    ticket = ticket,
                    comments = comments,
                    user = user,
                    requesterName = ticket?.let { nameOf(it.requesterEmail) }.orEmpty(),
                    assigneeName = ticket?.assigneeEmail?.let { nameOf(it) } ?: "Sin asignar",
                    isLoading = false
                )
            }.onStart { emit(TicketDetailUiState()) }
        }

    val uiState: StateFlow<TicketDetailUiState> =
        combine(ticketState, commentText, showCommentError) { state, text, showError ->
            state.copy(commentText = text, showCommentError = showError)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TicketDetailUiState())

    fun loadTicket(ticketId: Long) {
        if (selectedTicketId.value == ticketId) return
        selectedTicketId.value = ticketId
        commentText.value = ""
        showCommentError.value = false
    }

    fun onCommentChange(value: String) {
        commentText.value = value.take(TicketDetailUiState.COMMENT_MAX_LENGTH)
    }

    fun sendComment(): Boolean {
        showCommentError.value = true
        val text = commentText.value
        val ticketId = selectedTicketId.value ?: return false
        val author = sessionRepository.currentUser.value ?: return false
        if (TicketDetailUiState.validateComment(text) != null) return false

        viewModelScope.launch { ticketRepository.addComment(ticketId, text, author) }
        commentText.value = ""
        showCommentError.value = false
        return true
    }

    fun changeStatus(newStatus: TicketStatus) {
        val ticketId = selectedTicketId.value ?: return
        val author = sessionRepository.currentUser.value ?: return
        viewModelScope.launch { ticketRepository.changeStatus(ticketId, newStatus, author) }
    }

    private fun nameOf(email: String): String = userRepository.findByEmail(email)?.name ?: email
}
