package com.ctis487_fp.finalproject

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.ctis487_fp.finalproject.customer.CustomerLogin
import com.ctis487_fp.finalproject.databinding.ActivityMainBinding
import com.ctis487_fp.finalproject.sales.SalesLogin
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val auth = FirebaseAuth.getInstance() // FirebaseAuth instance

    private var mediaPlayer: MediaPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        // Apply fade-in and slide-in animations
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in)

        // Apply animations to elements
        binding.tvWelcome.startAnimation(fadeIn)
        binding.imageView3.startAnimation(slideIn)
        binding.btnCustomer.startAnimation(slideIn)
        binding.btnSales.startAnimation(slideIn)

        binding.imageView3.setImageResource(R.drawable.logo)

        // Navigate to Customer Login/Signup Activity
        binding.btnCustomer.setOnClickListener {
            val intent = Intent(this, CustomerLogin::class.java)
            startActivity(intent)
        }

        mediaPlayer?.isLooping = true
        // Navigate to Sales Login/Signup Activity
        binding.btnSales.setOnClickListener {
            val intent = Intent(this, SalesLogin::class.java)
            startActivity(intent)
        }
        playWelcomeSound()

    }
    private fun playWelcomeSound() {
        // MediaPlayer ile ses dosyasını çalma
        mediaPlayer = MediaPlayer.create(this, R.raw.videoplayback)
        mediaPlayer?.start()
    }
}
