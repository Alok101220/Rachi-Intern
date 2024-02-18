package com.example.rachi_intern.screen

import AdminFragment
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.rachi_intern.R

class AdminActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.admin_frameLayout, AdminFragment())
        fragmentTransaction.commit()
    }
}