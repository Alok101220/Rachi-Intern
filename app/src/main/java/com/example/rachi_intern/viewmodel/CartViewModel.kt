package com.example.rachi_intern.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rachi_intern.repository.CartRepository
import com.example.rachi_intern.roomDb.entities.CartItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CartViewModel(context: Context) : ViewModel() {

    private val repository= CartRepository(context)

    val isLoading= repository.isLoading
    val errorLiveData= repository.errorLiveData


    fun insertCartItem(cartItem: CartItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertCartItem(cartItem)
        }
    }

    fun updateCartItem(cartItem: CartItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateCartItem(cartItem)
        }
    }

    fun deleteCartItem(cartItem: CartItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteCartItem(cartItem)
        }
    }

    fun getCartItemByUser(userId: Long): LiveData<List<CartItem>?> {
        return repository.getCartItemByUser(userId)
    }

    fun getCartItemByUserAndProduct(userId: Long, productId: Long): LiveData<CartItem?> {
        return repository.getCartItemByUserAndProduct(userId, productId)
    }
}
