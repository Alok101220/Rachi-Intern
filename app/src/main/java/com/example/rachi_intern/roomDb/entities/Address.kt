package com.example.rachi_intern.roomDb.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Address(
    @PrimaryKey(autoGenerate = true)
    val addressId: Long,
    val locality: String,
    val city: String,
    val state: String,
    val pinCode: String,
    val phoneNumber: String,
    val userId: Long
    ) {
    override fun toString(): String {
        return "Address(addressId=$addressId, locality='$locality', city='$city', state='$state', pinCode='$pinCode', phoneNumber='$phoneNumber', userId=$userId)"
    }
}