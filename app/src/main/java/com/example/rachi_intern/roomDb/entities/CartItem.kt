package com.example.rachi_intern.roomDb.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CartItem(
    @PrimaryKey(autoGenerate = true)
    val cartId: Long,
    val userId: Long,
    val productId: Long,
    val quantity: Long
)