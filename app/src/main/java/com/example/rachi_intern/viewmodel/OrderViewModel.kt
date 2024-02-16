package com.example.rachi_intern.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rachi_intern.repository.AddressRepository
import com.example.rachi_intern.repository.OrderRepository
import com.example.rachi_intern.roomDb.entities.Address
import com.example.rachi_intern.roomDb.entities.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OrderViewModel(context: Context):ViewModel() {

    private val orderRepository = OrderRepository(context)

    val isLoading: LiveData<Boolean> get() = orderRepository.isLoading
    val errorLiveData: LiveData<String> get() = orderRepository.errorLiveData


    fun insertOrder(order: Order) {
        viewModelScope.launch(Dispatchers.IO) {
            orderRepository.insertOrder(order)
        }
    }
    fun getAllOrderByUser(userId:Long):LiveData<List<Order>?>{
        return orderRepository.getAllOrderByUser(userId)
    }
}