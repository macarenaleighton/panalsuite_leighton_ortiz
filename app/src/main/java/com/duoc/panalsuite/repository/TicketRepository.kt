package com.duoc.panalsuite.repository

import android.content.Context
import com.duoc.panalsuite.model.Category
import com.duoc.panalsuite.model.Comment
import com.duoc.panalsuite.model.Priority
import com.duoc.panalsuite.model.Role
import com.duoc.panalsuite.model.Ticket
import com.duoc.panalsuite.model.TicketStatus
import com.duoc.panalsuite.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class TicketRepository private constructor(context: Context) {

    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    private val userRepository = UserRepository()
    private val lock = Any()

    private val tickets = MutableStateFlow(loadTickets())
    private val comments = MutableStateFlow(loadComments())

    fun observeInbox(user: User): Flow<List<Ticket>> = tickets.map { allTickets ->
        val visibleTickets = when (user.role) {
            Role.REQUESTER -> allTickets.filter { it.requesterEmail == user.email }
            Role.RESOLVER -> allTickets.filter { it.assigneeEmail == user.email }
            Role.SUPERVISOR -> allTickets
        }
        visibleTickets.sortedByDescending { it.updatedAt }
    }

    fun observeTicket(ticketId: Long): Flow<Ticket?> =
        tickets.map { allTickets -> allTickets.firstOrNull { it.id == ticketId } }

    fun observeComments(ticketId: Long): Flow<List<Comment>> =
        comments.map { allComments -> allComments.filter { it.ticketId == ticketId } }

    suspend fun createTicket(
        title: String,
        description: String,
        category: Category,
        priority: Priority,
        location: String,
        author: User
    ): Long {
        val resolver = userRepository.getDefaultResolver()
        val ticket = Ticket(
            id = nextTicketId(),
            title = title.trim(),
            description = description.trim(),
            category = category,
            priority = priority,
            location = location.trim(),
            requesterEmail = author.email,
            assigneeEmail = resolver.email
        )
        tickets.value = tickets.value + ticket
        saveTickets()
        insertComment(ticket.id, author.name, "Solicitud creada y asignada a ${resolver.name}", isSystem = true)
        return ticket.id
    }

    suspend fun changeStatus(ticketId: Long, newStatus: TicketStatus, author: User) {
        val ticket = findTicket(ticketId) ?: return
        if (ticket.status == newStatus) return
        replaceTicket(ticket.copy(status = newStatus, updatedAt = System.currentTimeMillis()))
        insertComment(ticketId, author.name, "Estado: ${ticket.status.label} → ${newStatus.label}", isSystem = true)
    }

    suspend fun addComment(ticketId: Long, text: String, author: User) {
        val ticket = findTicket(ticketId) ?: return
        insertComment(ticketId, author.name, text.trim(), isSystem = false)
        replaceTicket(ticket.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun seedIfEmpty() {
        if (tickets.value.isNotEmpty()) return
        val requester = userRepository.getUsers().first { it.role == Role.REQUESTER }
        createTicket("Caja 3 no imprime boletas", "La impresora de la caja 3 no responde desde la apertura.", Category.STORE, Priority.HIGH, "Tienda Providencia", requester)
        createTicket("Sin conexión VPN en sucursal", "Los equipos no se conectan a la VPN desde las 9:00.", Category.INFRASTRUCTURE, Priority.CRITICAL, "Farmacia Maipú", requester)
        createTicket("Proveedor no recibe órdenes", "Las órdenes de compra quedan pendientes en el portal.", Category.SUPPLIER, Priority.MEDIUM, "Bodega Central", requester)
        createTicket("Solicitud de nuevo mouse", "El mouse del puesto 12 dejó de funcionar.", Category.INTERNAL_SUPPORT, Priority.LOW, "Oficina Alameda", requester)
    }


    private fun findTicket(ticketId: Long): Ticket? = tickets.value.firstOrNull { it.id == ticketId }

    private fun nextTicketId(): Long = (tickets.value.maxOfOrNull { it.id } ?: 0L) + 1

    private fun nextCommentId(): Long = (comments.value.maxOfOrNull { it.id } ?: 0L) + 1

    private suspend fun replaceTicket(updated: Ticket) {
        tickets.value = tickets.value.map { if (it.id == updated.id) updated else it }
        saveTickets()
    }

    private suspend fun insertComment(ticketId: Long, authorName: String, text: String, isSystem: Boolean) {
        val comment = Comment(
            id = nextCommentId(),
            ticketId = ticketId,
            authorName = authorName,
            text = text,
            isSystem = isSystem
        )
        comments.value = comments.value + comment
        saveComments()
    }


    private suspend fun saveTickets() {
        withContext(Dispatchers.IO) {
            synchronized(lock) {
                val json = JSONArray(tickets.value.map { it.toJson() }).toString()
                preferences.edit().putString(TICKETS_KEY, json).commit()
            }
        }
    }

    private suspend fun saveComments() {
        withContext(Dispatchers.IO) {
            synchronized(lock) {
                val json = JSONArray(comments.value.map { it.toJson() }).toString()
                preferences.edit().putString(COMMENTS_KEY, json).commit()
            }
        }
    }

    private fun loadTickets(): List<Ticket> {
        val json = preferences.getString(TICKETS_KEY, null) ?: return emptyList()
        val array = JSONArray(json)
        return List(array.length()) { index -> array.getJSONObject(index).toTicket() }
    }

    private fun loadComments(): List<Comment> {
        val json = preferences.getString(COMMENTS_KEY, null) ?: return emptyList()
        val array = JSONArray(json)
        return List(array.length()) { index -> array.getJSONObject(index).toComment() }
    }


    private fun Ticket.toJson(): JSONObject = JSONObject()
        .put("id", id)
        .put("title", title)
        .put("description", description)
        .put("category", category.name)
        .put("priority", priority.name)
        .put("status", status.name)
        .put("location", location)
        .put("requesterEmail", requesterEmail)
        .put("assigneeEmail", assigneeEmail ?: JSONObject.NULL)
        .put("createdAt", createdAt)
        .put("updatedAt", updatedAt)

    private fun JSONObject.toTicket(): Ticket = Ticket(
        id = getLong("id"),
        title = getString("title"),
        description = getString("description"),
        category = Category.valueOf(getString("category")),
        priority = Priority.valueOf(getString("priority")),
        status = TicketStatus.valueOf(getString("status")),
        location = getString("location"),
        requesterEmail = getString("requesterEmail"),
        assigneeEmail = if (isNull("assigneeEmail")) null else getString("assigneeEmail"),
        createdAt = getLong("createdAt"),
        updatedAt = getLong("updatedAt")
    )

    private fun Comment.toJson(): JSONObject = JSONObject()
        .put("id", id)
        .put("ticketId", ticketId)
        .put("authorName", authorName)
        .put("text", text)
        .put("isSystem", isSystem)
        .put("createdAt", createdAt)

    private fun JSONObject.toComment(): Comment = Comment(
        id = getLong("id"),
        ticketId = getLong("ticketId"),
        authorName = getString("authorName"),
        text = getString("text"),
        isSystem = getBoolean("isSystem"),
        createdAt = getLong("createdAt")
    )

    companion object {
        private const val PREFERENCES_NAME = "tickets"
        private const val TICKETS_KEY = "tickets"
        private const val COMMENTS_KEY = "comments"

        @Volatile
        private var instance: TicketRepository? = null

        fun getInstance(context: Context): TicketRepository =
            instance ?: synchronized(this) {
                instance ?: TicketRepository(context.applicationContext).also { instance = it }
            }
    }
}
