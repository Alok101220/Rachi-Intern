package com.example.rachi_intern.screen.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.rachi_intern.screen.AdminActivity
import com.example.rachi_intern.screen.MainActivity
import com.example.rachi_intern.R
import com.example.rachi_intern.roomDb.entities.User
import com.example.rachi_intern.factory.UserViewModelFactory
import com.example.rachi_intern.viewmodel.UserViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson

class LoginFragment : Fragment() {
    private lateinit var moveToRegistration:TextView
    private lateinit var email: TextInputEditText
    private lateinit var password:TextInputEditText
    private lateinit var loginButton:MaterialButton


    private lateinit var userViewModel: UserViewModel

    private lateinit  var sharedPref: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_login, container, false)
        sharedPref = requireActivity().getSharedPreferences(PREF_FILE_NAME ,Context.MODE_PRIVATE)


        userViewModel = ViewModelProvider(
            this,
            UserViewModelFactory(requireContext())
        )[UserViewModel::class.java]

        email=rootView.findViewById(R.id.login_email)
        password=rootView.findViewById(R.id.login_password)
        moveToRegistration=rootView.findViewById(R.id.login_to_registration)
        loginButton=rootView.findViewById(R.id.login_button)
        checkIfAlreadyLoggedIn()
        moveToRegistration.setOnClickListener{
            val fragmentManager = parentFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.authentication_fragment, RegistrationFragment())
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        loginButton.setOnClickListener {
            if(email.text.toString().isEmpty()&&password.text.toString().isEmpty()){
                Toast.makeText(requireContext(),"Fill all Fields",Toast.LENGTH_SHORT).show()
            }else{
                userViewModel.getUserByEmailAndPassword(email.text.toString(),password.text.toString()).observe(viewLifecycleOwner){ user->
                    if (user != null) {
                        saveLoginCredentials(user)
                        if(user.isAdmin){
                            val intent = Intent(requireContext(), AdminActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }else{
                            val intent = Intent(requireContext(), MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                    }else{
                        Toast.makeText(requireContext(),"User not registered!",Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }

        return rootView
    }
    private fun checkIfAlreadyLoggedIn() {
        val savedEmail = sharedPref.getString("email", "")
        val savedPassword = sharedPref.getString("password", "")
        val user = fetchUserResponseFromSharedPreferences()

        if (!savedEmail.isNullOrEmpty() && !savedPassword.isNullOrEmpty()) {
            // Perform automatic login
            if (checkLoginCredentials(savedEmail, savedPassword)) {
                // Login successful
                if (user != null) {
                    if(user.isAdmin){
                        val intent = Intent(requireContext(), AdminActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }else{
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                }
            }
        }
    }

    private fun checkLoginCredentials(savedEmail: String, savedPassword: String): Boolean {
        val saveEmail = sharedPref.getString("email", "") ?: ""
        val savePassword = sharedPref.getString("password", "") ?: ""

        return savedEmail == saveEmail && savedPassword == savePassword
    }


    private fun saveLoginCredentials(user: User) {
        val gson = Gson()
        val userJson = gson.toJson(user)
        val editor = sharedPref.edit()
        editor.putString("user_details", userJson)
        editor.putString("email", user.email)
        editor.putString("password", user.password)
        editor.apply()
    }
    private fun fetchUserResponseFromSharedPreferences(): User? {
        val userJson = sharedPref.getString("user_details", null)
        val gson = Gson()
        if(userJson==null)return null;
        return gson.fromJson(userJson, User::class.java)
    }
}