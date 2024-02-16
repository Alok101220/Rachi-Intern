package com.example.rachi_intern.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.rachi_intern.roomDb.AppDatabase
import com.example.rachi_intern.roomDb.entities.Order

class OrderRepository(context: Context) {

    private val orderDao=AppDatabase.getDatabase(context).orderDao()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String> get() = _errorLiveData


    suspend fun insertOrder(order: Order){
        withLoading {
            orderDao.insertOrder(order)
        }
    }

    fun getAllOrderByUser(userId:Long):LiveData<List<Order>?>{
        return orderDao.getOrderByUser(userId)
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