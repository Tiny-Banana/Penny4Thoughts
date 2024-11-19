package com.mobdeve.s12.group4.mco.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import com.mobdeve.s12.group4.mco.R

class Act1Welcome: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_welcome)

        val btnLogin: Button = findViewById(R.id.btnLogin)
        val btnSignup: Button = findViewById(R.id.btnSignup)

        btnLogin.setOnClickListener {
            val intent = Intent(this, Act3LogIn::class.java)
            startActivity(intent)
        }

        btnSignup.setOnClickListener {
            val intent = Intent(this, Act2SignUp::class.java)
            startActivity(intent)
        }
    }
}
