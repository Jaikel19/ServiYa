package com.example.shared.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared.data.repository.Auth.IAuthRepository
import com.example.shared.data.repository.User.IUserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: IAuthRepository,
    private val userRepository: IUserRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.value =
            _uiState.value.copy(
                email = value,
                errorMessage = null,
            )
    }

    fun onPasswordChange(value: String) {
        _uiState.value =
            _uiState.value.copy(
                password = value,
                errorMessage = null,
            )
    }

    fun login() {
        val current = _uiState.value
        val email = current.email.trim()
        val password = current.password

        if (email.isBlank()) {
            _uiState.value = current.copy(errorMessage = "Debes ingresar tu correo.")
            return
        }

        if (password.isBlank()) {
            _uiState.value = current.copy(errorMessage = "Debes ingresar tu contraseña.")
            return
        }

        viewModelScope.launch {
            _uiState.value =
                _uiState.value.copy(
                    isLoading = true,
                    errorMessage = null,
                    loggedUser = null,
                )

            try {
                val authUid = authRepository.signIn(email, password)
                val resolvedUser = userRepository.getUserById(authUid)

                if (resolvedUser == null) {
                    authRepository.signOut()
                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "No existe un perfil válido en Firestore para este usuario.",
                        )
                    return@launch
                }

                if (resolvedUser.role.isBlank()) {
                    authRepository.signOut()
                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "El perfil no tiene un rol válido asignado.",
                        )
                    return@launch
                }

                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        password = "",
                        loggedUser = resolvedUser,
                        errorMessage = null,
                    )
            } catch (e: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        errorMessage = mapLoginError(e),
                    )
            }
        }
    }

    fun consumeLoginSuccess() {
        _uiState.value = _uiState.value.copy(loggedUser = null)
    }

    private fun mapLoginError(error: Throwable): String {
        val raw = error.message.orEmpty()
        val normalized = raw.lowercase()

        return when {
            "invalid login credentials" in normalized -> "Correo o contraseña incorrectos."
            "password is invalid" in normalized -> "Correo o contraseña incorrectos."
            "no user record" in normalized -> "Correo o contraseña incorrectos."
            "network" in normalized -> "Hubo un problema de red. Intenta nuevamente."
            raw.isNotBlank() -> raw
            else -> "No se pudo iniciar sesión."
        }
    }
}