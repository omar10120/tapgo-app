package app.taplinks.vendor.ui.screens.services

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.taplinks.vendor.network.ApiException
import app.taplinks.vendor.network.api.VendorApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ServicesUiState(
    val services: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val showAddEditDialog: Boolean = false,
    val dialogMode: DialogMode = DialogMode.Add,
    val currentServiceName: String = "",
    val editingIndex: Int = -1
)

enum class DialogMode {
    Add,
    Edit
}

class ServicesViewModel(
    private val vendorApiService: VendorApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(ServicesUiState())
    val uiState: StateFlow<ServicesUiState> = _uiState.asStateFlow()

    fun loadServices() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            vendorApiService.getServices().fold(
                onSuccess = { services ->
                    _uiState.value = _uiState.value.copy(
                        services = services,
                        isLoading = false
                    )
                },
                onFailure = { exception ->
                    val errorMsg = when (exception) {
                        is ApiException -> exception.message ?: "Failed to load services"
                        else -> "Failed to load services. Please try again."
                    }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = errorMsg
                    )
                }
            )
        }
    }

    fun openAddDialog() {
        _uiState.value = _uiState.value.copy(
            showAddEditDialog = true,
            dialogMode = DialogMode.Add,
            currentServiceName = "",
            editingIndex = -1,
            errorMessage = null,
            successMessage = null
        )
    }

    fun openEditDialog(serviceName: String, index: Int) {
        _uiState.value = _uiState.value.copy(
            showAddEditDialog = true,
            dialogMode = DialogMode.Edit,
            currentServiceName = serviceName,
            editingIndex = index,
            errorMessage = null,
            successMessage = null
        )
    }

    fun closeDialog() {
        _uiState.value = _uiState.value.copy(
            showAddEditDialog = false,
            currentServiceName = "",
            editingIndex = -1,
            errorMessage = null
        )
    }

    fun updateCurrentServiceName(name: String) {
        _uiState.value = _uiState.value.copy(currentServiceName = name)
    }

    fun validateAndSaveService() {
        val currentState = _uiState.value
        val trimmedName = currentState.currentServiceName.trim()

        val validationError = validateServiceName(
            serviceName = trimmedName,
            services = currentState.services,
            mode = currentState.dialogMode,
            editingIndex = currentState.editingIndex
        )

        if (validationError != null) {
            _uiState.value = _uiState.value.copy(errorMessage = validationError)
            return
        }

        when (currentState.dialogMode) {
            DialogMode.Add -> addService(trimmedName)
            DialogMode.Edit -> updateService(
                oldName = currentState.services[currentState.editingIndex],
                newName = trimmedName
            )
        }
    }

    private fun validateServiceName(
        serviceName: String,
        services: List<String>,
        mode: DialogMode,
        editingIndex: Int
    ): String? {
        if (serviceName.isEmpty()) {
            return "Service name cannot be empty"
        }

        if (serviceName.length > 50) {
            return "Service name cannot exceed 50 characters"
        }

        val isDuplicate = services.any { existingService ->
            val index = services.indexOf(existingService)
            existingService.equals(serviceName, ignoreCase = true) &&
                    (mode == DialogMode.Add || index != editingIndex)
        }

        if (isDuplicate) {
            return "This service already exists"
        }

        return null
    }

    private fun addService(serviceName: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSaving = true,
                errorMessage = null
            )

            vendorApiService.addService(serviceName).fold(
                onSuccess = { updatedServices ->
                    _uiState.value = _uiState.value.copy(
                        services = updatedServices,
                        isSaving = false,
                        showAddEditDialog = false,
                        currentServiceName = "",
                        successMessage = "Service added successfully"
                    )
                },
                onFailure = { exception ->
                    val errorMsg = when (exception) {
                        is ApiException -> exception.message ?: "Failed to add service"
                        else -> "Failed to add service"
                    }
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        errorMessage = errorMsg
                    )
                }
            )
        }
    }

    private fun updateService(oldName: String, newName: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSaving = true,
                errorMessage = null
            )

            vendorApiService.updateService(oldName, newName).fold(
                onSuccess = { updatedServices ->
                    _uiState.value = _uiState.value.copy(
                        services = updatedServices,
                        isSaving = false,
                        showAddEditDialog = false,
                        currentServiceName = "",
                        editingIndex = -1,
                        successMessage = "Service updated successfully"
                    )
                },
                onFailure = { exception ->
                    val errorMsg = when (exception) {
                        is ApiException -> exception.message ?: "Failed to update service"
                        else -> "Failed to update service"
                    }
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        errorMessage = errorMsg
                    )
                }
            )
        }
    }

    fun deleteService(serviceName: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                errorMessage = null,
                successMessage = null
            )

            vendorApiService.deleteService(serviceName).fold(
                onSuccess = { updatedServices ->
                    _uiState.value = _uiState.value.copy(
                        services = updatedServices,
                        successMessage = "Service deleted successfully"
                    )
                },
                onFailure = { exception ->
                    val errorMsg = when (exception) {
                        is ApiException -> exception.message ?: "Failed to delete service"
                        else -> "Failed to delete service"
                    }
                    _uiState.value = _uiState.value.copy(
                        errorMessage = errorMsg
                    )
                }
            )
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null,
            successMessage = null
        )
    }
}
