package com.duoc.panalsuite.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.duoc.panalsuite.model.Role
import com.duoc.panalsuite.model.User
import com.duoc.panalsuite.viewmodel.LoginUiState
import com.duoc.panalsuite.viewmodel.LoginViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LoginScreenContent(
        state = state,
        demoUsers = viewModel.demoUsers,
        demoPassword = viewModel.demoPassword,
        snackbarHostState = snackbarHostState,
        onEmailChange = { viewModel.onEmailChange(it) },
        onPasswordChange = { viewModel.onPasswordChange(it) },
        onPasswordVisibilityToggle = { viewModel.onPasswordVisibilityToggle() },
        onDemoUserClick = { viewModel.onDemoUserSelected(it) },
        onSubmit = {
            if (viewModel.submit()) {
                onLoginSuccess()
            } else {
                scope.launch {
                    val message = viewModel.uiState.value.authError ?: "Revisa los campos marcados en rojo"
                    snackbarHostState.showSnackbar(message)
                }
            }
        }
    )
}

@Composable
fun LoginScreenContent(
    state: LoginUiState,
    demoUsers: List<User>,
    demoPassword: String,
    snackbarHostState: SnackbarHostState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPasswordVisibilityToggle: () -> Unit,
    onDemoUserClick: (User) -> Unit,
    onSubmit: () -> Unit
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            AppLogo(modifier = Modifier.size(80.dp))
            Text(
                text = "Iniciar sesión",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Accede a tu bandeja de PanalSuite",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = state.email,
                onValueChange = onEmailChange,
                label = { Text("Correo") },
                singleLine = true,
                isError = state.showErrors && state.emailError != null,
                supportingText = {
                    val error = state.emailError
                    if (state.showErrors && error != null) Text(error)
                    else Text("Tu correo de PanalSuite")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.password,
                onValueChange = onPasswordChange,
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    TextButton(onClick = onPasswordVisibilityToggle) {
                        Text(if (state.isPasswordVisible) "Ocultar" else "Ver")
                    }
                },
                isError = state.showErrors && state.passwordError != null,
                supportingText = {
                    val error = state.passwordError
                    if (state.showErrors && error != null) Text(error)
                    else Text("Mínimo 6 caracteres")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onSubmit() }),
                modifier = Modifier.fillMaxWidth()
            )

            AnimatedVisibility(
                visible = state.authError != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Text(
                    text = state.authError.orEmpty(),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Button(
                onClick = onSubmit,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ingresar")
            }

            DemoUsersCard(
                users = demoUsers,
                password = demoPassword,
                onUserClick = onDemoUserClick
            )
        }
    }
}

@Composable
fun DemoUsersCard(
    users: List<User>,
    password: String,
    onUserClick: (User) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Usuarios de prueba",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(text = "Contraseña: $password", style = MaterialTheme.typography.bodySmall)
            users.forEach { user ->
                TextButton(onClick = { onUserClick(user) }) {
                    Text("${user.role.label}: ${user.email}")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreenContent(
        state = LoginUiState(email = "correo-malo", password = "123", showErrors = true),
        demoUsers = listOf(User("solicitante@panal.test", "Sofía Rojas", Role.REQUESTER)),
        demoPassword = "Panal123",
        snackbarHostState = remember { SnackbarHostState() },
        onEmailChange = {},
        onPasswordChange = {},
        onPasswordVisibilityToggle = {},
        onDemoUserClick = {},
        onSubmit = {}
    )
}
