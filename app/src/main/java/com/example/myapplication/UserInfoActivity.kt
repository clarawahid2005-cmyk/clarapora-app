package com.example.myapplication

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserInfoActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var genderSpinner: Spinner
    private lateinit var saveButton: Button

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private var editMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)

        // Initialize views
        nameEditText = findViewById(R.id.nameEditText)
        genderSpinner = findViewById(R.id.genderSpinner)
        saveButton = findViewById(R.id.saveButton)

        // Spinner setup
        val genderOptions = listOf("Select Gender", "Male", "Female", "Other")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        genderSpinner.adapter = adapter

        // Check if editing
        editMode = intent.getBooleanExtra("editMode", false)
        val userId = auth.currentUser?.uid

        if (editMode && userId != null) {
            // Load current user info
            db.collection("users").document(userId).get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        nameEditText.setText(doc.getString("name"))
                        val gender = doc.getString("gender")
                        val position = genderOptions.indexOf(gender)
                        genderSpinner.setSelection(if (position >= 0) position else 0)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to load info", Toast.LENGTH_SHORT).show()
                }
        }

        // Save button listener
        saveButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val gender = genderSpinner.selectedItem.toString()

            if (name.isEmpty() || gender == "Select Gender") {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (userId != null) {
                val userMap = hashMapOf(
                    "name" to name,
                    "gender" to gender
                )

                db.collection("users").document(userId)
                    .set(userMap)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Info saved successfully!", Toast.LENGTH_SHORT).show()
                        finish() // Go back to HomeActivity
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error saving info: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}
