package com.example.rachi_intern.screen.fragment

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.LottieAnimationView
import com.example.rachi_intern.R
import com.example.rachi_intern.roomDb.entities.User
import com.example.rachi_intern.factory.UserViewModelFactory
import com.example.rachi_intern.viewmodel.UserViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText

const val PREF_FILE_NAME = "login_preference"

class RegistrationFragment : Fragment() {

    private lateinit var moveToLogin: TextView
    private lateinit var userFullName: TextInputEditText
    private lateinit var userEmail: TextInputEditText
    private lateinit var userPassword: TextInputEditText
    private lateinit var confirmPassword: TextInputEditText
    private lateinit var userRegisterButton: MaterialButton

    private lateinit var registrationMainContainer: MaterialCardView
    private lateinit var loadingProgressBar: LottieAnimationView

    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_registration, container, false)

        userViewModel = ViewModelProvider(
            this,
            UserViewModelFactory(requireContext())
        )[UserViewModel::class.java]
        userFullName = rootView.findViewById(R.id.registration_fullName_EditText)
        userEmail = rootView.findViewById(R.id.registration_email_EditText)
        userPassword = rootView.findViewById(R.id.registration_password_EditText)
        confirmPassword = rootView.findViewById(R.id.registration_confirmPassword_EditText)
        userRegisterButton = rootView.findViewById(R.id.registration_button)
        moveToLogin = rootView.findViewById(R.id.registration_to_login)

        loadingProgressBar = rootView.findViewById(R.id.registration_loading)
        registrationMainContainer = rootView.findViewById(R.id.registration_main_container)

        userViewModel.errorLiveData.observe(requireActivity()) { errorMessage ->

            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }

        userViewModel.isLoading.observe(requireActivity()) { isLoading ->
            if (isLoading) {
                registrationMainContainer.alpha = 0.2f
                loadingProgressBar.visibility = View.VISIBLE
            } else {
                registrationMainContainer.alpha = 1.0f
                loadingProgressBar.visibility = View.GONE
            }
        }

        userViewModel.isUserInserted.observe(requireActivity()) {

            if (it) {
                val fragmentManager = parentFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.authentication_fragment, LoginFragment())
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
            }
        }

        userRegisterButton.setOnClickListener {
            if (userFullName.text.isNullOrEmpty() || userEmail.text.isNullOrEmpty() || userPassword.text.isNullOrEmpty() || confirmPassword.text.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "All field required!", Toast.LENGTH_SHORT).show()
            } else {
                if (!isValidEmail(userEmail.text.toString())) {
                    Toast.makeText(requireContext(), "Email invalid!", Toast.LENGTH_SHORT)
                        .show()
                } else if (userPassword.text.toString() != confirmPassword.text.toString()) {
                    Toast.makeText(requireContext(), "Password not matched!", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    userViewModel.getUserByEmail(userEmail.text.toString())
                        .observe(requireActivity()) { registeredUser ->
                            if (registeredUser != null) {
                                Toast.makeText(
                                    requireContext(),
                                    "User already exist, try with another email!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                if (userPassword.text.toString() == "admin") {
                                    val user = User(
                                        0,
                                        userFullName.text.toString(),
                                        userEmail.text.toString(),
                                        userPassword.text.toString(),
                                        true
                                    )
                                    userViewModel.insertUser(user)
                                } else {
                                    val user = User(
                                        0,
                                        userFullName.text.toString(),
                                        userEmail.text.toString(),
                                        userPassword.text.toString(),
                                    )
                                    userViewModel.insertUser(user)
                                }
                            }
                        }
                }
            }
        }

        moveToLogin.setOnClickListener {
            val fragmentManager = parentFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.authentication_fragment, LoginFragment())
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        return rootView
    }

    private fun isValidEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email)
            .matches()
    }
}