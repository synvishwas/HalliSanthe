package com.example.hallisanthe

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hallisanthe.utils.LanguageHelper

class ContactUsActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LanguageHelper.applyLanguage(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_us)

        val btnBack    = findViewById<TextView>(R.id.btnBackContact)
        val etName     = findViewById<EditText>(R.id.etContactName)
        val etPhone    = findViewById<EditText>(R.id.etContactPhone)
        val etMessage  = findViewById<EditText>(R.id.etContactMessage)
        val btnSend    = findViewById<Button>(R.id.btnSendMessage)

        btnBack.setOnClickListener { finish() }

        btnSend.setOnClickListener {
            val name    = etName.text.toString().trim()
            val phone   = etPhone.text.toString().trim()
            val message = etMessage.text.toString().trim()

            if (name.isEmpty()) {
                etName.error = "Required"
                return@setOnClickListener
            }
            if (message.isEmpty()) {
                etMessage.error = "Required"
                return@setOnClickListener
            }

            val whatsappMsg = """
                *Halli-Santhe Contact Form*
                👤 Name: $name
                📞 Phone: $phone
                💬 Message: $message
            """.trimIndent()

            val url = "https://wa.me/919876543210?text=${Uri.encode(whatsappMsg)}"
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            } catch (e: Exception) {
                Toast.makeText(this, "WhatsApp not installed!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}