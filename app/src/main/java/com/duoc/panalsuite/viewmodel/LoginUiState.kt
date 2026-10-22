package com.duoc.panalsuite.viewmodel

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val authError: String? = null,
    val showErrors: Boolean = false
) {

    val emailError: String?
        get() = if (!EMAIL_PATTERN.matches(email.trim())) "Correo no válido. Ejemplo: nombre@panal.test" else null

    val passwordError: String?
        get() = if (password.length < 6) "La contraseña debe tener al menos 6 caracteres" else null

    val isValid: Boolean
        get() = emailError == null && passwordError == null

    companion object {
        private val EMAIL_PATTERN =
            Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    }
}
