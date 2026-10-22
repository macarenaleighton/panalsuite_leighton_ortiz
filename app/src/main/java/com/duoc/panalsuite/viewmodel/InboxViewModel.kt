package com.duoc.panalsuite.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.panalsuite.model.Ticket
import com.duoc.panalsuite.model.TicketStatus
import com.duoc.panalsuite.model.User
import com.duoc.panalsuite.repository.SessionRepository
import com.duoc.panalsuite.repository.TicketRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class InboxViewModel(application: Application) : AndroidViewModel(application) {

    private val sessionRepository = SessionRepository.getInstance(application)
    private val ticketRepository = TicketRepository.getInstance(application)

    private val selectedStatus = MutableStateFlow<TicketStatus?>(null)

    private val userTickets: Flow<Pair<User?, List<Ticket>>> =
        sessionRepository.currentUser.flatMapLatest { user ->
            if (user == null) {
                flowOf<Pair<User?, List<Ticket>>>(null to emptyList())
            } else {
                ticketRepository.observeInbox(user).map { tickets -> user to tickets }
            }
        }

    val uiState: StateFlow<InboxUiState> =
        combine(userTickets, selectedStatus) { (user, tickets), status ->
            InboxUiState(
                user = user,
                tickets = tickets,
                selectedStatus = status,
                isLoading = user == null
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), InboxUiState())

    fun onStatusSelected(status: TicketStatus?) {
        selectedStatus.value = status
    }
}
