package com.example.loseit.food

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.loseit.databinding.ActivityAdminFoodBinding
import com.google.firebase.firestore.FirebaseFirestore

class AdminFood : AppCompatActivity() {
    private lateinit var binding: ActivityAdminFoodBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val foodCollectionRef = firestore.collection("foods")

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAdminFoodBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnAddFood.setOnClickListener {
            // Retrieve values from EditText fields
            val name = binding.etName.text.toString().trim()
            val calories = binding.etCalories.text.toString().trim()

            // Check if name and calories are not empty
            if (name.isNotEmpty() && calories.isNotEmpty()) {
                // Create a map to store the food information
                val foodData = hashMapOf(
                    "name" to name,
                    "calories" to calories.toInt() // Convert calories to Int
                )

                // Add the food information to the Firestore collection
                foodCollectionRef.add(foodData)
                    .addOnSuccessListener {
                        Toast.makeText(
                            this,
                            "Makanan Berhasil Ditambahkan",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Clear the EditText fields after adding the food
                        binding.etName.text = null
                        binding.etCalories.text = null
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            this,
                            "Error adding food to Firestore: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
