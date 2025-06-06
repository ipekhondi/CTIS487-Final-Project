package com.ctis487_fp.finalproject.customer

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.ctis487_fp.finalproject.databinding.ActivityCustomerEditInfoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class CustomerEditInfo : AppCompatActivity() {
    lateinit var binding: ActivityCustomerEditInfoBinding
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().getReference("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCustomerEditInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get user data passed from the previous activity
        val userId = intent.getStringExtra("userId")
        val name = intent.getStringExtra("name")
        val email = intent.getStringExtra("email")
        val phone = intent.getStringExtra("phone")
        val region = intent.getStringExtra("region")
        // Populate fields with current user data
        binding.etCustomerName.setText(name)
        binding.etCustomerEmail.setText(email)
        binding.etCustomerPhone.setText(phone)
        binding.etCustomerRegion.setText(region)

        binding.editCustomerInfo.setOnClickListener {
            val updatedName = binding.etCustomerName.text.toString()
            val updatedEmail = binding.etCustomerEmail.text.toString()
            val updatedPhone = binding.etCustomerPhone.text.toString()
            val updatedRegion = binding.etCustomerRegion.text.toString()
            // Validate inputs
            if (updatedName.isNotEmpty() && updatedEmail.isNotEmpty() && updatedPhone.isNotEmpty() && updatedRegion.isNotEmpty()) {
                if (userId != null) {
                    // Update user data in Firebase Database
                    val updatedData = mapOf(
                        "name" to updatedName,
                        "email" to updatedEmail,
                        "phone" to updatedPhone,
                        "regionCustomer" to updatedRegion
                    )

                    database.child(userId).updateChildren(updatedData)
                        .addOnSuccessListener {
                            Toast.makeText(this, "User info updated successfully!", Toast.LENGTH_SHORT).show()
                            finish() // Close the activity
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error updating info: ${e.message}", Toast.LENGTH_SHORT).show()
                        }

                    // Optionally update Firebase Authentication email
                    auth.currentUser?.updateEmail(updatedEmail)?.addOnSuccessListener {
                        Toast.makeText(this, "Authentication email updated successfully!", Toast.LENGTH_SHORT).show()
                    }?.addOnFailureListener { e ->
                        Toast.makeText(this, "Error updating auth email: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "User ID not found!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill in all fields!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
