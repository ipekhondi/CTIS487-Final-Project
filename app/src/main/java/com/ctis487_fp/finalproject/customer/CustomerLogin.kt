package com.ctis487_fp.finalproject.customer

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ctis487_fp.finalproject.databinding.ActivityCustomerLoginBinding
import com.ctis487_fp.finalproject.service.impl.AccountServiceImpl
import kotlinx.coroutines.launch

class CustomerLogin : AppCompatActivity() {
    lateinit var binding: ActivityCustomerLoginBinding
    private val accountSignIn = AccountServiceImpl() // AccountService instance

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCustomerLoginBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        // Handle Login Button Click
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            // Check if the fields are not empty
            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Launch coroutine to sign in
                lifecycleScope.launch {
                    try {
                        accountSignIn.signIn(email, password) // Sign in using the AccountService
                        // Show success message
                        Toast.makeText(this@CustomerLogin, "Sales Login Successful!", Toast.LENGTH_SHORT).show()

                        // Proceed to Sales Main Page after successful login
                        val intent = Intent(this@CustomerLogin, CustomerMainPage::class.java) // Replace SalesMainPage with your main page activity
                        startActivity(intent)
                        finish() // Close the current login activity
                    } catch (e: Exception) {
                        // Handle errors (e.g., incorrect credentials)
                        Toast.makeText(this@CustomerLogin, "Login failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                // Show a message if fields are empty
                Toast.makeText(this, "Please fill in all fields!", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle Sign Up Button Click
        binding.btnSignup.setOnClickListener {
            val intent = Intent(this, CustomerSignUp::class.java)
            startActivity(intent)
        }
        binding.btnSignup.setOnClickListener {
            val intent = Intent(this, CustomerSignUp::class.java)
            startActivity(intent)
        }
    }
}