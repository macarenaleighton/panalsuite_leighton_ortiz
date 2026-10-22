package com.duoc.panalsuite.viewmodel

import com.duoc.panalsuite.model.Category
import com.duoc.panalsuite.model.Priority

data class TicketFormUiState(
    val title: String = "",
    val description: String = "",
    val location: String = "",
    val category: Category? = null,
    val priority: Priority? = null,
    val isSaving: Boolean = false,
    val showErrors: Boolean = false
) {

    val titleError: String?
        get() = if (title.trim().length < 5) "El título debe tener al menos 5 caracteres" else null

    val descriptionError: String?
        get() = if (description.trim().length < 10) "Describe el problema en al menos 10 caracteres" else null

    val locationError: String?
        get() = if (location.trim().length < 3) "Ingresa la sucursal o ubicación" else null

    val categoryError: String?
        get() = if (category == null) "Selecciona una categoría" else null

    val priorityError: String?
        get() = if (priority == null) "Selecciona una prioridad" else null

    val isValid: Boolean
        get() = titleError == null &&
                descriptionError == null &&
                locationError == null &&
                categoryError == null &&
                priorityError == null

    companion object {
        const val TITLE_MAX_LENGTH = 60
        const val DESCRIPTION_MAX_LENGTH = 500
    }
}
