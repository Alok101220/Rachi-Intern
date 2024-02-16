package com.example.rachi_intern.roomDb.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey(autoGenerate = true)
    val userId: Long,
    val fullName: String,
    val email: String,
    val password: String,
    val isAdmin:Boolean=false

)