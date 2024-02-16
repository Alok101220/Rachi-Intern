package com.example.rachi_intern.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rachi_intern.repository.AddressRepository
import com.example.rachi_intern.roomDb.entities.Address
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddressViewModel(context: Context) : ViewModel() {

    private val addressRepository =AddressRepository(context)

    val isLoading: LiveData<Boolean> get() = addressRepository.isLoading
    val errorLiveData: LiveData<String> get() = addressRepository.errorLiveData

    fun insertAddress(address: Address) {
        viewModelScope.launch(Dispatchers.IO) {
            addressRepository.insertAddress(address)
        }
    }

    fun updateAddress(address: Address) {
        viewModelScope.launch(Dispatchers.IO) {
            addressRepository.updateAddress(address)
        }
    }

    fun deleteAddress(address: Address) {
        viewModelScope.launch(Dispatchers.IO) {
            addressRepository.deleteAddress(address)
        }
    }

    fun getAddressByUserId(userId: Long):LiveData<List<Address>?> {
        return addressRepository.getAddressByUserId(userId)
    }
}