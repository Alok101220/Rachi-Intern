package com.example.rachi_intern.screen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.rachi_intern.R
import com.example.rachi_intern.screen.fragment.LoginFragment
import com.example.rachi_intern.screen.fragment.RegistrationFragment

class AuthenticationScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication_screen)

        replace(LoginFragment())


    }

    private fun replace(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.authentication_fragment, fragment)
        fragmentTransaction.commit()
    }


}