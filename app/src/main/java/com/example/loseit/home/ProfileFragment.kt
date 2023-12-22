package com.example.loseit.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.example.loseit.R
import com.example.loseit.auth.MainActivity
import com.example.loseit.auth.SharedPreferencesManager
import com.example.loseit.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        // Fetch and display the user's data
        val userId = auth.currentUser?.uid
        if (userId != null) {
            fetchDataFromFirebase(userId)
        }

        val logoutButton: AppCompatButton = binding.root.findViewById(R.id.button)
        logoutButton.setOnClickListener {
            logout()
        }



        return binding.root
    }

    private fun fetchDataFromFirebase(userId: String) {
        // Retrieve the user document from Firestore
        val userRef = firestore.collection("users").document(userId)
        userRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    binding.tvNama.text = "Nama : ${document.getString("name")}"
                    binding.tvHeight.text = "Tinggi : ${document.getString("height")} cm"
                    binding.tvWeightNow.text = "Berat Sekarang : ${document.getString("weightNow")} kg"
                    binding.tvWeightWant.text = "Berat Target : ${document.getString("weightWant")} kg"
                    binding.tvGoals.text = "Tujuan : ${document.getString("goal")} "
                }
            }
            .addOnFailureListener { e ->
                // Handle failures or errors if needed
            }
    }

    private fun logout() {
        // Clear the login status in SharedPreferences
        SharedPreferencesManager.getInstance(requireContext()).setLoggedIn(false)

        // Perform logout actions here, if any

        // For example, navigate back to MainActivity
        val intent = Intent(activity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        activity?.finish()
    }
}
