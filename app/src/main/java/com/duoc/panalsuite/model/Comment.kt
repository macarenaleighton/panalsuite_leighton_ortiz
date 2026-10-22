package com.duoc.panalsuite.model

data class Comment(
    val id: Long,
    val ticketId: Long,
    val authorName: String,
    val text: String,
    val isSystem: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
