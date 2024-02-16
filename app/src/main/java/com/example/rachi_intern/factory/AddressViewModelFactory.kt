package com.example.rachi_intern.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.rachi_intern.viewmodel.AddressViewModel

class AddressViewModelFactory (private val context: Context): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(AddressViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddressViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}