package com.example.loseit.food

import NotificationReceiver
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.findNavController
import com.example.loseit.R
import com.example.loseit.auth.SharedPreferencesManager
import com.example.loseit.database.FoodItem
import com.example.loseit.database.FoodRoomDatabase
import com.example.loseit.databinding.ActivityCustomFoodBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CustomFood : AppCompatActivity() {
    private lateinit var binding: ActivityCustomFoodBinding
    private val channelId = "LoseIt_Notification"
    private val db by lazy { FoodRoomDatabase(this) }
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPreferences: SharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
         userId = sharedPreferences.getString("userId", "")
        super.onCreate(savedInstanceState)
        binding = ActivityCustomFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupListener()

        // Retrieve data from the intent
        val foodName = intent.getStringExtra("foodName")
        val foodCalories = intent.getIntExtra("foodCalories", 0)

        with(binding){
            etName.setText(foodName)
            etCalories.setText(foodCalories.toString())
            etServing.setText("100")
            etTime.setOnClickListener {
                showTimePickerDialog(it)
            }
        }

    }

    fun showTimePickerDialog(v: View) {
        val etTime = binding.etTime
        val cal = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)

            etTime.setText(SimpleDateFormat("HH:mm", Locale.getDefault()).format(cal.time))
        }
        TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
    }

    private fun setupListener() {
        with(binding) {
            btnAddFood.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    db.foodItemDao().insert(
                        FoodItem(id = 0, etTime.text.toString(), etName.text.toString(), etCalories.text.toString().toInt(), etServing.text.toString().toInt(), userId.toString())
                    )
                    withContext(Dispatchers.Main) {
                        showNotification()
                        Toast.makeText(this@CustomFood, "Makanan Berhasil Ditambahkan", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
        }
    }

    private fun showNotification() {
        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_MUTABLE
        } else {
            0
        }

        val intent = Intent(this, NotificationReceiver::class.java)
            .putExtra("Message", "Baca Selengkapnya")
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, flag)

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setContentTitle("LoseIt")
            .setContentText("Makanan Berhasil Ditambahkan!") // Isi pesan bebas
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .addAction(0, "Baca Selengkapnya", pendingIntent)
            .setAutoCancel(true)

        val notifManager =  getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notifChannel = NotificationChannel(
                channelId,
                "LoseIt_Notification",
                NotificationManager.IMPORTANCE_HIGH
            )
            with(notifManager) {
                createNotificationChannel(notifChannel)
                notify(0, builder.build())
            }
        } else {
            notifManager.notify(0, builder.build())
        }
    }
}
