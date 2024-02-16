package com.example.rachi_intern.repository
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.rachi_intern.roomDb.AppDatabase
import com.example.rachi_intern.roomDb.dao.UserDao
import com.example.rachi_intern.roomDb.entities.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository( context: Context) {

    private val userDao = AppDatabase.getDatabase(context).userDao()

    private val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String> get() = _errorLiveData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isInserted = MutableLiveData<Boolean>()
    val isInserted: LiveData<Boolean> get() = _isInserted
    val allUsers: LiveData<List<User>> = userDao.getAllUsers()

    suspend fun insertUser(user: User) {
        withContext(Dispatchers.Main) {
            _isLoading.postValue(true)
        }
        withContext(Dispatchers.IO) {
            try {
                userDao.insertUser(user)
                withContext(Dispatchers.Main) {
                    _isInserted.postValue(true)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _isInserted.postValue(false)
                    _errorLiveData.postValue("Error inserting user: ${e.message}")
                }
            } finally {
                withContext(Dispatchers.Main) {
                    _isLoading.postValue(false)
                }
            }
        }
    }


    suspend fun updateUser(user: User) {
        _isLoading.postValue(true)
        withContext(Dispatchers.IO) {
            try {
                userDao.updateUser(user)
            } catch (e: Exception) {
                _errorLiveData.postValue("Error updating user: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    suspend fun deleteUser(user: User) {
        _isLoading.postValue(true)
        withContext(Dispatchers.IO) {
            try {
                userDao.deleteUser(user)
            } catch (e: Exception) {
                _errorLiveData.postValue("Error deleting user: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun getUserById(userId: Long): LiveData<User?> {
        return userDao.getUserById(userId)
    }

    fun getUserByEmail(email: String): LiveData<User?> {
        return userDao.getUserByEmail(email)
    }
    fun getUserByEmailAndPassword(email: String,password:String):LiveData<User?>{
        return userDao.getUserByEmailAndPassword(email, password)
    }
}