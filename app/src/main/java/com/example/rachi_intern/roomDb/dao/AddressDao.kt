package com.example.rachi_intern.roomDb.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.rachi_intern.roomDb.entities.Address

@Dao
interface AddressDao {

    @Insert
    suspend fun insertAddress(address: Address)

    @Update
    suspend fun updateAddress(address: Address)

    @Delete
    suspend fun deleteAddress(address: Address)

    @Query("SELECT * FROM ADDRESS WHERE userId =:userId")
    fun getAddressByUserId(userId: Long):LiveData<List<Address>?>


}