package com.example.rachi_intern.screen.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.rachi_intern.screen.OrderActivity
import com.example.rachi_intern.R
import com.example.rachi_intern.roomDb.entities.User
import com.example.rachi_intern.screen.AuthenticationScreen
import com.google.gson.Gson

class ProfileFragment : Fragment() {

    private lateinit var userName:TextView
    private lateinit var userEmail:TextView
    private lateinit var order:RelativeLayout
    private lateinit var profileLogout:RelativeLayout

    private lateinit var sharedPref: SharedPreferences

    private var currentUser:User?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_profile, container, false)

        sharedPref = requireActivity().getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
        currentUser=fetchUserResponseFromSharedPreferences()

        userName=rootView.findViewById(R.id.profile_username)
        userEmail=rootView.findViewById(R.id.profile_email)
        order=rootView.findViewById(R.id.profile_order)
        profileLogout=rootView.findViewById(R.id.profile_logout)

        if(currentUser!=null){
            userName.text=currentUser!!.fullName
            userEmail.text=currentUser!!.email
        }

        order.setOnClickListener {
            val intent=Intent(requireContext(), OrderActivity::class.java)
            startActivity(intent)
        }
        profileLogout.setOnClickListener {
            val editor = sharedPref.edit()
            editor.clear()
            editor.apply()
            moveToLoginFragment()
        }
        return rootView

    }

    private fun moveToLoginFragment() {
        val intent = Intent(requireContext(), AuthenticationScreen::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
    private fun fetchUserResponseFromSharedPreferences(): User? {
        val userJson = sharedPref.getString("user_details", null)
        val gson = Gson()
        if (userJson == null) return null;
        return gson.fromJson(userJson, User::class.java)
    }

}