package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {

    private lateinit var nameTextView: TextView
    private lateinit var genderTextView: TextView
    private lateinit var logoutButton: Button
    private lateinit var editProfileButton: Button
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        title= "Home Page"
        nameTextView = findViewById(R.id.nameTextView)
        genderTextView = findViewById(R.id.genderTextView)
        logoutButton = findViewById(R.id.logoutButton)
        editProfileButton = findViewById(R.id.editProfileButton)
        val userId = auth.currentUser?.uid

        if (userId != null) {
            // Get user info from Firestore
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val name = document.getString("name") ?: "Not set"
                        val gender = document.getString("gender") ?: "Not set"

                        nameTextView.text = "Name: $name"
                        genderTextView.text = "Gender: $gender"
                    } else {
                        Toast.makeText(this, "No user info found.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to load user info.", Toast.LENGTH_SHORT).show()
                }
        } else {
            // If not logged in, return to signup/login
            Toast.makeText(this, "Please log in first.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, SignupActivity::class.java))
            finish()
        }

        logoutButton.setOnClickListener {
            auth.signOut()
            Toast.makeText(this, "Logged out successfully!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, SignupActivity::class.java))
            finish()
        }
        editProfileButton.setOnClickListener {
            val intent = Intent(this, UserInfoActivity::class.java)
            intent.putExtra("editMode", true) // indicate we are editing
            startActivity(intent)
        }
    }
}
