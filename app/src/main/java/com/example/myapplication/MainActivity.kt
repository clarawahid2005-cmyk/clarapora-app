package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val isRemembered = sharedPrefs.getBoolean("remembered", false)
        val user = FirebaseAuth.getInstance().currentUser

        if (isRemembered && user != null) {
            startActivity(Intent(this, HomeActivity::class.java))
        } else {
            startActivity(Intent(this, SignupActivity::class.java))
        }
        finish()
    }
}
