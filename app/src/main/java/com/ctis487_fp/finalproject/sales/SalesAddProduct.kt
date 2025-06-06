package com.ctis487_fp.finalproject.sales

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.bumptech.glide.Glide
import com.ctis487_fp.finalproject.databinding.ActivitySalesAddProductBinding
import com.ctis487_fp.finalproject.db.MedicineStock
import com.ctis487_fp.finalproject.db.MedicineStockRoomDatabase
import com.ctis487_fp.finalproject.model.Medicine
import com.ctis487_fp.finalproject.model.MedicineItem
import com.ctis487_fp.finalproject.retrofit.ApiClient
import com.ctis487_fp.finalproject.retrofit.ApiService
import com.ctis487_fp.finalproject.util.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SalesAddProduct : AppCompatActivity() {
    private lateinit var binding: ActivitySalesAddProductBinding
    private lateinit var apiService: ApiService
    private var selectedMedicineItem: MedicineItem? = null
    private val medicineList = mutableListOf<MedicineItem>()
    private val roomDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            MedicineStockRoomDatabase::class.java,
            Constants.DATABASENAME
        ).build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySalesAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeApiService()
        setupClickListeners()
    }

    private fun initializeApiService() {
        apiService = ApiClient.getClient().create(ApiService::class.java)
        loadMedicinesFromApi()
    }

    private fun setupClickListeners() {
        binding.addbtn.setOnClickListener {
            addSelectedMedicine()
        }

        binding.backtoMain.setOnClickListener {
            finish()
        }
    }

    private fun loadMedicinesFromApi() {
        apiService.getMedicines().enqueue(object : Callback<List<MedicineItem>> {
            override fun onResponse(
                call: Call<List<MedicineItem>>,
                response: Response<List<MedicineItem>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        medicineList.clear()
                        medicineList.addAll(it)
                        setupSpinner()
                    }
                } else {
                    Toast.makeText(this@SalesAddProduct, "Failed to load medicines", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<MedicineItem>>, t: Throwable) {
                Toast.makeText(this@SalesAddProduct, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter(
            this,
            R.layout.simple_spinner_item,
            medicineList.map { it.title }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = adapter

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedMedicineItem = medicineList[position]
                updateMedicinePreview(selectedMedicineItem)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedMedicineItem = null
            }
        }
    }

    private fun updateMedicinePreview(medicineItem: MedicineItem?) {
        medicineItem?.let {
            Glide.with(this)
                .load(it.imageUrl)
                .into(binding.medicineImagePreview)
        }
    }

    private fun addSelectedMedicine() {
        val medicineItem = selectedMedicineItem
        if (medicineItem == null) {
            Toast.makeText(this, "Please select a medicine", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        val medicine = Medicine(
            type = medicineItem.type,
            title = medicineItem.title,
            description = medicineItem.description,
            stock = medicineItem.stock
        )

        // Firebase'e kaydetme
        val firebaseDatabase = FirebaseDatabase.getInstance()
            .getReference("sales")
            .child(userId)
            .child("medicines")

        firebaseDatabase.push().setValue(medicine)
            .addOnSuccessListener {
                saveToRoomDatabase(medicineItem)
                sendResultAndFinish(medicine)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to add medicine: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveToRoomDatabase(medicineItem: MedicineItem) {
        val medicineStock = MedicineStock(
            uidFirebase = medicineItem.title ?: "Unknown",
            stockMedicine = medicineItem.stock
        )

        Thread {
            roomDatabase.medicineStockDao().insertMedicineStock(medicineStock)
            runOnUiThread {
                Toast.makeText(this, "Medicine saved to Room database!", Toast.LENGTH_SHORT).show()
            }
        }.start()
    }

    private fun sendResultAndFinish(medicine: Medicine) {
        val resultIntent = Intent().apply {
            putExtra("medicineTitle", medicine.title)
            putExtra("medicineDescription", medicine.description)
            putExtra("medicineType", medicine.type)
        }
        setResult(RESULT_OK, resultIntent)
        Toast.makeText(this, "Medicine added successfully!", Toast.LENGTH_SHORT).show()
        finish()
    }
}
