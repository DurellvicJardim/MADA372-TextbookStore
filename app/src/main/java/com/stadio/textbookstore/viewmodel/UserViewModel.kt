package com.stadio.textbookstore.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.stadio.textbookstore.data.BookStoreRepository
import com.stadio.textbookstore.data.User

//Manages currently logged-in user, login, registration, logout, and profile updates
class UserViewModel : ViewModel() {

    private val _currentUser = MutableLiveData<User?>(BookStoreRepository.getCurrentUser())
    val currentUser: LiveData<User?> = _currentUser

    private val _authError = MutableLiveData<String?>()
    val authError: LiveData<String?> = _authError

    //Return true on successful login
    fun login(email: String, password: String): Boolean {
        val user = BookStoreRepository.loginUser(email, password)
        return if (user != null) {
            BookStoreRepository.setCurrentUser(user)
            _currentUser.value = user
            _authError.value = null
            true
        } else {
            _authError.value = "Invalid email or password."
            false
        }
    }

    //return true on successful registration
    fun register(
        fullName: String, email: String, password: String,
        institution: String, course: String, studentStatus: String
    ): Boolean {
        val ok = BookStoreRepository.registerUser(
            fullName, email, password, institution, course, studentStatus
        )
        return if (ok) {
            login(email, password)   //auto-login on success
        } else {
            _authError.value = "An account with that email already exists."
            false
        }
    }

    fun logout() {
        BookStoreRepository.setCurrentUser(null)
        _currentUser.value = null
    }

    fun updateProfile(updated: User) {
        BookStoreRepository.updateUser(updated)
        _currentUser.value = updated
    }

    fun clearError() {
        _authError.value = null
    }
}