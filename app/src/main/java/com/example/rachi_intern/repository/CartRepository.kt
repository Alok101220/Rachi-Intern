package com.example.rachi_intern.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.rachi_intern.roomDb.AppDatabase
import com.example.rachi_intern.roomDb.entities.CartItem

class CartRepository(context: Context) {

    private val cartDao = AppDatabase.getDatabase(context).cartDao()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String> get() = _errorLiveData

    suspend fun insertCartItem(cartItem: CartItem) {
        withLoading {
            cartDao.insertCartItem(cartItem)
        }
    }

    suspend fun updateCartItem(cartItem: CartItem) {
        withLoading {
            cartDao.updateCartItem(cartItem)
        }
    }

    suspend fun deleteCartItem(cartItem: CartItem) {
        withLoading {
            cartDao.deleteCartItem(cartItem)
        }
    }

    fun getCartItemByUser(userId: Long): LiveData<List<CartItem>?> {
        return cartDao.getCartItemByUser(userId)

    }

    fun getCartItemByUserAndProduct(userId: Long, productId: Long): LiveData<CartItem?> {
        return cartDao.getCartItemByUserAndProduct(userId, productId)

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
