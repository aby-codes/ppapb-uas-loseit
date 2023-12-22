package com.example.loseit.home

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.loseit.database.FoodItem
import com.example.loseit.database.FoodRoomDatabase
import com.example.loseit.databinding.FragmentHistoryBinding
import com.example.loseit.food.CustomFood
import com.example.loseit.food.FoodAdapter
import com.example.loseit.food.ListFood
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private val db by lazy { FoodRoomDatabase(requireContext()) }
    private lateinit var foodAdapter: FoodAdapter
    private var userId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        userId = sharedPreferences.getString("userId", "")
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        setupListener()
        setupRecyclerView()

        val intent = Intent(activity, ListFood::class.java)
        binding.btnAddFood.setOnClickListener {
            startActivity(intent)
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        loadNote()
    }

    private fun loadNote() {
        CoroutineScope(Dispatchers.IO).launch {
            val foods = db.foodItemDao().getFoodByUser(userId.toString())
            Log.d("Main", "DBres: $foods")
            withContext(Dispatchers.Main) {
                foodAdapter.setData(foods)
            }
        }
    }

    private fun setupListener() {
        with(binding) {
        }
    }

    private fun setupRecyclerView() {
        foodAdapter = FoodAdapter(arrayListOf(), object : FoodAdapter.OnAdapterListener {

            override fun onDelete(food: FoodItem) {
                deleteDialog(food)
            }
        })
        with(binding) {
            rvFood.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = foodAdapter
            }
        }
    }

    private fun deleteDialog(food: FoodItem) {
        val alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.apply {
            setTitle("Konfirmasi")
            setMessage("Apakah anda yakin?")
            setNegativeButton("Batal") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            setPositiveButton("Hapus") { dialogInterface, _ ->
                dialogInterface.dismiss()
                CoroutineScope(Dispatchers.IO).launch {
                    db.foodItemDao().deleteFood(food)
                    loadNote()
                }
            }
        }
        alertDialog.show()
    }
}