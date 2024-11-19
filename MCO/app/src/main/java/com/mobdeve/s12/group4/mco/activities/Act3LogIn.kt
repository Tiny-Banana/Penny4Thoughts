package com.mobdeve.s12.group4.mco.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mobdeve.s12.group4.mco.R
import com.mobdeve.s12.group4.mco.fragments.HomeFragment

class Act3LogIn : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_login)

        val etEmail: EditText = findViewById(R.id.etEmail)
        val etPassword: EditText = findViewById(R.id.etPassword)
        val btnLogin: Button = findViewById(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email == "test@example.com" && password == "password123") {
                val intent = Intent(this, HomeFragment::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
