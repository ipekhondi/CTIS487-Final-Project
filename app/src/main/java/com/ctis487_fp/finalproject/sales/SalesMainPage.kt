package com.ctis487_fp.finalproject.sales

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ctis487_fp.finalproject.MainActivity
import com.ctis487_fp.finalproject.R
import com.ctis487_fp.finalproject.customeradapter.CustomerAdapter
import com.ctis487_fp.finalproject.databinding.ActivitySalesMainPageBinding
import com.ctis487_fp.finalproject.model.Medicine
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*



class SalesMainPage : AppCompatActivity() {

    private lateinit var binding: ActivitySalesMainPageBinding
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private lateinit var medicineAdapter: CustomerAdapter
    private val medicines = mutableListOf<Medicine>()

    private val ADD_MEDICINE_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySalesMainPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupListeners()

        loadMedicines()
    }

    private fun setupRecyclerView() {
        binding.medicineView.layoutManager = LinearLayoutManager(this)
        medicineAdapter = CustomerAdapter(medicines) { medicine, position ->
            showEditDialog(medicine, position)
        }
        binding.medicineView.adapter = medicineAdapter
    }

    private fun setupListeners() {
        binding.addNew.setOnClickListener {
            val intent = Intent(this, SalesAddProduct::class.java)
            startActivityForResult(intent, ADD_MEDICINE_REQUEST_CODE)
        }

        binding.editInfo.setOnClickListener { handleEditInfo() }
        binding.signoutbtn.setOnClickListener { handleSignOut() }
    }

    private fun handleEditInfo() {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            val userId = user.uid
            val userEmail = user.email
            val database = FirebaseDatabase.getInstance().getReference("sales")

            database.child(userId).get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val name = snapshot.child("name").getValue(String::class.java)
                    val phone = snapshot.child("phone").getValue(String::class.java)
                    val region = snapshot.child("region").getValue(String::class.java)

                    val intent = Intent(this, SalesEditInfo::class.java).apply {
                        putExtra("userId", userId)
                        putExtra("email", userEmail)
                        putExtra("name", name)
                        putExtra("phone", phone)
                        putExtra("region", region)
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "User data not found!", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { e ->
                Toast.makeText(this, "Error retrieving user data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(this, "User not authenticated!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleSignOut() {
        auth.signOut()
        medicines.clear()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun loadMedicines() {
        val userId = auth.currentUser?.uid ?: return
        val database = FirebaseDatabase.getInstance().getReference("sales")
            .child(userId)
            .child("medicines")

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    medicines.clear()
                    val uniqueMedicines = mutableSetOf<Medicine>()

                    for (medicineSnapshot in snapshot.children) {
                        val medicine = medicineSnapshot.getValue(Medicine::class.java)
                        medicine?.let {
                            uniqueMedicines.add(it)
                        }
                    }
                    medicines.addAll(uniqueMedicines)
                    medicineAdapter.notifyDataSetChanged()
                } catch (e: Exception) {
                    Toast.makeText(this@SalesMainPage, "Error loading medicines: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SalesMainPage, "Failed to load medicines: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showEditDialog(medicine: Medicine, position: Int) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_medicine, null)
        val titleEdit = dialogView.findViewById<EditText>(R.id.editTextTitle)
        val descriptionEdit = dialogView.findViewById<EditText>(R.id.editTextDescription)
        val typeSpinner = dialogView.findViewById<Spinner>(R.id.spinnerType)

        ArrayAdapter.createFromResource(
            this,
            R.array.medicine_types,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            typeSpinner.adapter = adapter
        }

        titleEdit.setText(medicine.title)
        descriptionEdit.setText(medicine.description)
        typeSpinner.setSelection(if (medicine.type == 1) 0 else 1)

        AlertDialog.Builder(this)
            .setTitle("Edit Medicine")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val newTitle = titleEdit.text.toString()
                val newDescription = descriptionEdit.text.toString()
                val newType = if (typeSpinner.selectedItemPosition == 0) 1 else 2

                if (newTitle.isNotEmpty() && newDescription.isNotEmpty()) {
                    val updatedMedicine = Medicine(newType, newTitle, newDescription)
                    updateMedicineInFirebase(updatedMedicine, position)
                } else {
                    Toast.makeText(this, "Title and description cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Delete") { _, _ -> showDeleteConfirmationDialog(position) }
            .setNeutralButton("Cancel", null)
            .show()
    }

    private fun showDeleteConfirmationDialog(position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Delete Medicine")
            .setMessage("Are you sure you want to delete this medicine?")
            .setPositiveButton("Yes") { _, _ -> deleteMedicineFromFirebase(position) }
            .setNegativeButton("No", null)
            .show()
    }

    private fun updateMedicineInFirebase(medicine: Medicine, position: Int) {
        val userId = auth.currentUser?.uid ?: return
        val database = FirebaseDatabase.getInstance().getReference("sales")
            .child(userId)
            .child("medicines")

        database.child(position.toString()).setValue(medicine)
            .addOnSuccessListener {
                medicines[position] = medicine
                medicineAdapter.notifyItemChanged(position)
                Toast.makeText(this, "Medicine updated successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to update medicine: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteMedicineFromFirebase(position: Int) {
        val userId = auth.currentUser?.uid ?: return
        val database = FirebaseDatabase.getInstance().getReference("sales")
            .child(userId)
            .child("medicines")

        database.child(position.toString()).removeValue()
            .addOnSuccessListener {
                medicines.removeAt(position)
                medicineAdapter.notifyItemRemoved(position)
                Toast.makeText(this, "Medicine deleted successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to delete medicine: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onResume() {
        super.onResume()
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            val userId = user.uid
            val database = FirebaseDatabase.getInstance().getReference("sales")
            database.child(userId).get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val name = snapshot.child("name").getValue(String::class.java)
                    binding.textView4.text = "Welcome back, $name!"
                }
            }.addOnFailureListener { e ->
                Toast.makeText(this, "Error retrieving user data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_MEDICINE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            val title = data.getStringExtra("medicineTitle") ?: ""
            val description = data.getStringExtra("medicineDescription") ?: ""
            val type = data.getIntExtra("medicineType", 1)

            if (title.isNotEmpty() && description.isNotEmpty()) {
                val newMedicine = Medicine(type, title, description)
                medicines.add(newMedicine)
                medicineAdapter.notifyItemInserted(medicines.size - 1)
            } else {
                Toast.makeText(this, "Medicine title and description cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }
}


