package com.duoc.panalsuite.repository

import android.content.Context
import com.duoc.panalsuite.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class SessionRepository private constructor(context: Context) {

    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    private val userRepository = UserRepository()
    private val lock = Any()

    private val _currentUser = MutableStateFlow(
        preferences.getString(EMAIL_KEY, null)?.let { email -> userRepository.findByEmail(email) }
    )
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    suspend fun saveSession(user: User) {
        _currentUser.value = user
        persist()
    }

    suspend fun clearSession() {
        _currentUser.value = null
        persist()
    }

    private suspend fun persist() {
        withContext(Dispatchers.IO) {
            synchronized(lock) {
                val email = _currentUser.value?.email
                val editor = preferences.edit()
                if (email == null) editor.remove(EMAIL_KEY) else editor.putString(EMAIL_KEY, email)
                editor.commit()
            }
        }
    }

    companion object {
        private const val PREFERENCES_NAME = "session"
        private const val EMAIL_KEY = "email"

        @Volatile
        private var instance: SessionRepository? = null

        fun getInstance(context: Context): SessionRepository =
            instance ?: synchronized(this) {
                instance ?: SessionRepository(context.applicationContext).also { instance = it }
            }
    }
}
