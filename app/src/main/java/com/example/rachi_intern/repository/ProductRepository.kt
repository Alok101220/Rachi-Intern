package com.example.rachi_intern.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.rachi_intern.roomDb.AppDatabase
import com.example.rachi_intern.roomDb.entities.Product

class ProductRepository(context: Context) {

    private val productDao = AppDatabase.getDatabase(context).productDao()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String> get() = _errorLiveData

    suspend fun insertProduct(product: Product) {
        withLoading {
            productDao.insertProduct(product)
        }
    }

    suspend fun updateProduct(product: Product) {
        withLoading {
            productDao.updateProduct(product)
        }
    }

    suspend fun getAllProducts(): List<Product> {
        return withLoading {
            productDao.getAllProducts()
        }
    }

    suspend fun deleteProduct(product: Product) {
        withLoading {
            productDao.deleteProduct(product)
        }
    }

    suspend fun getProductsByName(productName: String): List<Product> {
        return withLoading {
            productDao.getProductsByName(productName)
        }
    }

    suspend fun getProductsByCategory(productCategory: String): List<Product> {
        return withLoading {
            productDao.getProductsByCategory(productCategory)
        }
    }
    suspend fun getProductById(productId:Long):Product{
        return withLoading {
            productDao.getProductsById(productId)
        }
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
