package com.duoc.panalsuite.viewmodel

import com.duoc.panalsuite.model.Comment
import com.duoc.panalsuite.model.Role
import com.duoc.panalsuite.model.Ticket
import com.duoc.panalsuite.model.User

data class TicketDetailUiState(
    val ticket: Ticket? = null,
    val comments: List<Comment> = emptyList(),
    val user: User? = null,
    val requesterName: String = "",
    val assigneeName: String = "",
    val commentText: String = "",
    val showCommentError: Boolean = false,
    val isLoading: Boolean = true
) {

    val commentError: String?
        get() = validateComment(commentText)

    val canChangeStatus: Boolean
        get() = user?.role == Role.RESOLVER || user?.role == Role.SUPERVISOR

    companion object {
        const val COMMENT_MAX_LENGTH = 300

        fun validateComment(text: String): String? =
            if (text.isBlank()) "Escribe un comentario antes de enviar" else null
    }
}