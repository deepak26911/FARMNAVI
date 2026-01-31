package com.example.farmnavi

import MarketFragment
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment

class HomeFragment : Fragment(R.layout.fragment_home) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        view.findViewById<LinearLayout>(R.id.item_crop)?.setOnClickListener {
            (activity as? MainActivity)?.showFragment(AdvisoryFragment())
        }
        view.findViewById<LinearLayout>(R.id.item_market)?.setOnClickListener {
            (activity as? MainActivity)?.showFragment(MarketFragment())
        }

        view.findViewById<LinearLayout>(R.id.item_pest)?.setOnClickListener {
            (activity as? MainActivity)?.showFragment(PestDiseaseAnalysisFragment())
        }

        view.findViewById<LinearLayout>(R.id.item_weather)?.setOnClickListener {
            (activity as? MainActivity)?.showFragment(WeatherFragment())
        }

    }
}
