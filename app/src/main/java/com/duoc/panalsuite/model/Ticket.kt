package com.duoc.panalsuite.model

data class Ticket(
    val id: Long,
    val title: String,
    val description: String,
    val category: Category,
    val priority: Priority,
    val status: TicketStatus = TicketStatus.OPEN,
    val location: String,
    val requesterEmail: String,
    val assigneeEmail: String?,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
