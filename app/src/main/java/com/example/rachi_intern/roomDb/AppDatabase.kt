package com.example.rachi_intern.roomDb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.rachi_intern.roomDb.dao.AddressDao
import com.example.rachi_intern.roomDb.dao.CartDao
import com.example.rachi_intern.roomDb.dao.OrderDao
import com.example.rachi_intern.roomDb.dao.ProductDao
import com.example.rachi_intern.roomDb.dao.UserDao
import com.example.rachi_intern.roomDb.entities.Address
import com.example.rachi_intern.roomDb.entities.CartItem
import com.example.rachi_intern.roomDb.entities.Converters
import com.example.rachi_intern.roomDb.entities.Product
import com.example.rachi_intern.roomDb.entities.User
import com.example.rachi_intern.roomDb.entities.Order

@Database(
    entities = [User::class, Product::class,  CartItem::class, Order::class, Address::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun addressDao(): AddressDao
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao
    abstract fun productDao(): ProductDao
    abstract fun userDao(): UserDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "hireQuest_database"
                )
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }

}