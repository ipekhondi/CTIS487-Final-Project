package com.ctis487_fp.finalproject.sales

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ctis487_fp.finalproject.databinding.ActivitySalesEditInfoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SalesEditInfo : AppCompatActivity() {
    lateinit var binding: ActivitySalesEditInfoBinding
    private val database = FirebaseDatabase.getInstance().getReference("sales")
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySalesEditInfoBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Retrieve passed data
        val userId = intent.getStringExtra("userId")
        val currentEmail = intent.getStringExtra("email")
        val name = intent.getStringExtra("name")
        val phone = intent.getStringExtra("phone")
        val region = intent.getStringExtra("region")

        // Pre-fill fields
        binding.etSalesName.setText(name)
        binding.etSalesEmail.setText(currentEmail)
        binding.etSalesPhone.setText(phone)
        binding.etSalesRegion.setText(region)

        // Save changes to database
        binding.editBtn.setOnClickListener {
            val updatedName = binding.etSalesName.text.toString()
            val updatedEmail = binding.etSalesEmail.text.toString()
            val updatedPhone = binding.etSalesPhone.text.toString()
            val updatedRegion = binding.etSalesRegion.text.toString()

            if (userId != null) {
                // Update the Firebase Realtime Database
                val updates = mapOf(
                    "name" to updatedName,
                    "email" to updatedEmail,
                    "phone" to updatedPhone,
                    "region" to updatedRegion
                )

                database.child(userId).updateChildren(updates).addOnSuccessListener {
                    Toast.makeText(this, "Information updated successfully in database!", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener { e ->
                    Toast.makeText(this, "Error updating database: ${e.message}", Toast.LENGTH_SHORT).show()
                }

                // Update email in Firebase Authentication
                auth.currentUser?.updateEmail(updatedEmail)?.addOnSuccessListener {
                    Toast.makeText(this, "Email updated successfully!", Toast.LENGTH_SHORT).show()
                    finish()
                }?.addOnFailureListener { e ->
                    Toast.makeText(this, "Error updating email: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "User ID is missing!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
