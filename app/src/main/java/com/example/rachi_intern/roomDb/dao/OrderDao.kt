package com.example.rachi_intern.roomDb.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.rachi_intern.roomDb.entities.Order

@Dao
interface OrderDao {

    @Insert
    suspend fun insertOrder(order: Order)

    @Query("SELECT * FROM `Order` WHERE userId =:userId ")
    fun getOrderByUser(userId: Long): LiveData<List<Order>?>

}