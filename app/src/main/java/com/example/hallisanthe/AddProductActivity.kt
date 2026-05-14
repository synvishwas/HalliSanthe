package com.example.hallisanthe

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.hallisanthe.model.Product
import com.example.hallisanthe.utils.ImageCompressor
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID
import com.example.hallisanthe.utils.LanguageHelper
import android.content.Context
class AddProductActivity : AppCompatActivity() {

    private lateinit var imgPickedImage: ImageView
    private lateinit var layoutPickImage: LinearLayout
    private lateinit var etProductName: EditText
    private lateinit var etPrice: EditText
    private lateinit var etDescription: EditText
    private lateinit var etSellerName: EditText
    private lateinit var etSellerPhone: EditText
    private lateinit var etLocation: EditText
    private lateinit var etPin: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var spinnerCondition: Spinner
    private lateinit var btnUploadProduct: Button
    private lateinit var btnBackAdd: TextView
    private lateinit var progressBar: ProgressBar

    private var selectedImageUri: Uri? = null
    private val db = FirebaseFirestore.getInstance()

    // ✅ Updated categories
    private val categories = listOf(
        "Painting", "Drawing & Sketch", "Handicraft",
        "Textile & Weaving", "Pottery & Clay", "Toys & Dolls",
        "Jewellery", "Woodcraft", "Knitting & Crochet",
        "Organic Food", "Farming Produce", "Embroidery",
        "Masks & Decor", "Other"
    )

    // ✅ Condition options
    private val conditions = listOf(
        "🖐️ Handmade",
        "✨ Limited Edition",
        "💰 Price Negotiable",
        "🚚 Delivery Available",
        "🆕 New Arrival"
    )

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedImageUri = result.data?.data
            selectedImageUri?.let { uri ->
                imgPickedImage.visibility  = View.VISIBLE
                layoutPickImage.visibility = View.GONE
                Glide.with(this).load(uri).centerCrop().into(imgPickedImage)
            }
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LanguageHelper.applyLanguage(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        imgPickedImage   = findViewById(R.id.imgPickedImage)
        layoutPickImage  = findViewById(R.id.layoutPickImage)
        etProductName    = findViewById(R.id.etProductName)
        etPrice          = findViewById(R.id.etPrice)
        etDescription    = findViewById(R.id.etDescription)
        etSellerName     = findViewById(R.id.etSellerName)
        etSellerPhone    = findViewById(R.id.etSellerPhone)
        etLocation       = findViewById(R.id.etLocation)
        etPin            = findViewById(R.id.etPin)
        spinnerCategory  = findViewById(R.id.spinnerCategory)
        spinnerCondition = findViewById(R.id.spinnerCondition)
        btnUploadProduct = findViewById(R.id.btnUploadProduct)
        btnBackAdd       = findViewById(R.id.btnBackAdd)
        progressBar      = findViewById(R.id.progressBar)

        setupSpinners()

        layoutPickImage.setOnClickListener { openImagePicker() }
        imgPickedImage.setOnClickListener  { openImagePicker() }
        btnBackAdd.setOnClickListener      { finish() }
        btnUploadProduct.setOnClickListener { validateAndUpload() }
    }

    private fun setupSpinners() {
        spinnerCategory.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, categories
        ).also { it.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item) }

        spinnerCondition.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, conditions
        ).also { it.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item) }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        imagePickerLauncher.launch(intent)
    }

    private fun validateAndUpload() {
        val name        = etProductName.text.toString().trim()
        val price       = etPrice.text.toString().trim()
        val description = etDescription.text.toString().trim()
        val sellerName  = etSellerName.text.toString().trim()
        val sellerPhone = etSellerPhone.text.toString().trim()
        val location    = etLocation.text.toString().trim()
        val pin         = etPin.text.toString().trim()
        val category    = spinnerCategory.selectedItem.toString()
        val condition   = spinnerCondition.selectedItem.toString()

        when {
            name.isEmpty() -> { etProductName.error = "Required"; return }
            price.isEmpty() -> { etPrice.error = "Required"; return }
            sellerName.isEmpty() -> { etSellerName.error = "Required"; return }
            sellerPhone.isEmpty() || sellerPhone.length != 10 -> {
                etSellerPhone.error = "Enter valid 10 digit number"; return }
            location.isEmpty() -> { etLocation.error = "Required"; return }
            pin.isEmpty() || pin.length != 4 -> {
                etPin.error = "Enter 4 digit PIN"; return }
            selectedImageUri == null -> {
                Toast.makeText(this, "Please select a photo!", Toast.LENGTH_SHORT).show()
                return
            }
        }

        uploadProduct(name, price, category, condition,
            description, sellerName, sellerPhone, location, pin)
    }

    private fun uploadProduct(
        name: String, price: String, category: String,
        condition: String, description: String,
        sellerName: String, sellerPhone: String,
        location: String, pin: String
    ) {
        progressBar.visibility     = View.VISIBLE
        btnUploadProduct.isEnabled = false
        btnUploadProduct.text      = "Uploading..."

        val base64Image = ImageCompressor.compressToBase64(this, selectedImageUri!!)
        val productId   = UUID.randomUUID().toString()

        val product = Product(
            id          = productId,
            name        = name,
            price       = price,
            category    = category,
            condition   = condition,
            description = description,
            sellerName  = sellerName,
            sellerPhone = sellerPhone,
            location    = location,
            pin         = pin,
            imageUrl    = base64Image,
            isSold      = false,
            timestamp   = System.currentTimeMillis()
        )

        db.collection("products").document(productId).set(product)
            .addOnSuccessListener {
                progressBar.visibility     = View.GONE
                btnUploadProduct.isEnabled = true
                btnUploadProduct.text      = "🚀 Upload Product"
                Toast.makeText(this, "✅ Product uploaded!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                progressBar.visibility     = View.GONE
                btnUploadProduct.isEnabled = true
                btnUploadProduct.text      = "🚀 Upload Product"
                Toast.makeText(this, "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}