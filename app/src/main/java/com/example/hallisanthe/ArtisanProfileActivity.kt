package com.example.hallisanthe

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hallisanthe.adapter.ProductAdapter
import com.example.hallisanthe.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import android.content.Context
class ArtisanProfileActivity : AppCompatActivity() {

    private lateinit var tvProfileName: TextView
    private lateinit var tvProfileLocation: TextView
    private lateinit var tvProfilePhone: TextView
    private lateinit var tvTotalProducts: TextView
    private lateinit var tvSoldProducts: TextView
    private lateinit var tvAvailableProducts: TextView
    private lateinit var recyclerArtisanProducts: RecyclerView
    private lateinit var btnBackProfile: TextView

    private val db = FirebaseFirestore.getInstance()
    private val artisanProducts = mutableListOf<Product>()
    private lateinit var adapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_artisan_profile)

        tvProfileName         = findViewById(R.id.tvProfileName)
        tvProfileLocation     = findViewById(R.id.tvProfileLocation)
        tvProfilePhone        = findViewById(R.id.tvProfilePhone)
        tvTotalProducts       = findViewById(R.id.tvTotalProducts)
        tvSoldProducts        = findViewById(R.id.tvSoldProducts)
        tvAvailableProducts   = findViewById(R.id.tvAvailableProducts)
        recyclerArtisanProducts = findViewById(R.id.recyclerArtisanProducts)
        btnBackProfile        = findViewById(R.id.btnBackProfile)

        val sellerName  = intent.getStringExtra("sellerName")  ?: "Artisan"
        val sellerPhone = intent.getStringExtra("sellerPhone") ?: ""
        val location    = intent.getStringExtra("location")    ?: ""

        tvProfileName.text     = sellerName
        tvProfileLocation.text = if (location.isNotEmpty()) "📍 $location" else "📍 Not specified"
        tvProfilePhone.text    = "📞 $sellerPhone"

        recyclerArtisanProducts.layoutManager = GridLayoutManager(this, 2)

        adapter = ProductAdapter(this, artisanProducts) { product ->
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra("name",        product.name)
                putExtra("price",       product.price)
                putExtra("category",    product.category)
                putExtra("description", product.description)
                putExtra("sellerName",  product.sellerName)
                putExtra("sellerPhone", product.sellerPhone)
                putExtra("imageUrl",    product.imageUrl)
                putExtra("isSold",      product.isSold)
                putExtra("location",    product.location)
                putExtra("condition",   product.condition)
            }
            startActivity(intent)
        }
        recyclerArtisanProducts.adapter = adapter

        btnBackProfile.setOnClickListener { finish() }

        loadArtisanProducts(sellerPhone)
    }

    private fun loadArtisanProducts(phone: String) {
        db.collection("products")
            .whereEqualTo("sellerPhone", phone)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener

                artisanProducts.clear()
                for (doc in snapshot.documents) {
                    val p = doc.toObject(Product::class.java)
                    if (p != null) artisanProducts.add(p)
                }

                adapter.updateList(artisanProducts)

                // ✅ Stats
                val total     = artisanProducts.size
                val sold      = artisanProducts.count { it.isSold }
                val available = total - sold

                tvTotalProducts.text     = total.toString()
                tvSoldProducts.text      = sold.toString()
                tvAvailableProducts.text = available.toString()
            }
    }
}