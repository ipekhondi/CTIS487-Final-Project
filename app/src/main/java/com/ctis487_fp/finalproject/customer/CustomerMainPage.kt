package com.ctis487_fp.finalproject.customer

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.ctis487_fp.finalproject.BackgroundChangeWorker
import com.ctis487_fp.finalproject.MainActivity
import com.ctis487_fp.finalproject.customeradapter.MedicineAdapter
import com.ctis487_fp.finalproject.databinding.ActivityCustomerMainPageBinding
import com.ctis487_fp.finalproject.model.Medicine
import com.ctis487_fp.finalproject.service.module.ItemSwipeListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class CustomerMainPage : AppCompatActivity() {
    lateinit var binding: ActivityCustomerMainPageBinding
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().getReference("users")

    private val selectedMedicines = mutableMapOf<String, Int>() // Track selected medicines and their quantities

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCustomerMainPageBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        val recyclerView: RecyclerView = binding.customerview // RecyclerView
        val medicinesList = mutableListOf<Medicine>()
        val spinner: Spinner = binding.custspinner

        // Spinner için sales isimlerini tutacak liste
        val items = mutableListOf<String>()

        // Müşterinin regionCustomer bilgisini al
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            val userId = user.uid

            database.child(userId).get().addOnSuccessListener { snapshot ->
                val regionCustomer = snapshot.child("regionCustomer").getValue(String::class.java)

                if (regionCustomer != null) {
                    // regionCustomer bilgisi varsa, sadece aynı region'daki sales'i yükle
                    loadSalesData(regionCustomer, items, spinner)
                } else {
                    Toast.makeText(this, "Bölge bilgisi bulunamadı!", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Kullanıcı bilgileri alınamadı!", Toast.LENGTH_SHORT).show()
            }
        }

        // Spinner seçim olayını dinle
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedSalesName = parent.getItemAtPosition(position) as String
                selectedMedicines.clear() // Seçilen ilaçları sıfırla
                changeBackgroundWithWorker(this@CustomerMainPage, position)
                fetchMedicinesForSales(selectedSalesName) // Seçili sales için ilaçları getir
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Hiçbir şey seçilmediğinde yapılacak işlemler
            }
        }

        // Edit Info butonu
        binding.btnEditInfo.setOnClickListener {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val userId = currentUser.uid

                // Kullanıcı bilgilerini düzenlemek için al
                database.child(userId).get().addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        val name = snapshot.child("name").getValue(String::class.java)
                        val email = snapshot.child("email").getValue(String::class.java)
                        val phone = snapshot.child("phone").getValue(String::class.java)

                        val intent = Intent(this, CustomerEditInfo::class.java).apply {
                            putExtra("userId", userId)
                            putExtra("name", name)
                            putExtra("email", email)
                            putExtra("phone", phone)
                        }
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Kullanıcı verisi bulunamadı!", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, "Veriler alınırken hata oluştu!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Kullanıcı oturumu açık değil!", Toast.LENGTH_SHORT).show()
            }
        }

        // Çıkış butonu
        binding.sgnoutbtn.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.buyBtn.setOnClickListener {
            binding.productNames.text = ""
            Toast.makeText(this, "Siparişiniz alınmıştır!", Toast.LENGTH_SHORT).show()
        }
    }

    // Aynı region'daki sales kullanıcılarını yükleme fonksiyonu
    private fun loadSalesData(regionCustomer: String, items: MutableList<String>, spinner: Spinner) {
        val salesDatabase = FirebaseDatabase.getInstance().getReference("sales")

        val getData = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                items.clear()
                for (userSnapshot in snapshot.children) {
                    val salesRegion = userSnapshot.child("region").getValue(String::class.java)
                    val salesName = userSnapshot.child("name").getValue(String::class.java)

                    if (salesRegion == regionCustomer && salesName != null) {
                        items.add(salesName)
                    }
                }

                if (items.isNotEmpty()) {
                    val adapter = ArrayAdapter(this@CustomerMainPage, android.R.layout.simple_spinner_item, items)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner.adapter = adapter

                    // İlk elemanı seç
                    spinner.setSelection(0)

                    // Varsayılan olarak ilk sales kullanıcısının ilaçlarını getir
                    fetchMedicinesForSales(items[0])
                } else {
                    Toast.makeText(this@CustomerMainPage, "Bu bölgede eczane bulunamadı!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@CustomerMainPage, "Veri yüklenirken hata: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        }

        salesDatabase.addListenerForSingleValueEvent(getData)
    }

    // Seçilen sales için ilaçları getir
    private fun fetchMedicinesForSales(salesName: String) {
        val medicinesList = mutableListOf<Medicine>()
        val salesDatabase = FirebaseDatabase.getInstance().getReference("sales")

        salesDatabase.orderByChild("name").equalTo(salesName).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                medicinesList.clear()
                for (saleSnapshot in snapshot.children) {
                    val medicinesSnapshot = saleSnapshot.child("medicines")
                    for (medicineSnapshot in medicinesSnapshot.children) {
                        val title = medicineSnapshot.child("title").getValue(String::class.java)
                        val description = medicineSnapshot.child("description").getValue(String::class.java)
                        val type = medicineSnapshot.child("type").getValue(Int::class.java)

                        title?.let {
                            type?.let { it1 -> Medicine(it1, title, description) }?.let { medicinesList.add(it) }
                        }
                    }
                }

                // Veriyi güncelleyin
                val adapter = MedicineAdapter(
                    medicinesList,
                    salesName,
                    { medicine, _ -> },
                    object : ItemSwipeListener {
                        override fun onItemSwiped(action: String) {
                            Toast.makeText(this@CustomerMainPage, "Action: $action", Toast.LENGTH_SHORT).show()
                        }
                    },
                    { medicine, action -> updateProductQuantity(medicine, action, salesName) }
                )

                if (binding.customerview.adapter == null) {
                    binding.customerview.layoutManager = LinearLayoutManager(this@CustomerMainPage)
                    binding.customerview.adapter = adapter
                    adapter.attachItemTouchHelper(binding.customerview, this@CustomerMainPage)
                } else {
                    (binding.customerview.adapter as MedicineAdapter).updateList(medicinesList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@CustomerMainPage, "Veri yüklenirken hata: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun updateProductQuantity(medicine: Medicine, action: String, pharmacyName: String) {
        val uniqueKey = "$pharmacyName-${medicine.title}"
        val currentQuantity = selectedMedicines[uniqueKey] ?: 0
        val updatedQuantity = when (action) {
            "increase" -> currentQuantity + 1
            "decrease" -> if (currentQuantity > 0) currentQuantity - 1 else 0
            else -> currentQuantity
        }

        if (updatedQuantity == 0) selectedMedicines.remove(uniqueKey)
        else selectedMedicines[uniqueKey] = updatedQuantity

        updateProductDisplay()
    }

    private fun updateProductDisplay() {
        val productInfo = selectedMedicines.entries.joinToString("\n") { "${it.key.split("-")[1]}: ${it.value}" }
        binding.productNames.text = productInfo
    }
    fun changeBackgroundWithWorker(context: Context, index: Int) {
        val inputData = Data.Builder()
            .putInt("index", index)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<BackgroundChangeWorker>()
            .setInputData(inputData)
            .build()

        // WorkManager işini başlat
        WorkManager.getInstance(context).enqueue(workRequest)

        // İş tamamlandıktan sonra arka planı güncelle
        WorkManager.getInstance(context).getWorkInfoByIdLiveData(workRequest.id).observe(this) { workInfo ->
            if (workInfo != null && workInfo.state.isFinished) {
                // İş tamamlandığında SharedPreferences'taki rengi oku ve arka planı güncelle
                val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                val color = sharedPref.getInt("background_color", Color.WHITE)
                binding.root.setBackgroundColor(color)
            }
        }
    }



    override fun onResume() {
        super.onResume()
        val currentUser = auth.currentUser
        currentUser?.let {
            val userId = it.uid
            database.child(userId).get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val name = snapshot.child("name").getValue(String::class.java)
                    binding.textView5.text = "Welcome back, $name!"
                }
            }
        }
        // Arka plan rengini güncelle
        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val color = sharedPref.getInt("background_color", Color.WHITE)
        binding.root.setBackgroundColor(color)
    }
}
