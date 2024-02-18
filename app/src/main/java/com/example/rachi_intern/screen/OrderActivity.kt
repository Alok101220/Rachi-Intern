package com.example.rachi_intern.screen

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rachi_intern.R
import com.example.rachi_intern.adapter.OrderHistoryAdapter
import com.example.rachi_intern.factory.OrderViewModelFactory
import com.example.rachi_intern.factory.ProductViewModelFactory
import com.example.rachi_intern.roomDb.entities.Product
import com.example.rachi_intern.roomDb.entities.User
import com.example.rachi_intern.screen.fragment.PREF_FILE_NAME
import com.example.rachi_intern.viewmodel.OrderViewModel
import com.example.rachi_intern.viewmodel.ProductViewModel
import com.google.gson.Gson

class OrderActivity : AppCompatActivity() {

    private lateinit var orderHistoryRecyclerView: RecyclerView

    private lateinit var orderViewModel: OrderViewModel
    private lateinit var productViewModel: ProductViewModel
    private lateinit var orderHistoryAdapter: OrderHistoryAdapter

    private var productList:MutableList<Product> = mutableListOf()

    private var currentUser:User?=null

    private lateinit var sharedPref: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        sharedPref = getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)

        orderViewModel=ViewModelProvider(this,OrderViewModelFactory(this))[OrderViewModel::class.java]
        productViewModel=ViewModelProvider(this,ProductViewModelFactory(this))[ProductViewModel::class.java]

        orderHistoryRecyclerView=findViewById(R.id.order_history_recyclerView)

        orderHistoryAdapter= OrderHistoryAdapter(productList)
        orderHistoryRecyclerView.layoutManager=LinearLayoutManager(this)
        orderHistoryRecyclerView.adapter=orderHistoryAdapter


        currentUser=fetchUserResponseFromSharedPreferences()
        if(currentUser!=null){
            orderViewModel.getAllOrderByUser(currentUser!!.userId).observe(this){orderList->
                if(!orderList.isNullOrEmpty()){
                    productList.clear()
                    for (order in orderList){
                        productViewModel.getProductById(order.productId).observe(this){
                            if(it!=null){
                                productList.add(it)
                            }
                        }
                    }
                }

            }
            orderHistoryAdapter.updateData(productList)
        }else{
            onBackPressed()
        }

    }

    private fun fetchUserResponseFromSharedPreferences(): User? {
        val userJson = sharedPref.getString("user_details", null)
        val gson = Gson()
        if (userJson == null) return null;
        return gson.fromJson(userJson, User::class.java)
    }
}