package com.example.rachi_intern.screen

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.LottieAnimationView
import com.example.rachi_intern.R
import com.example.rachi_intern.factory.CartViewModelFactory
import com.example.rachi_intern.factory.ProductViewModelFactory
import com.example.rachi_intern.roomDb.entities.CartItem
import com.example.rachi_intern.roomDb.entities.User
import com.example.rachi_intern.screen.fragment.PREF_FILE_NAME
import com.example.rachi_intern.viewmodel.CartViewModel
import com.example.rachi_intern.viewmodel.ProductViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.gson.Gson


class ViewProductActivity : AppCompatActivity() {

    private lateinit var mainContainer:NestedScrollView
    private lateinit var loadingAnimationView: LottieAnimationView
    private lateinit var backButton:ImageView

    private lateinit var productImage:ShapeableImageView
    private lateinit var productName:TextView
    private lateinit var productPrice:TextView
    private lateinit var productDescription:TextView

    private lateinit var addToCartButton:MaterialButton
    private lateinit var buyButton: MaterialButton

    private lateinit var productViewModel: ProductViewModel
    private lateinit var cartViewModel: CartViewModel

    private lateinit var sharedPref: SharedPreferences


    private var isAddedInCart:Boolean=false
    private var currentUser: User?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_product)

        sharedPref = getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)

        productViewModel = ViewModelProvider(this,ProductViewModelFactory(this@ViewProductActivity))[ProductViewModel::class.java]
        cartViewModel = ViewModelProvider(this, CartViewModelFactory(this))[CartViewModel::class.java]

        mainContainer = findViewById(R.id.main_container)
        loadingAnimationView = findViewById(R.id.viewProduct_loading)
        backButton = findViewById(R.id.back_from_view_product)

        productImage=findViewById(R.id.view_product_image)
        productName=findViewById(R.id.view_product_name)
        productPrice=findViewById(R.id.view_product_price)
        productDescription=findViewById(R.id.view_product_description)

        addToCartButton=findViewById(R.id.add_to_cart_button)
        buyButton=findViewById(R.id.buy_product_button)

        val productId = intent.getLongExtra("product-id",0)

        currentUser =fetchUserResponseFromSharedPreferences()
        cartViewModel.getCartItemByUserAndProduct(currentUser!!.userId,productId).observe(this){cartItem->
            isAddedInCart = cartItem!=null
            if(isAddedInCart){
                addToCartButton.text="View cart"
            }else{
                addToCartButton.text="Add to cart"
            }
        }
        if(productId>0&&currentUser!=null){
            productViewModel.getProductById(productId).observe(this@ViewProductActivity){product->

                loadingAnimationView.visibility= View.GONE
                if(product!=null){
                    mainContainer.visibility=View.VISIBLE
                    productName.text = product.productName
                    productPrice.text=product.productPrice
                    productDescription.text=product.productDescription

                    val bitmap = BitmapFactory.decodeByteArray(product.productImage, 0, product.productImage.size)
                    productImage.setImageBitmap(bitmap)

                }


            }



        }else{
            onBackPressed()
        }

        addToCartButton.setOnClickListener {
            if(isAddedInCart){
               moveToCart()
            }else{
                isAddedInCart=true
                addToCartButton.text="View cart"
                val cartItem=CartItem(0,currentUser!!.userId,productId,0)
                cartViewModel.insertCartItem(cartItem)
            }
        }

        buyButton.setOnClickListener{
            if(isAddedInCart){
                moveToCart()
            }else{
                isAddedInCart=true
                addToCartButton.text="View cart"
                val cartItem=CartItem(0,currentUser!!.userId,productId,0)
                cartViewModel.insertCartItem(cartItem)

                moveToCart()

            }
        }



        backButton.setOnClickListener {
            onBackPressed()
        }


    }
    private fun fetchUserResponseFromSharedPreferences(): User? {
        val userJson = sharedPref.getString("user_details", null)
        val gson = Gson()
        if (userJson == null) return null;
        return gson.fromJson(userJson, User::class.java)
    }

    private fun moveToCart(){
        val intent=Intent(this, MainActivity::class.java)
        intent.putExtra("fragment","cart")
        startActivity(intent)
    }
}