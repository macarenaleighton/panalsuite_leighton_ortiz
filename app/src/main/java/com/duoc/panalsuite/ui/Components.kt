package com.duoc.panalsuite.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.duoc.panalsuite.R
import com.duoc.panalsuite.model.Priority
import com.duoc.panalsuite.model.Ticket
import com.duoc.panalsuite.model.TicketStatus
import com.duoc.panalsuite.ui.theme.color
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val dateFormatter = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.forLanguageTag("es-CL"))

fun Long.toDisplayDate(): String = dateFormatter.format(Date(this))

@Composable
fun AppLogo(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.logo),
        contentDescription = "Logo PanalSuite",
        modifier = modifier
    )
}

@Composable
fun LabelChip(
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    val animatedColor by animateColorAsState(targetValue = color, label = "labelChipColor")
    Text(
        text = text,
        color = animatedColor,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(animatedColor.copy(alpha = 0.14f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}

@Composable
fun StatusChip(status: TicketStatus) {
    LabelChip(text = status.label, color = status.color())
}

@Composable
fun PriorityChip(priority: Priority) {
    LabelChip(text = "Prioridad ${priority.label}", color = priority.color())
}

@Composable
fun TicketCard(
    ticket: Ticket,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 6.dp)
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(ticket.priority.color())
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "#${ticket.id} · ${ticket.category.label}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = ticket.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatusChip(ticket.status)
                    PriorityChip(ticket.priority)
                }
                Text(
                    text = "${ticket.location} · ${ticket.updatedAt.toDisplayDate()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TicketCardPreview() {
    TicketCard(ticket = PreviewData.tickets.first(), onClick = {})
}