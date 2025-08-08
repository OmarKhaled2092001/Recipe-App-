package com.example.recipeapp.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.data.models.User
import com.example.recipeapp.data.repository.MealRepository
import kotlinx.coroutines.launch
import java.security.MessageDigest

enum class AuthStatus { SUCCESS, LOADING, ERROR }

class AuthViewModel(private val repository: MealRepository) : ViewModel() {

    private val _loginStatus = MutableLiveData<Pair<AuthStatus, String>>()
    val loginStatus: LiveData<Pair<AuthStatus, String>> = _loginStatus

    private val _registrationStatus = MutableLiveData<Pair<AuthStatus, String>>()
    val registrationStatus: LiveData<Pair<AuthStatus, String>> = _registrationStatus

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginStatus.postValue(AuthStatus.LOADING to "Checking credentials...")
            val user = repository.getUserByEmail(email)
            if (user == null) {
                _loginStatus.postValue(AuthStatus.ERROR to "User not found.")
                return@launch
            }
            if (user.passwordHash == hashPassword(password)) {
                _loginStatus.postValue(AuthStatus.SUCCESS to "Login successful!")
            } else {
                _loginStatus.postValue(AuthStatus.ERROR to "Incorrect password.")
            }
        }
    }

    fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            _registrationStatus.postValue(AuthStatus.LOADING to "Creating account...")
            val existingUser = repository.getUserByEmail(email)
            if (existingUser != null) {
                _registrationStatus.postValue(AuthStatus.ERROR to "Email is already registered.")
                return@launch
            }
            val newUser = User(
                username = username,
                email = email,
                passwordHash = hashPassword(password)
            )
            repository.registerUser(newUser)
            _registrationStatus.postValue(AuthStatus.SUCCESS to "Registration successful!")
        }
    }

    private fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
}
