package com.duoc.panalsuite.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.duoc.panalsuite.model.Category
import com.duoc.panalsuite.model.Priority
import com.duoc.panalsuite.viewmodel.TicketFormUiState
import com.duoc.panalsuite.viewmodel.TicketFormViewModel
import kotlinx.coroutines.launch

@Composable
fun TicketFormScreen(
    viewModel: TicketFormViewModel,
    onTicketCreated: (Long) -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    TicketFormScreenContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onTitleChange = { viewModel.onTitleChange(it) },
        onDescriptionChange = { viewModel.onDescriptionChange(it) },
        onLocationChange = { viewModel.onLocationChange(it) },
        onCategoryChange = { viewModel.onCategoryChange(it) },
        onPriorityChange = { viewModel.onPriorityChange(it) },
        onSubmit = {
            if (viewModel.submit()) {
                viewModel.saveTicket(onSaved = onTicketCreated)
            } else {
                scope.launch {
                    snackbarHostState.showSnackbar("Revisa los campos marcados en rojo")
                }
            }
        },
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketFormScreenContent(
    state: TicketFormUiState,
    snackbarHostState: SnackbarHostState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onLocationChange: (String) -> Unit,
    onCategoryChange: (Category) -> Unit,
    onPriorityChange: (Priority) -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva solicitud", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Volver") }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            OutlinedTextField(
                value = state.title,
                onValueChange = onTitleChange,
                label = { Text("Título") },
                singleLine = true,
                isError = state.showErrors && state.titleError != null,
                supportingText = {
                    val error = state.titleError
                    if (state.showErrors && error != null) Text(error)
                    else Text("${state.title.length} / ${TicketFormUiState.TITLE_MAX_LENGTH}")
                },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.description,
                onValueChange = onDescriptionChange,
                label = { Text("Descripción") },
                minLines = 3,
                isError = state.showErrors && state.descriptionError != null,
                supportingText = {
                    val error = state.descriptionError
                    if (state.showErrors && error != null) Text(error)
                    else Text("${state.description.length} / ${TicketFormUiState.DESCRIPTION_MAX_LENGTH}")
                },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.location,
                onValueChange = onLocationChange,
                label = { Text("Sucursal o ubicación") },
                singleLine = true,
                isError = state.showErrors && state.locationError != null,
                supportingText = {
                    val error = state.locationError
                    if (state.showErrors && error != null) Text(error)
                    else Text("Ejemplo: Tienda Providencia")
                },
                modifier = Modifier.fillMaxWidth()
            )

            OptionSelector(
                title = "Categoría",
                options = Category.entries,
                selected = state.category,
                optionLabel = { it.label },
                onSelect = onCategoryChange,
                error = if (state.showErrors) state.categoryError else null
            )

            OptionSelector(
                title = "Prioridad",
                options = Priority.entries,
                selected = state.priority,
                optionLabel = { it.label },
                onSelect = onPriorityChange,
                error = if (state.showErrors) state.priorityError else null
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onSubmit,
                enabled = !state.isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Crossfade(targetState = state.isSaving, label = "saveButton") { isSaving ->
                    if (isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Crear solicitud")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun <T> OptionSelector(
    title: String,
    options: List<T>,
    selected: T?,
    optionLabel: (T) -> String,
    onSelect: (T) -> Unit,
    error: String?
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleSmall)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            options.forEach { option ->
                FilterChip(
                    selected = option == selected,
                    onClick = { onSelect(option) },
                    label = { Text(optionLabel(option)) }
                )
            }
        }
        AnimatedVisibility(visible = error != null) {
            Text(
                text = error.orEmpty(),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TicketFormScreenPreview() {
    TicketFormScreenContent(
        state = TicketFormUiState(title = "Caja", showErrors = true),
        snackbarHostState = remember { SnackbarHostState() },
        onTitleChange = {},
        onDescriptionChange = {},
        onLocationChange = {},
        onCategoryChange = {},
        onPriorityChange = {},
        onSubmit = {},
        onBack = {}
    )
}
