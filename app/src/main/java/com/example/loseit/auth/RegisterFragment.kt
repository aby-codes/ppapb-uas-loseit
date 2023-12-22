package com.example.loseit.auth

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import com.example.loseit.R
import com.example.loseit.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private var selectedDate: String? = null
    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollectionRef = firestore.collection("users")
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(inflater, container, false)

        val goals = resources.getStringArray(R.array.goals)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, goals)
        binding.spGoals.setAdapter(arrayAdapter)

        binding.spDate.setOnClickListener {
            showDatePicker()
        }

        binding.btnRegister.setOnClickListener {
            if (validateInputs()) {
                val email = binding.etEmail.text.toString().trim()
                val password = binding.etPassword.text.toString().trim()

                // Create user in Firebase Authentication
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Email and password are saved in Firebase Authentication
                            val user = auth.currentUser
                            val userId = user?.uid ?: ""

                            // Save additional user details in Firestore
                            val name = binding.etName.text.toString().trim()
                            val weightNow = binding.etWeightNow.text.toString().trim()
                            val weightWant = binding.etWeightWant.text.toString().trim()
                            val height = binding.etHeight.text.toString().trim()
                            val goal = binding.spGoals.text.toString().trim()
                            val date = binding.spDate.text.toString().trim()
                            val calories = binding.etCalories.text.toString().trim()
                            val type = "user"

                            val users = Users(
                                name,
                                email,
                                password,
                                weightNow,
                                weightWant,
                                height,
                                goal,
                                date,
                                calories,
                                type
                            )
                            usersCollectionRef.document(userId)
                                .set(users)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        requireContext(),
                                        "Pengguna berhasil ditambahkan",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    val intent = Intent(requireContext(), MainActivity::class.java)
                                    startActivity(intent)
                                    requireActivity().finish()
                                }
                                .addOnFailureListener { e ->
                                    Log.d("MainActivity", "Error adding document", e)
                                }
                        } else {
                            Log.w("RegisterFragment", "createUserWithEmail:failure", task.exception)
                            binding.etEmail.error = "Email telah digunakan, gunakan email lain"
                            Toast.makeText(
                                requireContext(),
                                "Email telah digunakan, gunakan email lain",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }


        return binding.root
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
            selectedDate = "$dayOfMonth/${month + 1}/$year"
            binding.spDate.setText(selectedDate)
        }, year, month, day)
        datePickerDialog.show()
    }

    private fun validateInputs(): Boolean {
        val validationFields = listOf(
            binding.etName to "Name",
            binding.etEmail to "Email",
            binding.etPassword to "Password",
            binding.etWeightNow to "Weight now",
            binding.etWeightWant to "Target weight",
            binding.spGoals to "Goal",
            binding.spDate to "Date",
            binding.etCalories to "Calories"
        )

        for ((view, fieldName) in validationFields) {
            val text = view.text.toString().trim()

            when (view) {
                is EditText -> {
                    when (view) {
                        binding.etEmail -> {
                            if (!isValidEmail(text)) {
                                view.error = "Invalid $fieldName"
                                return false
                            }
                        }

                        binding.etPassword -> {
                            if (text.length < 8) {
                                view.error = "$fieldName harus lebih dari 8 karakter"
                                return false
                            }
                        }
                    }
                }
            }

            if (TextUtils.isEmpty(text)) {
                view.error = "$fieldName is diperlukan"
                return false
            }
        }
        return true
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("^\\S+@\\S+\\.\\S+\$")
        return emailRegex.matches(email)
    }
}