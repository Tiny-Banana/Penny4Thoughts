package com.mobdeve.s12.group4.mco.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mobdeve.s12.group4.mco.R

class Act4ProfMng : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_profilemanagement)

        val etNewName: EditText = findViewById(R.id.etNewName)
        val etNewEmail: EditText = findViewById(R.id.etNewEmail)
        val etNewPassword: EditText = findViewById(R.id.etNewPassword)
        val btnCancel: Button = findViewById(R.id.btnCancel)
        val btnSave: Button = findViewById(R.id.btnSave)

        btnCancel.setOnClickListener {
            finish()
        }

        btnSave.setOnClickListener {
            val newName = etNewName.text.toString()
            val newEmail = etNewEmail.text.toString()
            val newPassword = etNewPassword.text.toString()

            if (newName.isNotEmpty() || newEmail.isNotEmpty() || newPassword.isNotEmpty()) {
                Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please fill at least one field", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
