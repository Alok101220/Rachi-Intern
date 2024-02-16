package com.example.rachi_intern.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rachi_intern.repository.UserRepository
import com.example.rachi_intern.roomDb.entities.User
import kotlinx.coroutines.launch

class UserViewModel(context: Context) : ViewModel() {

    private val userRepository =UserRepository(context)

    val errorLiveData: LiveData<String> = userRepository.errorLiveData
    val isLoading: LiveData<Boolean> = userRepository.isLoading
    val allUsers: LiveData<List<User>> = userRepository.allUsers
    val isUserInserted :LiveData<Boolean> = userRepository.isInserted

    fun insertUser(user: User) {
        viewModelScope.launch {
            userRepository.insertUser(user)
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            userRepository.updateUser(user)
        }
    }

    fun deleteUser(user: User) {
        viewModelScope.launch {
            userRepository.deleteUser(user)
        }
    }

    fun getUserById(userId: Long): LiveData<User?> {
        return userRepository.getUserById(userId)
    }

    fun getUserByEmail(email: String): LiveData<User?> {
        return userRepository.getUserByEmail(email)
    }
    fun getUserByEmailAndPassword(email: String,password:String):LiveData<User?>{
        return userRepository.getUserByEmailAndPassword(email, password)
    }
}
