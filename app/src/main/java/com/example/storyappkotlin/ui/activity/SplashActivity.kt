package com.example.storyappkotlin.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.storyappkotlin.databinding.ActivitySplashBinding
import com.example.storyappkotlin.ui.MainActivity
import com.example.storyappkotlin.utils.SharedPreferenceUtil

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var pref: SharedPreferenceUtil;
    private val TAG = SplashActivity::class.java.simpleName;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        enableEdgeToEdge()

        val binding = ActivitySplashBinding.inflate(layoutInflater);
        setContentView(binding.root);

        pref = SharedPreferenceUtil(this);
        checkToken();
    }

    private fun checkToken() {
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = when {
                pref.getToken().isEmpty() -> {
                    if (pref.isAlreadyHaveAccount()) {
                        Intent(this, LoginActivity::class.java)
                    } else {
                        Intent(this, OnBoardingActivity::class.java)
                    }
                }
                else -> Intent(this, MainActivity::class.java)
            }

            startActivity(intent)
            finish()
        }, 1000)
    }
}