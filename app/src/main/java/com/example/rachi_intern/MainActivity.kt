package com.example.rachi_intern

import HomeFragment
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.example.rachi_intern.roomDb.entities.User
import com.example.rachi_intern.screen.fragment.CartFragment
import com.example.rachi_intern.screen.fragment.PREF_FILE_NAME
import com.example.rachi_intern.screen.fragment.ProfileFragment
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    private lateinit var home_icon:ImageView
    private lateinit var account_icon:ImageView
    private lateinit var cart_icon:ImageView
    private lateinit var homeButton:LinearLayout
    private lateinit var accountButton:LinearLayout
    private lateinit var cartButton:LinearLayout


    private lateinit var sharedPref: SharedPreferences

    private var currentFragment: Fragment? = null

    private var currentUser: User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPref = getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)


        val fragment = intent.getStringExtra("fragment")
        home_icon=findViewById(R.id.home_icon)
        account_icon=findViewById(R.id.account_icon)
        cart_icon=findViewById(R.id.cart_icon)

        homeButton=findViewById(R.id.home)
        accountButton=findViewById(R.id.account)
        cartButton=findViewById(R.id.cart)


        currentUser = fetchUserResponseFromSharedPreferences()

        if(fragment=="cart"){
            replace(CartFragment())
            cart_icon.setImageResource(R.drawable.icon_cart_active)
        }else{
            replace(HomeFragment())
            home_icon.setImageResource(R.drawable.icon_home_active)
        }


        homeButton.setOnClickListener {
            if (currentFragment !is HomeFragment) {
                resetButtons()

                    home_icon.setImageResource(R.drawable.icon_home_active)
                    replace(HomeFragment())

            }
        }
        accountButton.setOnClickListener {
            if (currentFragment !is ProfileFragment) {
                resetButtons()

                account_icon.setImageResource(R.drawable.icon_account_active)
                replace(ProfileFragment())

            }
        }
        cartButton.setOnClickListener {
            if (currentFragment !is CartFragment) {
                resetButtons()

                cart_icon.setImageResource(R.drawable.icon_cart_active)
                replace(CartFragment())

            }
        }


    }

    private fun replace(fragment: Fragment) {
        currentFragment=fragment
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.FL, currentFragment!!)
        fragmentTransaction.commit()
    }

    private fun fetchUserResponseFromSharedPreferences(): User? {
        val userJson = sharedPref.getString("user_details", null)
        val gson = Gson()
        if (userJson == null) return null;
        return gson.fromJson(userJson, User::class.java)
    }
    override fun onResume() {
        super.onResume()

        // Reset buttons and backgrounds when MainActivity resumes
        resetButtons()
        when (currentFragment) {

            is HomeFragment -> {
                home_icon.setImageResource(R.drawable.icon_home_active)
            }

            is ProfileFragment -> {
                account_icon.setImageResource(R.drawable.icon_account_active)
            }

            is CartFragment -> {
                cart_icon.setImageResource(R.drawable.icon_cart_active)
            }
        }
    }
    private fun resetButtons() {

        home_icon.setImageResource(R.drawable.icon_home)

        account_icon.setImageResource(R.drawable.icon_account)

        cart_icon.setImageResource(R.drawable.icon_cart)

    }
}