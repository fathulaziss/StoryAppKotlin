package com.example.storyappkotlin.ui.activity

import android.content.Intent
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.storyappkotlin.R
import com.example.storyappkotlin.databinding.ActivityLoginBinding
import com.example.storyappkotlin.ui.viewmodel.AuthViewModel
import com.example.storyappkotlin.ui.viewmodel.ViewModelFactory
import com.example.storyappkotlin.utils.SharedPreferenceUtil
import com.example.storyappkotlin.data.remote.Result
import com.example.storyappkotlin.ui.MainActivity

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private val TAG = LoginActivity::class.java.simpleName
    private lateinit var binding: ActivityLoginBinding
    private lateinit var authViewModel: AuthViewModel
    private lateinit var pref: SharedPreferenceUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pref = SharedPreferenceUtil(this)

        val factory = ViewModelFactory.getInstance(this)
        authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        binding.main.viewTreeObserver.addOnPreDrawListener {
            isKeyboardVisible()
            true
        }

        binding.tvNotYetHaveAccount.append(" ")
        binding.tvRegister.paintFlags = binding.tvRegister.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        binding.tvRegister.setOnClickListener(this)

        authViewModel.getLoginResult().observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnLogin.visibility = View.GONE
                    binding.layoutNotYetHaveAccount.visibility = View.GONE
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.visibility = View.VISIBLE
                    binding.layoutNotYetHaveAccount.visibility = View.VISIBLE

                    val res = result.data
                    val data = res.loginResult
                    Log.d(TAG,"data = $data")
                    pref.setToken(data.token)
                    pref.setAlreadyHaveAccount(true)
                    Toast.makeText(
                        this,
                        getString(R.string.success) + ": " + res.message,
                        Toast.LENGTH_SHORT
                    ).show()

                    val intent = Intent(this, MainActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    startActivity(intent)
                    finish()
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.visibility = View.VISIBLE
                    binding.layoutNotYetHaveAccount.visibility = View.VISIBLE
                    Toast.makeText(
                        this,
                        getString(R.string.failed) + ": " + result.error,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    // do nothing
                }
            }
        }

        binding.btnLogin.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btn_login -> {
                if (validateLogin()) {
                    authViewModel.login(
                        this,
                        binding.etEmail.text?.toString().orEmpty(),
                        binding.etPassword.text?.toString().orEmpty()
                    )
                }
            }
            R.id.tv_register -> {
                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun validateLogin(): Boolean {
        val email = binding.etEmail.text?.toString()?.trim().orEmpty()
        val password = binding.etPassword.text?.toString()?.trim().orEmpty()

        if (email.isEmpty()) {
            binding.etEmail.error = getString(R.string.empty_email_validation)
            Toast.makeText(this, getString(R.string.empty_email_validation), Toast.LENGTH_SHORT).show()
            return false
        }

        if (password.isEmpty()) {
            binding.etPassword.error = getString(R.string.empty_password_validation)
            Toast.makeText(this, getString(R.string.empty_password_validation), Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun isKeyboardVisible() {
        val rect = Rect()
        binding.main.getWindowVisibleDisplayFrame(rect)
        val screenHeight = binding.main.height
        val keypadHeight = screenHeight - rect.bottom

        if (keypadHeight > screenHeight * 0.15) {
            binding.layoutContent.setPadding(0, 0, 0, rect.bottom / 2)
        } else {
            binding.layoutContent.setPadding(0, 0, 0, 0)
        }
    }
}