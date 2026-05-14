package com.example.hallisanthe

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.example.hallisanthe.utils.LanguageHelper
import com.google.firebase.firestore.FirebaseFirestore

class EditProductActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etPrice: EditText
    private lateinit var etDiscount: EditText
    private lateinit var etDescription: EditText
    private lateinit var etLocation: EditText
    private lateinit var switchFeatured: SwitchCompat
    private lateinit var switchDelivery: SwitchCompat
    private lateinit var btnSave: Button
    private lateinit var progressBar: ProgressBar

    private val db = FirebaseFirestore.getInstance()

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LanguageHelper.applyLanguage(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_product)

        etName          = findViewById(R.id.etEditName)
        etPrice         = findViewById(R.id.etEditPrice)
        etDiscount      = findViewById(R.id.etEditDiscount)
        etDescription   = findViewById(R.id.etEditDescription)
        etLocation      = findViewById(R.id.etEditLocation)
        switchFeatured  = findViewById(R.id.switchFeatured)
        switchDelivery  = findViewById(R.id.switchDelivery)
        btnSave         = findViewById(R.id.btnSaveEdit)
        progressBar     = findViewById(R.id.progressBarEdit)

        // Get existing data
        val productId   = intent.getStringExtra("id")          ?: ""
        val name        = intent.getStringExtra("name")        ?: ""
        val price       = intent.getStringExtra("price")       ?: ""
        val discount    = intent.getStringExtra("discount")    ?: ""
        val description = intent.getStringExtra("description") ?: ""
        val location    = intent.getStringExtra("location")    ?: ""
        val isFeatured  = intent.getBooleanExtra("isFeatured", false)
        val isDelivery  = intent.getBooleanExtra("deliveryAvailable", false)

        // Pre-fill
        etName.setText(name)
        etPrice.setText(price)
        etDiscount.setText(discount)
        etDescription.setText(description)
        etLocation.setText(location)
        switchFeatured.isChecked = isFeatured
        switchDelivery.isChecked = isDelivery

        findViewById<TextView>(R.id.btnBackEdit).setOnClickListener { finish() }

        btnSave.setOnClickListener {
            val newName        = etName.text.toString().trim()
            val newPrice       = etPrice.text.toString().trim()
            val newDiscount    = etDiscount.text.toString().trim()
            val newDescription = etDescription.text.toString().trim()
            val newLocation    = etLocation.text.toString().trim()

            if (newName.isEmpty()) {
                etName.error = "Required"
                return@setOnClickListener
            }
            if (newPrice.isEmpty()) {
                etPrice.error = "Required"
                return@setOnClickListener
            }

            progressBar.visibility = View.VISIBLE
            btnSave.isEnabled      = false

            val updates = hashMapOf<String, Any>(
                "name"              to newName,
                "price"             to newPrice,
                "discount"          to newDiscount,
                "description"       to newDescription,
                "location"          to newLocation,
                "isFeatured"        to switchFeatured.isChecked,
                "deliveryAvailable" to switchDelivery.isChecked
            )

            db.collection("products").document(productId)
                .update(updates)
                .addOnSuccessListener {
                    progressBar.visibility = View.GONE
                    btnSave.isEnabled      = true
                    Toast.makeText(this, "✅ Product updated!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    progressBar.visibility = View.GONE
                    btnSave.isEnabled      = true
                    Toast.makeText(this, "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}