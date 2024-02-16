package com.example.rachi_intern.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.rachi_intern.repository.ProductRepository
import com.example.rachi_intern.roomDb.entities.Product
import kotlinx.coroutines.launch

class ProductViewModel(context: Context) : ViewModel() {

    private val productRepository = ProductRepository(context)
    val isLoading: LiveData<Boolean> = productRepository.isLoading
    val errorLiveData: LiveData<String> = productRepository.errorLiveData

    fun insertProduct(product: Product) {
        viewModelScope.launch {
            productRepository.insertProduct(product)
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            productRepository.updateProduct(product)
        }
    }

    fun getAllProducts(): LiveData<List<Product>> {
        return liveData {
            emit(productRepository.getAllProducts())
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            productRepository.deleteProduct(product)
        }
    }

    fun getProductsByName(productName: String): LiveData<List<Product>> {
        return liveData {
            emit(productRepository.getProductsByName(productName))
        }
    }

    fun getProductsByCategory(productCategory: String): LiveData<List<Product>> {
        return liveData {
            emit(productRepository.getProductsByCategory(productCategory))
        }
    }

    fun getProductById(productId:Long):LiveData<Product?>{
        return liveData {
            emit(productRepository.getProductById(productId))
        }
    }
}
