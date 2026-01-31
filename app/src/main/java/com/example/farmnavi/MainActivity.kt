package com.example.farmnavi

import MarketFragment
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        showFragment(HomeFragment()) // Start screen


        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    showFragment(HomeFragment())
                    true
                }
                R.id.nav_advisory -> {
                    showFragment(AdvisoryFragment())
                    true
                }
                R.id.nav_market -> {
                    showFragment(MarketFragment())
                    true
                }
                R.id.nav_profile -> {
                    showFragment(UserProfileFragment())
                    true
                }
                else -> false
            }
        }
    }
    fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}
