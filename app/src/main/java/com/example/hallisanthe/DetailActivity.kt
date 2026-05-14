package com.example.hallisanthe

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hallisanthe.adapter.SimilarAdapter
import com.example.hallisanthe.model.Product
import com.example.hallisanthe.utils.LanguageHelper
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class DetailActivity : AppCompatActivity() {

    private lateinit var imgProductDetail: ImageView
    private lateinit var tvDetailName: TextView
    private lateinit var tvDetailPrice: TextView
    private lateinit var tvDetailCategory: TextView
    private lateinit var tvDetailCondition: TextView
    private lateinit var tvDetailDescription: TextView
    private lateinit var tvDetailSellerName: TextView
    private lateinit var tvDetailSellerPhone: TextView
    private lateinit var tvDetailLocation: TextView
    private lateinit var tvSoldBadge: TextView
    private lateinit var tvDetailViewCount: TextView
    private lateinit var tvDetailRating: TextView
    private lateinit var tvDetailEnquiry: TextView
    private lateinit var tvDetailDiscount: TextView
    private lateinit var tvDeliveryTag: TextView
    private lateinit var btnMessageSeller: Button
    private lateinit var btnCallSeller: Button
    private lateinit var btnBack: TextView
    private lateinit var btnShare: TextView
    private lateinit var btnReport: Button
    private lateinit var btnReviews: Button
    private lateinit var layoutArtisanProfile: LinearLayout
    private lateinit var recyclerSimilar: RecyclerView

    private val db = FirebaseFirestore.getInstance()
    private val similarList = mutableListOf<Product>()
    private lateinit var similarAdapter: SimilarAdapter

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LanguageHelper.applyLanguage(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        initViews()

        val id          = intent.getStringExtra("id")          ?: ""
        val name        = intent.getStringExtra("name")        ?: "Unknown"
        val price       = intent.getStringExtra("price")       ?: "0"
        val category    = intent.getStringExtra("category")    ?: "General"
        val condition   = intent.getStringExtra("condition")   ?: ""
        val description = intent.getStringExtra("description") ?: "No description."
        val sellerName  = intent.getStringExtra("sellerName")  ?: "Unknown"
        val sellerPhone = intent.getStringExtra("sellerPhone") ?: ""
        val imageUrl    = intent.getStringExtra("imageUrl")    ?: ""
        val isSold      = intent.getBooleanExtra("isSold", false)
        val location    = intent.getStringExtra("location")    ?: ""
        val discount    = intent.getStringExtra("discount")    ?: ""
        val isFeatured  = intent.getBooleanExtra("isFeatured", false)
        val isDelivery  = intent.getBooleanExtra("deliveryAvailable", false)
        val viewCount   = intent.getIntExtra("viewCount", 0)
        val avgRating   = intent.getFloatExtra("avgRating", 0f)

        // Set data
        tvDetailName.text        = name
        tvDetailCategory.text    = category
        tvDetailCondition.text   = condition
        tvDetailDescription.text = description
        tvDetailSellerName.text  = sellerName
        tvDetailSellerPhone.text = "📞 $sellerPhone"
        tvDetailLocation.text    = if (location.isNotEmpty()) "📍 $location" else ""
        tvDetailViewCount.text   = "👁️ $viewCount views"
        tvDetailRating.text      = "⭐ ${"%.1f".format(avgRating)}"
        tvDeliveryTag.visibility = if (isDelivery) View.VISIBLE else View.GONE

        // ✅ Price with discount — Step 10
        if (discount.isNotEmpty() && discount != "0") {
            val originalPrice   = price.toDoubleOrNull() ?: 0.0
            val discountPct     = discount.toDoubleOrNull() ?: 0.0
            val discountedPrice = originalPrice - (originalPrice * discountPct / 100)
            tvDetailPrice.text    = "₹ ${discountedPrice.toInt()}"
            tvDetailDiscount.text = "Was ₹$price | $discount% OFF"
            tvDetailDiscount.visibility = View.VISIBLE
        } else {
            tvDetailPrice.text = "₹ $price"
            tvDetailDiscount.visibility = View.GONE
        }

        // SOLD state
        if (isSold) {
            tvSoldBadge.visibility = View.VISIBLE
            btnMessageSeller.isEnabled = false
            btnMessageSeller.text = getString(R.string.item_sold)
            btnMessageSeller.backgroundTintList =
                android.content.res.ColorStateList.valueOf(
                    Color.parseColor("#9E9E9E"))
        }

        // Load image
        loadImage(imageUrl)

        // ✅ Step 7 — Increment view count
        if (id.isNotEmpty()) incrementViewCount(id)

        // ✅ Step 18 — Load similar products
        setupSimilarProducts(category, id)

        // Tap image → full screen
        imgProductDetail.setOnClickListener {
            showFullScreenImage(imageUrl)
        }

        btnBack.setOnClickListener { finish() }

        // ✅ Share — Step(already done)
        btnShare.setOnClickListener {
            val shareText = """
                🏪 Halli-Santhe — Check this out!
                🏷️ $name
                💰 ₹$price
                📦 $category
                📍 $location
                Artisan: $sellerName | 📞 $sellerPhone
                #HalliSanthe #VocalForLocal
            """.trimIndent()
            val i = Intent(Intent.ACTION_SEND)
            i.type = "text/plain"
            i.putExtra(Intent.EXTRA_TEXT, shareText)
            startActivity(Intent.createChooser(i, getString(R.string.share_via)))
        }

        // Artisan profile
        layoutArtisanProfile.setOnClickListener {
            val i = Intent(this, ArtisanProfileActivity::class.java).apply {
                putExtra("sellerName",  sellerName)
                putExtra("sellerPhone", sellerPhone)
                putExtra("location",    location)
            }
            startActivity(i)
        }

        // WhatsApp
        btnMessageSeller.setOnClickListener {
            if (sellerPhone.isNotEmpty()) {
                // ✅ Step 8 — Increment enquiry count
                if (id.isNotEmpty()) incrementEnquiryCount(id)
                val msg = "Hi! I'm interested in: *$name* ₹$price. Available?"
                val url = "https://wa.me/91$sellerPhone?text=${Uri.encode(msg)}"
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                } catch (e: Exception) {
                    Toast.makeText(this, "WhatsApp not installed!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Phone not available", Toast.LENGTH_SHORT).show()
            }
        }

        // Call
        btnCallSeller.setOnClickListener {
            if (sellerPhone.isNotEmpty()) {
                // ✅ Step 8 — Increment enquiry count
                if (id.isNotEmpty()) incrementEnquiryCount(id)
                startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$sellerPhone")))
            } else {
                Toast.makeText(this, "Phone not available", Toast.LENGTH_SHORT).show()
            }
        }

        // ✅ Step 6 — Report
        btnReport.setOnClickListener { showReportDialog(id, name) }

        // ✅ Step 17 — Reviews
        btnReviews.setOnClickListener {
            val i = Intent(this, ReviewsActivity::class.java)
            i.putExtra("productId", id)
            startActivity(i)
        }
    }

    private fun initViews() {
        imgProductDetail      = findViewById(R.id.imgProductDetail)
        tvDetailName          = findViewById(R.id.tvDetailName)
        tvDetailPrice         = findViewById(R.id.tvDetailPrice)
        tvDetailCategory      = findViewById(R.id.tvDetailCategory)
        tvDetailCondition     = findViewById(R.id.tvDetailCondition)
        tvDetailDescription   = findViewById(R.id.tvDetailDescription)
        tvDetailSellerName    = findViewById(R.id.tvDetailSellerName)
        tvDetailSellerPhone   = findViewById(R.id.tvDetailSellerPhone)
        tvDetailLocation      = findViewById(R.id.tvDetailLocation)
        tvSoldBadge           = findViewById(R.id.tvSoldBadge)
        tvDetailViewCount     = findViewById(R.id.tvDetailViewCount)
        tvDetailRating        = findViewById(R.id.tvDetailRating)
        tvDetailEnquiry       = findViewById(R.id.tvDetailEnquiry)
        tvDetailDiscount      = findViewById(R.id.tvDetailDiscount)
        tvDeliveryTag         = findViewById(R.id.tvDeliveryTag)
        btnMessageSeller      = findViewById(R.id.btnMessageSeller)
        btnCallSeller         = findViewById(R.id.btnCallSeller)
        btnBack               = findViewById(R.id.btnBack)
        btnShare              = findViewById(R.id.btnShare)
        btnReport             = findViewById(R.id.btnReport)
        btnReviews            = findViewById(R.id.btnReviews)
        layoutArtisanProfile  = findViewById(R.id.layoutArtisanProfile)
        recyclerSimilar       = findViewById(R.id.recyclerSimilar)
    }

    private fun loadImage(imageUrl: String) {
        if (imageUrl.isNotEmpty()) {
            try {
                val bytes  = Base64.decode(imageUrl, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                imgProductDetail.setImageBitmap(bitmap)
            } catch (e: Exception) {
                imgProductDetail.setImageResource(R.drawable.ic_launcher_background)
            }
        }
    }

    private fun showFullScreenImage(imageUrl: String) {
        val dialog = Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        val fullImg = ImageView(this)
        fullImg.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        fullImg.setBackgroundColor(Color.BLACK)
        fullImg.scaleType = ImageView.ScaleType.FIT_CENTER
        if (imageUrl.isNotEmpty()) {
            try {
                val bytes  = Base64.decode(imageUrl, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                fullImg.setImageBitmap(bitmap)
            } catch (e: Exception) { }
        }
        fullImg.setOnClickListener { dialog.dismiss() }
        dialog.setContentView(fullImg)
        dialog.show()
    }

    // ✅ Step 7 — View Counter
    private fun incrementViewCount(productId: String) {
        db.collection("products").document(productId)
            .update("viewCount", FieldValue.increment(1))
    }

    // ✅ Step 8 — Enquiry Counter
    private fun incrementEnquiryCount(productId: String) {
        db.collection("products").document(productId)
            .update("enquiryCount", FieldValue.increment(1))
    }

    // ✅ Step 18 — Similar Products
    private fun setupSimilarProducts(category: String, currentId: String) {
        recyclerSimilar.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        similarAdapter = SimilarAdapter(this, similarList) { product ->
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra("id",          product.id)
                putExtra("name",        product.name)
                putExtra("price",       product.price)
                putExtra("category",    product.category)
                putExtra("condition",   product.condition)
                putExtra("description", product.description)
                putExtra("sellerName",  product.sellerName)
                putExtra("sellerPhone", product.sellerPhone)
                putExtra("imageUrl",    product.imageUrl)
                putExtra("isSold",      product.isSold)
                putExtra("location",    product.location)
                putExtra("discount",    product.discount)
                putExtra("viewCount",   product.viewCount)
                putExtra("avgRating",   product.avgRating)
            }
            startActivity(intent)
        }
        recyclerSimilar.adapter = similarAdapter

        db.collection("products")
            .whereEqualTo("category", category)
            .limit(10)
            .get()
            .addOnSuccessListener { snapshot ->
                similarList.clear()
                for (doc in snapshot.documents) {
                    val p = doc.toObject(Product::class.java)
                    if (p != null && p.id != currentId) similarList.add(p)
                }
                similarAdapter.updateList(similarList)
            }
    }

    // ✅ Step 6 — Report Product
    private fun showReportDialog(productId: String, productName: String) {
        val reasons = arrayOf(
            "🚫 Fake or Misleading Product",
            "💰 Wrong Price",
            "📷 Wrong Image",
            "🔞 Inappropriate Content",
            "🔁 Duplicate Listing",
            "Other"
        )

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("🚩 Report Product")
            .setItems(reasons) { _, which ->
                val report = hashMapOf(
                    "productId"   to productId,
                    "productName" to productName,
                    "reason"      to reasons[which],
                    "timestamp"   to System.currentTimeMillis()
                )
                db.collection("reports").add(report)
                    .addOnSuccessListener {
                        Toast.makeText(
                            this,
                            "✅ Report submitted. Thank you!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}