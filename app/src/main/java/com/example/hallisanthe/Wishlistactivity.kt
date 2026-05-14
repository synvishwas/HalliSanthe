package com.example.hallisanthe

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hallisanthe.adapter.ProductAdapter
import com.example.hallisanthe.model.Product
import com.example.hallisanthe.utils.LanguageHelper
import com.google.firebase.firestore.FirebaseFirestore

class WishlistActivity : AppCompatActivity() {

    private lateinit var recyclerWishlist: RecyclerView
    private lateinit var layoutEmpty: LinearLayout
    private lateinit var tvWishlistCount: TextView

    private val db = FirebaseFirestore.getInstance()
    private val wishlistProducts = mutableListOf<Product>()
    private lateinit var adapter: ProductAdapter
    private lateinit var prefs: SharedPreferences

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LanguageHelper.applyLanguage(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wishlist)

        recyclerWishlist = findViewById(R.id.recyclerWishlist)
        layoutEmpty      = findViewById(R.id.layoutWishlistEmpty)
        tvWishlistCount  = findViewById(R.id.tvWishlistCount)

        prefs = getSharedPreferences("wishlist", Context.MODE_PRIVATE)

        recyclerWishlist.layoutManager = GridLayoutManager(this, 2)

        adapter = ProductAdapter(this, wishlistProducts) { product ->
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
        recyclerWishlist.adapter = adapter

        findViewById<TextView>(R.id.btnBackWishlist)
            .setOnClickListener { finish() }

        loadWishlist()
    }

    private fun loadWishlist() {
        // ✅ Get all wishlisted product IDs from SharedPreferences
        val wishlistedIds = prefs.all
            .filter { it.value == true }
            .keys
            .toList()

        if (wishlistedIds.isEmpty()) {
            showEmpty()
            return
        }

        // ✅ Fetch wishlisted products from Firestore
        db.collection("products")
            .get()
            .addOnSuccessListener { snapshot ->
                wishlistProducts.clear()

                for (doc in snapshot.documents) {
                    val product = doc.toObject(Product::class.java)
                    if (product != null && wishlistedIds.contains(product.id)) {
                        wishlistProducts.add(product)
                    }
                }

                if (wishlistProducts.isEmpty()) {
                    showEmpty()
                } else {
                    layoutEmpty.visibility        = View.GONE
                    recyclerWishlist.visibility   = View.VISIBLE
                    adapter.updateList(wishlistProducts)
                    tvWishlistCount.text = "${wishlistProducts.size} items"
                }
            }
            .addOnFailureListener {
                showEmpty()
            }
    }

    private fun showEmpty() {
        layoutEmpty.visibility      = View.VISIBLE
        recyclerWishlist.visibility = View.GONE
        tvWishlistCount.text        = "0 items"
    }

    override fun onResume() {
        super.onResume()
        loadWishlist()
    }
}