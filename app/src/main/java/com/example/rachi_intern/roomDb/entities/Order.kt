package com.example.rachi_intern.roomDb.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.Date

@Entity
data class Order(
    @PrimaryKey(autoGenerate = true)
    val orderId: Long,
    val userId: Long,
    val productId: Long,
    val addressId: Long,
    val quantity: Long,
    val paymentMode: String,
    val fullAddress:String
)