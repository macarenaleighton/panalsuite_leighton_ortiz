package com.duoc.panalsuite.viewmodel
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.panalsuite.repository.SessionRepository
import com.duoc.panalsuite.repository.TicketRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SessionViewModel(application: Application) : AndroidViewModel(application) {

    private val sessionRepository = SessionRepository.getInstance(application)
    private val ticketRepository = TicketRepository.getInstance(application)

    val sessionState: StateFlow<SessionState> = sessionRepository.currentUser
        .map { user -> if (user == null) SessionState.LoggedOut else SessionState.LoggedIn(user) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, SessionState.Loading)

    init {
        viewModelScope.launch { ticketRepository.seedIfEmpty() }
    }

    fun logout() {
        viewModelScope.launch { sessionRepository.clearSession() }
    }
}
