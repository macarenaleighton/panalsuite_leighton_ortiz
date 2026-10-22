package com.duoc.panalsuite.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.panalsuite.model.User
import com.duoc.panalsuite.repository.SessionRepository
import com.duoc.panalsuite.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository = UserRepository()
    private val sessionRepository = SessionRepository.getInstance(application)

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    val demoUsers: List<User> = userRepository.getUsers()
    val demoPassword: String = UserRepository.DEMO_PASSWORD

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(email = value.trim(), authError = null)
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(password = value, authError = null)
    }

    fun onPasswordVisibilityToggle() {
        _uiState.value = _uiState.value.copy(isPasswordVisible = !_uiState.value.isPasswordVisible)
    }

    fun onDemoUserSelected(user: User) {
        _uiState.value = _uiState.value.copy(email = user.email, password = demoPassword, authError = null)
    }

    fun submit(): Boolean {
        _uiState.value = _uiState.value.copy(showErrors = true, authError = null)
        val state = _uiState.value
        if (!state.isValid) return false

        val user = userRepository.authenticate(state.email, state.password)
        if (user == null) {
            _uiState.value = state.copy(authError = "Correo o contraseña incorrectos")
            return false
        }

        viewModelScope.launch { sessionRepository.saveSession(user) }
        return true
    }

    fun reset() {
        _uiState.value = LoginUiState()
    }
}
