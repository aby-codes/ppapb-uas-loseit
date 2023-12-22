package com.example.loseit.home

import NotificationReceiver
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.example.loseit.R
import com.example.loseit.database.FoodRoomDatabase
import com.example.loseit.databinding.FragmentHomeBinding
import com.example.loseit.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val db by lazy { FoodRoomDatabase(requireContext()) }
    private var username: String? = null
    private var goalCalories: Int? = null
    private var remaining: Int? = null
    private var food: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Fetch and display the user's data
        val userId = auth.currentUser?.uid
        if (userId != null) {
            fetchDataFromFirebase(userId)
            fetchTotalCaloriesFromRoom(userId)
        }

        return binding.root
    }

    private fun fetchDataFromFirebase(userId: String) {
        // Retrieve the user document from Firestore
        val userRef = firestore.collection("users").document(userId)
        userRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Save data in global variables
                    username = document.getString("name")
                    goalCalories = document.getString("calories")?.toInt()

                    remaining = maxOf(0, goalCalories?.minus(food!!) ?: 0)

                    // Set values in the binding object
                    binding.tvUsername.text = "Haloo $username"
                    binding.tvGoal.text = goalCalories.toString() + " kcal"
                    binding.tvRemaining.text = remaining.toString() + " kcal"

                    // Calculate the progress percentage
                    val progressPercentage = ((remaining!!).toDouble() / goalCalories!!) * 100
                    Log.d("tess1", "Progress percentage: $progressPercentage")
                    binding.progressCircularIndicator.setProgress(progressPercentage.toInt(), true)

                }
            }
            .addOnFailureListener { e ->
                Log.d("tess1", "Error fetching data: $e")
            }
    }

    private fun fetchTotalCaloriesFromRoom(userId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val totalCalories = db.foodItemDao().getTotalCaloriesForUser(userId)
            food = totalCalories
            updateUI()
        }
    }

    private fun updateUI() {
        binding.tvFood.text = food.toString() + " kcal"
    }

}