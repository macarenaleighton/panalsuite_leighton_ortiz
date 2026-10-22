package com.duoc.panalsuite.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.duoc.panalsuite.model.Comment
import com.duoc.panalsuite.model.TicketStatus
import com.duoc.panalsuite.viewmodel.TicketDetailUiState
import com.duoc.panalsuite.viewmodel.TicketDetailViewModel

@Composable
fun TicketDetailScreen(
    viewModel: TicketDetailViewModel,
    ticketId: Long,
    onBack: () -> Unit
) {
    LaunchedEffect(ticketId) {
        viewModel.loadTicket(ticketId)
    }
    val state by viewModel.uiState.collectAsState()

    TicketDetailScreenContent(
        state = state,
        onStatusChange = { viewModel.changeStatus(it) },
        onCommentChange = { viewModel.onCommentChange(it) },
        onSendComment = { viewModel.sendComment() },
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketDetailScreenContent(
    state: TicketDetailUiState,
    onStatusChange: (TicketStatus) -> Unit,
    onCommentChange: (String) -> Unit,
    onSendComment: () -> Unit,
    onBack: () -> Unit
) {
    val ticket = state.ticket

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (ticket != null) "Solicitud #${ticket.id}" else "Solicitud") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Volver") }
                }
            )
        },
        bottomBar = {
            CommentInput(
                text = state.commentText,
                error = if (state.showCommentError) state.commentError else null,
                onTextChange = onCommentChange,
                onSend = onSendComment
            )
        }
    ) { padding ->
        if (state.isLoading || ticket == null) {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { TicketHeader(state) }

                if (state.canChangeStatus) {
                    item {
                        StatusActions(current = ticket.status, onStatusChange = onStatusChange)
                    }
                }

                item {
                    Text(
                        text = "Trazabilidad y comentarios",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                items(state.comments, key = { it.id }) { comment ->
                    CommentItem(comment = comment, modifier = Modifier.animateItem())
                }
            }
        }
    }
}

@Composable
fun TicketHeader(state: TicketDetailUiState) {
    val ticket = state.ticket ?: return
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = ticket.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusChip(ticket.status)
                PriorityChip(ticket.priority)
            }
            Text(text = ticket.description, style = MaterialTheme.typography.bodyMedium)
            HorizontalDivider()
            InfoRow(label = "Categoría", value = ticket.category.label)
            InfoRow(label = "Ubicación", value = ticket.location)
            InfoRow(label = "Solicitante", value = state.requesterName)
            InfoRow(label = "Responsable", value = state.assigneeName)
            InfoRow(label = "Creada", value = ticket.createdAt.toDisplayDate())
            InfoRow(label = "Actualizada", value = ticket.updatedAt.toDisplayDate())
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(100.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StatusActions(
    current: TicketStatus,
    onStatusChange: (TicketStatus) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(text = "Cambiar estado", style = MaterialTheme.typography.titleSmall)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TicketStatus.entries.forEach { status ->
                FilterChip(
                    selected = status == current,
                    onClick = { onStatusChange(status) },
                    label = { Text(status.label) }
                )
            }
        }
    }
}

@Composable
fun CommentItem(comment: Comment, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 6.dp)
                .size(10.dp)
                .clip(CircleShape)
                .background(
                    if (comment.isSystem) MaterialTheme.colorScheme.secondary
                    else MaterialTheme.colorScheme.primary
                )
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = "${comment.authorName} · ${comment.createdAt.toDisplayDate()}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = comment.text,
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = if (comment.isSystem) FontStyle.Italic else FontStyle.Normal
            )
        }
    }
}

@Composable
fun CommentInput(
    text: String,
    error: String?,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Surface(tonalElevation = 3.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = onTextChange,
                placeholder = { Text("Escribe un comentario…") },
                isError = error != null,
                supportingText = { if (error != null) Text(error) },
                maxLines = 3,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = onSend) { Text("Enviar") }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TicketDetailScreenPreview() {
    TicketDetailScreenContent(
        state = TicketDetailUiState(
            ticket = PreviewData.tickets.first(),
            comments = PreviewData.comments,
            user = PreviewData.resolver,
            requesterName = PreviewData.requester.name,
            assigneeName = PreviewData.resolver.name,
            isLoading = false
        ),
        onStatusChange = {},
        onCommentChange = {},
        onSendComment = {},
        onBack = {}
    )
}
