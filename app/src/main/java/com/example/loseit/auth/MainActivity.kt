package com.example.loseit.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.loseit.databinding.ActivityMainBinding
import com.example.loseit.home.AdminDashboard
import com.example.loseit.home.HomeActivity
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var mediator: TabLayoutMediator
    lateinit var viewPager2: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check if the user is already logged in
        val isLoggedIn = SharedPreferencesManager.getInstance(this).isLoggedIn()

        if (isLoggedIn) {
            // User is already logged in, check user type and navigate accordingly
            val userType = SharedPreferencesManager.getInstance(this).getUserType()
            if (userType == "admin") {
                navigateToAdminDashboard()
            } else {
                navigateToHomeActivity()
            }
        } else {
            // User is not logged in, proceed with the login/register setup
            with(binding) {
                viewPager2 = viewPager
                viewPager.adapter = TabAdapter(supportFragmentManager, this@MainActivity.lifecycle)
                mediator = TabLayoutMediator(tabLayout, viewPager)
                { tab, position ->
                    when (position) {
                        0 -> tab.text = "Login"
                        1 -> tab.text = "Register"
                    }
                }
                mediator.attach()
            }
        }
    }

    private fun navigateToHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToAdminDashboard() {
        val intent = Intent(this, AdminDashboard::class.java)
        startActivity(intent)
        finish()
    }
}
