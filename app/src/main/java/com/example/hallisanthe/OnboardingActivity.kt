package com.example.hallisanthe

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.hallisanthe.adapter.OnboardingAdapter
import com.example.hallisanthe.adapter.OnboardingSlide
import com.example.hallisanthe.utils.LanguageHelper

class OnboardingActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var btnNext: Button
    private lateinit var btnSkip: TextView
    private lateinit var dotsLayout: LinearLayout

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LanguageHelper.applyLanguage(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        if (prefs.getBoolean("onboarding_done", false)) {
            startActivity(Intent(this, SplashActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_onboarding)

        viewPager  = findViewById(R.id.viewPager)
        btnNext    = findViewById(R.id.btnNext)
        btnSkip    = findViewById(R.id.btnSkip)
        dotsLayout = findViewById(R.id.dotsLayout)

        val slides = listOf(
            OnboardingSlide(
                "🏪",
                "Welcome to Halli-Santhe!",
                "Your local digital marketplace connecting village artisans directly to buyers"
            ),
            OnboardingSlide(
                "🧑‍🎨",
                "Support Local Artisans",
                "Browse unique handmade products from talented artisans across Karnataka"
            ),
            OnboardingSlide(
                "🛍️",
                "Buy & Sell Easily",
                "Upload your products in seconds or find amazing handmade items near you"
            ),
            OnboardingSlide(
                "💬",
                "Connect Directly",
                "Message or call artisans directly on WhatsApp — no middlemen!"
            )
        )

        viewPager.adapter = OnboardingAdapter(slides)
        setupDots(slides.size, 0)

        viewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    setupDots(slides.size, position)
                    btnNext.text = if (position == slides.size - 1)
                        "🚀 Get Started!" else "Next →"
                }
            }
        )

        btnNext.setOnClickListener {
            if (viewPager.currentItem < slides.size - 1) {
                viewPager.currentItem++
            } else {
                finishOnboarding(prefs)
            }
        }

        btnSkip.setOnClickListener { finishOnboarding(prefs) }
    }

    private fun setupDots(count: Int, current: Int) {
        dotsLayout.removeAllViews()
        for (i in 0 until count) {
            val dot = View(this)
            val size = if (i == current) 24 else 16
            val params = LinearLayout.LayoutParams(size, size)
            params.setMargins(8, 0, 8, 0)
            dot.layoutParams = params
            dot.background = if (i == current)
                getDrawable(R.drawable.badge_background)
            else
                getDrawable(R.drawable.dot_inactive)
            dotsLayout.addView(dot)
        }
    }

    private fun finishOnboarding(prefs: SharedPreferences) {
        prefs.edit().putBoolean("onboarding_done", true).apply()
        startActivity(Intent(this, SplashActivity::class.java))
        finish()
    }
}