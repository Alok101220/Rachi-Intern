package com.example.rachi_intern.dao

import com.example.rachi_intern.roomDb.entities.Address
import com.example.rachi_intern.roomDb.entities.Product

data class OrderProduct (
    val product:Product,
    val address: Address,
    val userId:Long,
    val quantity:Int
)