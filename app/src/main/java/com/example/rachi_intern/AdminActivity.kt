package com.example.rachi_intern

import AdminFragment
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

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