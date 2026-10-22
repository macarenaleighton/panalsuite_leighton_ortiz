package com.duoc.panalsuite.ui

import com.duoc.panalsuite.model.Category
import com.duoc.panalsuite.model.Comment
import com.duoc.panalsuite.model.Priority
import com.duoc.panalsuite.model.Role
import com.duoc.panalsuite.model.Ticket
import com.duoc.panalsuite.model.TicketStatus
import com.duoc.panalsuite.model.User

object PreviewData {

    val requester = User("solicitante@panal.test", "Sofía Rojas", Role.REQUESTER)
    val resolver = User("resolutor@panal.test", "Diego Muñoz", Role.RESOLVER)
    val supervisor = User("supervisor@panal.test", "Carla Pérez", Role.SUPERVISOR)

    val tickets = listOf(
        Ticket(
            id = 1,
            title = "Caja 3 no imprime boletas",
            description = "La impresora de la caja 3 no responde desde la apertura.",
            category = Category.STORE,
            priority = Priority.HIGH,
            status = TicketStatus.OPEN,
            location = "Tienda Providencia",
            requesterEmail = requester.email,
            assigneeEmail = resolver.email
        ),
        Ticket(
            id = 2,
            title = "Sin conexión VPN en sucursal",
            description = "Los equipos no se conectan a la VPN desde las 9:00.",
            category = Category.INFRASTRUCTURE,
            priority = Priority.CRITICAL,
            status = TicketStatus.IN_PROGRESS,
            location = "Farmacia Maipú",
            requesterEmail = requester.email,
            assigneeEmail = resolver.email
        ),
        Ticket(
            id = 3,
            title = "Proveedor no recibe órdenes",
            description = "Las órdenes de compra quedan pendientes en el portal.",
            category = Category.SUPPLIER,
            priority = Priority.MEDIUM,
            status = TicketStatus.RESOLVED,
            location = "Bodega Central",
            requesterEmail = requester.email,
            assigneeEmail = resolver.email
        )
    )

    val comments = listOf(
        Comment(id = 1, ticketId = 1, authorName = requester.name, text = "Solicitud creada y asignada a ${resolver.name}", isSystem = true),
        Comment(id = 2, ticketId = 1, authorName = resolver.name, text = "Voy en camino a revisar la impresora.")
    )
}
