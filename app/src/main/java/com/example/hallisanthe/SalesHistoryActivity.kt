package com.example.hallisanthe

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hallisanthe.adapter.SalesHistoryAdapter
import com.example.hallisanthe.model.Product
import com.example.hallisanthe.utils.LanguageHelper
import com.google.firebase.firestore.FirebaseFirestore

class SalesHistoryActivity : AppCompatActivity() {

    private lateinit var recyclerSales: RecyclerView
    private lateinit var tvNoSales: TextView
    private lateinit var tvTotalSales: TextView
    private lateinit var tvTotalEarnings: TextView
    private lateinit var tvTotalSoldCount: TextView

    private val db = FirebaseFirestore.getInstance()
    private val salesList = mutableListOf<Product>()
    private lateinit var adapter: SalesHistoryAdapter

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LanguageHelper.applyLanguage(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sales_history)

        recyclerSales    = findViewById(R.id.recyclerSalesHistory)
        tvNoSales        = findViewById(R.id.tvNoSales)
        tvTotalSales     = findViewById(R.id.tvTotalSales)
        tvTotalEarnings  = findViewById(R.id.tvTotalEarnings)
        tvTotalSoldCount = findViewById(R.id.tvTotalSoldCount)

        val sellerPhone = intent.getStringExtra("sellerPhone") ?: ""

        recyclerSales.layoutManager = LinearLayoutManager(this)
        adapter = SalesHistoryAdapter(this, salesList)
        recyclerSales.adapter = adapter

        findViewById<TextView>(R.id.btnBackSales).setOnClickListener { finish() }

        loadSalesHistory(sellerPhone)
    }

    private fun loadSalesHistory(phone: String) {
        db.collection("products")
            .whereEqualTo("sellerPhone", phone)
            .whereEqualTo("isSold", true)
            .get()
            .addOnSuccessListener { snapshot ->
                salesList.clear()
                var totalEarnings = 0.0

                for (doc in snapshot.documents) {
                    val p = doc.toObject(Product::class.java)
                    if (p != null) {
                        salesList.add(p)
                        totalEarnings += p.price.toDoubleOrNull() ?: 0.0
                    }
                }

                adapter.updateList(salesList)
                tvTotalSales.text     = "${salesList.size} sold"
                tvTotalSoldCount.text = salesList.size.toString()
                tvTotalEarnings.text  = "₹${totalEarnings.toInt()}"
                tvNoSales.visibility  =
                    if (salesList.isEmpty()) View.VISIBLE else View.GONE
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}