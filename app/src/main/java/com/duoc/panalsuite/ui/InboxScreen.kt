package com.duoc.panalsuite.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.duoc.panalsuite.model.TicketStatus
import com.duoc.panalsuite.ui.theme.color
import com.duoc.panalsuite.viewmodel.InboxUiState
import com.duoc.panalsuite.viewmodel.InboxViewModel

@Composable
fun InboxScreen(
    viewModel: InboxViewModel,
    onTicketClick: (Long) -> Unit,
    onCreateTicketClick: () -> Unit,
    onLogout: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    InboxScreenContent(
        state = state,
        onStatusSelected = { viewModel.onStatusSelected(it) },
        onTicketClick = onTicketClick,
        onCreateTicketClick = onCreateTicketClick,
        onLogout = onLogout
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InboxScreenContent(
    state: InboxUiState,
    onStatusSelected: (TicketStatus?) -> Unit,
    onTicketClick: (Long) -> Unit,
    onCreateTicketClick: () -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Bandeja", fontWeight = FontWeight.Bold)
                        val user = state.user
                        if (user != null) {
                            Text(
                                text = "${user.name} · ${user.role.label}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    TextButton(onClick = onLogout) { Text("Salir") }
                }
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = state.canCreateTickets,
                enter = scaleIn(),
                exit = scaleOut()
            ) {
                ExtendedFloatingActionButton(onClick = onCreateTicketClick) {
                    Text("Nueva solicitud")
                }
            }
        }
    ) { padding ->
        if (state.isLoading) {
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
                // Espacio abajo para que el botón flotante no tape la última tarjeta
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (state.showsSummary) {
                    item { StatusSummary(state) }
                }
                item {
                    StatusFilters(
                        selected = state.selectedStatus,
                        totalCount = state.tickets.size,
                        countByStatus = { state.countByStatus(it) },
                        onStatusSelected = onStatusSelected
                    )
                }
                if (state.visibleTickets.isEmpty()) {
                    item { EmptyInbox() }
                }
                // key le da a cada tarjeta una identidad estable, como en CartScreen
                items(state.visibleTickets, key = { it.id }) { ticket ->
                    TicketCard(
                        ticket = ticket,
                        onClick = { onTicketClick(ticket.id) },
                        modifier = Modifier.animateItem() // las tarjetas se reordenan con animación
                    )
                }
            }
        }
    }
}

@Composable
fun StatusSummary(state: InboxUiState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TicketStatus.entries.forEach { status ->
            SummaryCard(
                label = status.label,
                value = state.countByStatus(status),
                color = status.color(),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun SummaryCard(
    label: String,
    value: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    val animatedValue by animateIntAsState(targetValue = value, label = "summaryCount")
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$animatedValue",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun StatusFilters(
    selected: TicketStatus?,
    totalCount: Int,
    countByStatus: (TicketStatus) -> Int,
    onStatusSelected: (TicketStatus?) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            FilterChip(
                selected = selected == null,
                onClick = { onStatusSelected(null) },
                label = { Text("Todos ($totalCount)") }
            )
        }
        items(TicketStatus.entries) { status ->
            FilterChip(
                selected = selected == status,
                onClick = { onStatusSelected(status) },
                label = { Text("${status.label} (${countByStatus(status)})") }
            )
        }
    }
}

@Composable
fun EmptyInbox() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AppLogo(
            modifier = Modifier
                .size(56.dp)
                .alpha(0.4f)
        )
        Text(
            text = "No hay solicitudes en esta bandeja",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
fun InboxScreenPreview() {
    InboxScreenContent(
        state = InboxUiState(
            user = PreviewData.supervisor,
            tickets = PreviewData.tickets,
            isLoading = false
        ),
        onStatusSelected = {},
        onTicketClick = {},
        onCreateTicketClick = {},
        onLogout = {}
    )
}
