package com.example.loseit.food

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.loseit.R
import com.example.loseit.database.FirebaseFoodItem
import com.example.loseit.database.FoodItem
import com.example.loseit.databinding.ActivityCustomFoodBinding
import com.example.loseit.databinding.ActivityListFoodBinding
import com.google.firebase.firestore.FirebaseFirestore

class ListFood : AppCompatActivity() {

    private lateinit var binding: ActivityListFoodBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val foodRef = firestore.collection("foods")
    private lateinit var firebaseFoodAdapter: FirebaseFoodAdapter
    private val foodListLiveData: MutableLiveData<List<FirebaseFoodItem>> by lazy {
        MutableLiveData<List<FirebaseFoodItem>>()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityListFoodBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupRecyclerView()
        observeFoods()

        binding.btnCustom.setOnClickListener {
            val intent = Intent(this@ListFood, CustomFood::class.java)
            startActivity(intent)
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
                val intent = Intent(this@ListFood, CustomFood::class.java)
                intent.putExtra("foodName", food.name)
                intent.putExtra("foodCalories", food.calories)
                // Add other data as needed
                startActivity(intent)
            }
        })
        with(binding) {
            rvFood.apply {
                layoutManager = LinearLayoutManager(this@ListFood)
                adapter = firebaseFoodAdapter
            }
        }
    }

}