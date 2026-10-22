package com.duoc.panalsuite.model

enum class TicketStatus(val label: String) {
    OPEN("Abierto"),
    IN_PROGRESS("En progreso"),
    RESOLVED("Resuelto"),
    CLOSED("Cerrado")
}