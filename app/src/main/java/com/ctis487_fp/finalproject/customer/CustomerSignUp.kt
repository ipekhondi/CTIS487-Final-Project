package com.ctis487_fp.finalproject.customer

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ctis487_fp.finalproject.databinding.ActivityCustomerSignUpBinding
import com.ctis487_fp.finalproject.model.Customer
import com.ctis487_fp.finalproject.sales.SalesLogin
import com.ctis487_fp.finalproject.service.impl.AccountServiceImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch

class CustomerSignUp : AppCompatActivity() {
    lateinit var binding: ActivityCustomerSignUpBinding
    private var accountSignUp = AccountServiceImpl()
    private val database = FirebaseDatabase.getInstance().getReference("users")
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCustomerSignUpBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val intent = Intent(this, CustomerLogin::class.java)
            startActivity(intent)
        }

        binding.btnCustomerSignUp.setOnClickListener {
            val name = binding.etCustomerName.text.toString()
            val email = binding.etCustomerEmail.text.toString()
            val password = binding.etCustomerPassword.text.toString()
            val phone = binding.etCustomerPhone.text.toString()
            val region = binding.etCustomerRegion.text.toString()

            // Validate form fields
            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && phone.isNotEmpty() && region.isNotEmpty()) {
                lifecycleScope.launch {
                    try {
                        // Sign up the user with Firebase Authentication
                        accountSignUp.signUp(email, password)

                        // Get the UID of the signed-up user
                        val uid = auth.currentUser?.uid
                        if (uid != null) {
                            // Save customer data in Realtime Database under the UID
                            val customerData = Customer(name, email, password, phone, region)
                            database.child(uid).setValue(customerData)
                                .addOnSuccessListener {
                                    // Successfully saved to database
                                    Toast.makeText(
                                        this@CustomerSignUp,
                                        "Successfully signed up as Customer!",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    // Navigate to SalesLogin Activity
                                    val intent = Intent(this@CustomerSignUp, CustomerLogin::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    // Handle database error
                                    Toast.makeText(
                                        this@CustomerSignUp,
                                        "Error saving customer data: ${e.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        } else {
                            Toast.makeText(
                                this@CustomerSignUp,
                                "Error retrieving user UID.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        // Handle authentication errors
                        Toast.makeText(
                            this@CustomerSignUp,
                            "Error signing up: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                Toast.makeText(this, "Please fill in all fields!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
