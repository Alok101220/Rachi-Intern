package com.example.rachi_intern.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.rachi_intern.roomDb.AppDatabase
import com.example.rachi_intern.roomDb.entities.Address

class AddressRepository(context: Context) {

    private val addressDao=AppDatabase.getDatabase(context).addressDao()
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String> get() = _errorLiveData

    suspend fun insertAddress(address: Address) {
        withLoading {
            addressDao.insertAddress(address)
        }
    }

    suspend fun updateAddress(address: Address) {
        withLoading {
            addressDao.updateAddress(address)
        }
    }

    suspend fun deleteAddress(address: Address) {
        withLoading {
            addressDao.deleteAddress(address)
        }
    }

    fun getAddressByUserId(userId: Long): LiveData<List<Address>?> {
        return addressDao.getAddressByUserId(userId)

    }

    private suspend fun <T> withLoading(block: suspend () -> T): T {
        try {
            _isLoading.postValue(true)
            return block.invoke()
        } catch (e: Exception) {
            _errorLiveData.postValue("Error: ${e.message}")
            throw e
        } finally {
            _isLoading.postValue(false)
        }
    }
}
