package com.duoc.panalsuite.viewmodel

import com.duoc.panalsuite.model.Role
import com.duoc.panalsuite.model.Ticket
import com.duoc.panalsuite.model.TicketStatus
import com.duoc.panalsuite.model.User

data class InboxUiState(
    val user: User? = null,
    val tickets: List<Ticket> = emptyList(),
    val selectedStatus: TicketStatus? = null, // null = "Todos"
    val isLoading: Boolean = true
) {

    val visibleTickets: List<Ticket>
        get() = if (selectedStatus == null) tickets else tickets.filter { it.status == selectedStatus }

    val canCreateTickets: Boolean
        get() = user?.role == Role.REQUESTER || user?.role == Role.SUPERVISOR

    val showsSummary: Boolean
        get() = user?.role == Role.SUPERVISOR

    fun countByStatus(status: TicketStatus): Int = tickets.count { it.status == status }
}
