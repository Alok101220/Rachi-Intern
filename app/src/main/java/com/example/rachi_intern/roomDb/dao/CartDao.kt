package com.example.rachi_intern.roomDb.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.rachi_intern.roomDb.entities.CartItem

@Dao
interface CartDao {

    @Insert
    suspend fun insertCartItem(cartItem: CartItem)

    @Update
    suspend fun updateCartItem(cartItem: CartItem)

    @Delete
    suspend fun deleteCartItem(cartItem: CartItem)

    @Query("SELECT * FROM CartItem WHERE userId =:userId")
    fun getCartItemByUser(userId: Long): LiveData<List<CartItem>?>

    @Query("SELECT * FROM CartItem WHERE userId =:userId and productId=:productId")
    fun getCartItemByUserAndProduct(userId: Long, productId:Long): LiveData<CartItem?>


}