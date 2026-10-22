package com.duoc.panalsuite.ui.navigation

object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val INBOX = "inbox"
    const val TICKET_FORM = "ticketForm"
    const val TICKET_DETAIL = "ticketDetail/{ticketId}"

    fun ticketDetail(ticketId: Long) = "ticketDetail/$ticketId"
}
