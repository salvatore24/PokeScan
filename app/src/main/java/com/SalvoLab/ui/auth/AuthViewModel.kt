package com.SalvoLab.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.SalvoLab.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val error: String? = null,
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
)

sealed interface AuthEvent {
    data class EmailChanged(val value: String) : AuthEvent
    data class PasswordChanged(val value: String) : AuthEvent
    data class ConfirmPasswordChanged(val value: String) : AuthEvent
    data object LoginSubmitted : AuthEvent
    data object RegisterSubmitted : AuthEvent
    data class GoogleSignIn(val idToken: String) : AuthEvent
    data object AnonymousSignIn : AuthEvent
    data object ErrorDismissed : AuthEvent
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        authRepository.currentUser
            .onEach { user -> _uiState.update { it.copy(isAuthenticated = user != null) } }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.EmailChanged -> _uiState.update { it.copy(email = event.value, emailError = null) }
            is AuthEvent.PasswordChanged -> _uiState.update { it.copy(password = event.value, passwordError = null) }
            is AuthEvent.ConfirmPasswordChanged -> _uiState.update { it.copy(confirmPassword = event.value, confirmPasswordError = null) }
            is AuthEvent.LoginSubmitted -> loginWithEmail()
            is AuthEvent.RegisterSubmitted -> registerWithEmail()
            is AuthEvent.GoogleSignIn -> signInWithGoogle(event.idToken)
            is AuthEvent.AnonymousSignIn -> signInAnonymously()
            is AuthEvent.ErrorDismissed -> _uiState.update { it.copy(error = null) }
        }
    }

    private fun loginWithEmail() {
        if (!validateLoginForm()) return
        launchAuth { authRepository.signInWithEmail(_uiState.value.email.trim(), _uiState.value.password) }
    }

    private fun registerWithEmail() {
        if (!validateRegisterForm()) return
        launchAuth { authRepository.registerWithEmail(_uiState.value.email.trim(), _uiState.value.password) }
    }

    private fun signInWithGoogle(idToken: String) = launchAuth { authRepository.signInWithGoogle(idToken) }
    private fun signInAnonymously() = launchAuth { authRepository.signInAnonymously() }

    private fun launchAuth(block: suspend () -> Result<*>) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            block()
                .onFailure { t -> _uiState.update { it.copy(isLoading = false, error = t.localizedMessage ?: "Errore sconosciuto") } }
                .onSuccess { _uiState.update { it.copy(isLoading = false) } }
        }
    }

    private fun validateLoginForm(): Boolean {
        var valid = true
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(_uiState.value.email.trim()).matches()) {
            _uiState.update { it.copy(emailError = "Email non valida") }; valid = false
        }
        if (_uiState.value.password.length < 6) {
            _uiState.update { it.copy(passwordError = "Minimo 6 caratteri") }; valid = false
        }
        return valid
    }

    private fun validateRegisterForm(): Boolean {
        var valid = validateLoginForm()
        if (_uiState.value.password != _uiState.value.confirmPassword) {
            _uiState.update { it.copy(confirmPasswordError = "Le password non coincidono") }; valid = false
        }
        return valid
    }
}
