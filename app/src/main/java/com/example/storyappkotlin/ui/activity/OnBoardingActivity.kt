package com.example.storyappkotlin.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.storyappkotlin.R
import com.example.storyappkotlin.databinding.ActivityOnBoardingBinding

class OnBoardingActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityOnBoardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener(this)
        binding.btnRegister.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btn_login -> {
                val intent = Intent(this@OnBoardingActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            R.id.btn_register -> {
                val intent = Intent(this@OnBoardingActivity, RegisterActivity::class.java)
                startActivity(intent)
            }
        }
    }
}