package com.duoc.panalsuite.viewmodel

import com.duoc.panalsuite.model.User

sealed interface SessionState {
    data object Loading : SessionState
    data object LoggedOut : SessionState
    data class LoggedIn(val user: User) : SessionState
}
