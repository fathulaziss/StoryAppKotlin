package com.example.storyappkotlin.ui.activity

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.storyappkotlin.R
import com.example.storyappkotlin.databinding.ActivityRegisterBinding
import com.example.storyappkotlin.ui.viewmodel.AuthViewModel
import com.example.storyappkotlin.ui.viewmodel.ViewModelFactory
import com.example.storyappkotlin.utils.SharedPreferenceUtil
import com.example.storyappkotlin.data.remote.Result

class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var authViewModel: AuthViewModel
    private lateinit var pref: SharedPreferenceUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pref = SharedPreferenceUtil(this)

        val factory = ViewModelFactory.getInstance(this)
        authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        binding.main.viewTreeObserver.addOnPreDrawListener {
            isKeyboardVisible()
            true
        }

        val toolbar = binding.appBar.toolbarTitleAppBar
        setSupportActionBar(toolbar)
        toolbar.title = getString(R.string.register)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        authViewModel.getRegisterResult().observe(this) { result ->
            result?.let {
                when (it) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.btnRegister.visibility = View.GONE
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.btnRegister.visibility = View.VISIBLE
                        pref.setAlreadyHaveAccount(true)
                        val res = it.data
                        Toast.makeText(this, getString(R.string.success) + ": ${res.message}", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, LoginActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        startActivity(intent)
                        finish()
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.btnRegister.visibility = View.VISIBLE
                        Toast.makeText(this, getString(R.string.failed) + ": ${it.error}", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        // do nothing
                    }
                }
            }
        }

        binding.btnRegister.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        if (view?.id == R.id.btn_register && validateRegister()) {
            authViewModel.register(
                this,
                binding.etName.text?.toString() ?: "",
                binding.etEmail.text?.toString() ?: "",
                binding.etPassword.text?.toString() ?: ""
            )
        }
    }

    private fun validateRegister(): Boolean {
        val name = binding.etName.text?.toString()?.trim().orEmpty()
        val email = binding.etEmail.text?.toString()?.trim().orEmpty()
        val password = binding.etPassword.text?.toString()?.trim().orEmpty()

        if (name.isEmpty()) {
            binding.etName.requestFocus()
            binding.etName.error = getString(R.string.empty_name_validation)
            Toast.makeText(this, getString(R.string.empty_name_validation), Toast.LENGTH_SHORT).show()
            return false
        }

        if (email.isEmpty()) {
            binding.etEmail.requestFocus()
            binding.etEmail.error = getString(R.string.empty_email_validation)
            Toast.makeText(this, getString(R.string.empty_email_validation), Toast.LENGTH_SHORT).show()
            return false
        }

        if (password.isEmpty()) {
            binding.etPassword.requestFocus()
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

        binding.layoutContent.setPadding(
            0, 0, 0,
            if (keypadHeight > screenHeight * 0.15) rect.bottom / 2 else 0
        )
    }
}