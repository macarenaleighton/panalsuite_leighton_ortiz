package com.duoc.panalsuite.repository

import com.duoc.panalsuite.model.Role
import com.duoc.panalsuite.model.User

class UserRepository {

    fun getUsers(): List<User> = listOf(
        User("solicitante@panal.test", "Sofía Rojas", Role.REQUESTER),
        User("resolutor@panal.test", "Diego Muñoz", Role.RESOLVER),
        User("supervisor@panal.test", "Carla Pérez", Role.SUPERVISOR)
    )

    fun findByEmail(email: String): User? =
        getUsers().firstOrNull { it.email.equals(email.trim(), ignoreCase = true) }

    fun authenticate(email: String, password: String): User? =
        findByEmail(email)?.takeIf { password == DEMO_PASSWORD }

    fun getDefaultResolver(): User = getUsers().first { it.role == Role.RESOLVER }

    companion object {
        const val DEMO_PASSWORD = "Panal123"
    }
}
