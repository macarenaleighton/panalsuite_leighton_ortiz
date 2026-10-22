package com.duoc.panalsuite.ui.theme

import androidx.compose.ui.graphics.Color
import com.duoc.panalsuite.model.Priority
import com.duoc.panalsuite.model.TicketStatus

val Lavender = Color(0xFF6E56A8)
val LavenderLight = Color(0xFFE9DDFF)
val Lilac = Color(0xFF8B74B8)
val LilacLight = Color(0xFFF1E8FF)
val Mist = Color(0xFFFBF8FF)
val MistVariant = Color(0xFFEDE6F7)
val Plum = Color(0xFF221B30)
val PlumVariant = Color(0xFF4A4458)

val NightPlum = Color(0xFF1C1A22)
val NightPlumVariant = Color(0xFF2B2733)
val LavenderPastel = Color(0xFFCFBCFF)

val StatusOpen = Color(0xFF4A6FD1)
val StatusInProgress = Color(0xFFD9822B)
val StatusResolved = Color(0xFF3A8F5C)
val StatusClosed = Color(0xFF7A7585)

val PriorityLow = Color(0xFF5E9A78)
val PriorityMedium = Color(0xFF5B7FC7)
val PriorityHigh = Color(0xFFD9822B)
val PriorityCritical = Color(0xFFC9485B)

fun TicketStatus.color(): Color = when (this) {
    TicketStatus.OPEN -> StatusOpen
    TicketStatus.IN_PROGRESS -> StatusInProgress
    TicketStatus.RESOLVED -> StatusResolved
    TicketStatus.CLOSED -> StatusClosed
}

fun Priority.color(): Color = when (this) {
    Priority.LOW -> PriorityLow
    Priority.MEDIUM -> PriorityMedium
    Priority.HIGH -> PriorityHigh
    Priority.CRITICAL -> PriorityCritical
}