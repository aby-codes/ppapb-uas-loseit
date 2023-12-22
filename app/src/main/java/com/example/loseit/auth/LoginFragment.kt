package com.example.loseit.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.loseit.databinding.FragmentLoginBinding
import com.example.loseit.home.HomeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.loseit.auth.SharedPreferencesManager
import com.example.loseit.home.AdminDashboard

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Firebase authentication to sign in the user
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(requireActivity()) { task ->
                        if (task.isSuccessful) {
                            Log.d("tess123", "signInWithEmail:success")
                            checkUserTypeAndNavigate()
                        } else {
                            binding.etEmail.error = "Email tidak valid atau tidak terdaftar"
                            binding.etPassword.error = "Password salah atau email tidak terdaftar"
                        }
                    }
            } else {
                // Handle empty email or password
                if (email.isEmpty()) {
                    binding.etEmail.error = "Email is required"
                }
                if (password.isEmpty()) {
                    binding.etPassword.error = "Password is required"
                }
            }
        }

        binding.btnToRegister.setOnClickListener {
            (requireActivity() as MainActivity).viewPager2.currentItem = 1
        }

        return binding.root
    }

    private fun checkUserTypeAndNavigate() {
        // Check the user type in Firestore
        val userRef = firestore.collection("users").document(auth.currentUser?.uid ?: "")
        userRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val userType = document.getString("type")
                    if (userType == "user" || userType == "admin") {
                        // Save user type in SharedPreferencesManager
                        saveUserType(userType)

                        // Navigate based on user type
                        val userId = auth.currentUser?.uid ?: ""
                        saveLoginStatus(true, userId)
                        if (userType == "user") {
                            navigateToHomeActivity()
                        } else if (userType == "admin") {
                            navigateToAdminDashboard()
                        }
                    } else {
                        // Handle other user types if needed
                    }
                }
            }
            .addOnFailureListener { e ->
                // Handle failures or errors if needed
            }
    }

    private fun saveUserType(userType: String) {
        SharedPreferencesManager.getInstance(requireContext()).setUsertype(userType)
    }

    private fun saveLoginStatus(isLoggedIn: Boolean, userId: String) {
        SharedPreferencesManager.getInstance(requireContext()).setLoggedIn(isLoggedIn)
        SharedPreferencesManager.getInstance(requireContext()).setUserId(userId)
    }

    private fun navigateToHomeActivity() {
        val intent = Intent(requireContext(), HomeActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun navigateToAdminDashboard() {
        val intent = Intent(requireContext(), AdminDashboard::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

}