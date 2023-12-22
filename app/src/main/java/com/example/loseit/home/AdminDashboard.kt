package com.example.loseit.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.loseit.R
import com.example.loseit.auth.MainActivity
import com.example.loseit.auth.SharedPreferencesManager
import com.example.loseit.database.FirebaseFoodItem
import com.example.loseit.databinding.ActivityAdminDashboardBinding
import com.example.loseit.databinding.ActivityListFoodBinding
import com.example.loseit.food.AdminFood
import com.example.loseit.food.CustomFood
import com.example.loseit.food.FirebaseFoodAdapter
import com.google.firebase.firestore.FirebaseFirestore

class AdminDashboard : AppCompatActivity() {
    private lateinit var binding: ActivityAdminDashboardBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val foodRef = firestore.collection("foods")
    private lateinit var firebaseFoodAdapter: FirebaseFoodAdapter
    private val foodListLiveData: MutableLiveData<List<FirebaseFoodItem>> by lazy {
        MutableLiveData<List<FirebaseFoodItem>>()
    }
        override fun onCreate(savedInstanceState: Bundle?) {
            binding = ActivityAdminDashboardBinding.inflate(layoutInflater)
            super.onCreate(savedInstanceState)
            setContentView(binding.root)
            setupRecyclerView()
            observeFoods()

            binding.btnAddFood.setOnClickListener {
                val intent = Intent(this, AdminFood::class.java)
                startActivity(intent)
            }

            binding.btnLogOut.setOnClickListener {
                logout()
            }
        }

    override fun onStart() {
        super.onStart()
        loadFood()
    }

    private fun observeFoods() {
        foodListLiveData.observe(this) { firebaseFoodItem ->
            firebaseFoodAdapter.setData(firebaseFoodItem.toMutableList())
        }
    }

    private fun loadFood(){
        foodRef.addSnapshotListener { snapshots, error ->
            if (error != null) {
                return@addSnapshotListener
            }
            val item = snapshots?.toObjects(FirebaseFoodItem::class.java)
            if (item != null) {
                foodListLiveData.postValue(item)
            }
        }
    }

    private fun setupRecyclerView() {
        firebaseFoodAdapter = FirebaseFoodAdapter(arrayListOf(), object :
            FirebaseFoodAdapter.OnAdapterListener {

            override fun onClick(food: FirebaseFoodItem) {
                // Handle the click event, and pass data to CustomFood activity
                val intent = Intent(this@AdminDashboard, CustomFood::class.java)
                intent.putExtra("foodName", food.name)
                intent.putExtra("foodCalories", food.calories)
                // Add other data as needed
                startActivity(intent)
            }
        })
        with(binding) {
            rvFood.apply {
                layoutManager = LinearLayoutManager(this@AdminDashboard)
                adapter = firebaseFoodAdapter
            }
        }
    }

    private fun logout() {
        // Clear the login status in SharedPreferences
        SharedPreferencesManager.getInstance(this).setLoggedIn(false)

        // Perform logout actions here, if any

        // For example, navigate back to MainActivity
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}