package com.example.rachi_intern.roomDb.entities


import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Locale.Category

@Entity
data class Product(

    @PrimaryKey(autoGenerate = true)
    val productId: Long,
    val productName: String,
    val productCategory: String,
    val productPrice: String,
    val productDescription: String,
    val productImage:ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Product

        if (productId != other.productId) return false
        if (productName != other.productName) return false
        if (productCategory != other.productCategory) return false
        if (productPrice != other.productPrice) return false
        if (productDescription != other.productDescription) return false
        return productImage.contentEquals(other.productImage)
    }

    override fun hashCode(): Int {
        var result = productId.hashCode()
        result = 31 * result + productName.hashCode()
        result = 31 * result + productCategory.hashCode()
        result = 31 * result + productPrice.hashCode()
        result = 31 * result + productDescription.hashCode()
        result = 31 * result + productImage.contentHashCode()
        return result
    }
}