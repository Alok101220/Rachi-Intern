package com.example.rachi_intern.roomDb.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.rachi_intern.roomDb.entities.Product

@Dao
interface ProductDao {

    @Insert
    suspend fun insertProduct(product: Product)

    @Update
    suspend fun updateProduct(product: Product)

    @Query("SELECT * FROM Product")
    suspend fun getAllProducts(): List<Product>

    @Delete
    suspend fun deleteProduct(product: Product)

    @Query("SELECT * FROM Product WHERE productName LIKE '%' || :query || '%'")
    suspend fun getProductsByName(query: String): List<Product>

    @Query("SELECT * FROM Product WHERE productCategory LIKE '%' || :productCategory || '%'")
    suspend fun getProductsByCategory(productCategory: String): List<Product>

    @Query("SELECT * FROM Product where productId = :productId")
    suspend fun getProductsById(productId: Long): Product

}
