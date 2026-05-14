package com.example.hallisanthe

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hallisanthe.adapter.MyProductAdapter
import com.example.hallisanthe.model.Product
import com.example.hallisanthe.utils.LanguageHelper
import com.google.firebase.firestore.FirebaseFirestore

class MyProductsActivity : AppCompatActivity() {

    private lateinit var recyclerMyProducts: RecyclerView
    private lateinit var etMyPhone: EditText
    private lateinit var etMyPin: EditText
    private lateinit var btnFindMyProducts: Button
    private lateinit var btnBackMyProducts: TextView
    private lateinit var tvMyProductCount: TextView
    private lateinit var tvNoMyProducts: TextView
    private lateinit var layoutPhoneInput: View

    private val db = FirebaseFirestore.getInstance()
    private val myProductList = mutableListOf<Product>()
    private lateinit var myAdapter: MyProductAdapter
    private var currentPhone = ""

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LanguageHelper.applyLanguage(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_products)

        recyclerMyProducts = findViewById(R.id.recyclerMyProducts)
        etMyPhone          = findViewById(R.id.etMyPhone)
        etMyPin            = findViewById(R.id.etMyPin)
        btnFindMyProducts  = findViewById(R.id.btnFindMyProducts)
        btnBackMyProducts  = findViewById(R.id.btnBackMyProducts)
        tvMyProductCount   = findViewById(R.id.tvMyProductCount)
        tvNoMyProducts     = findViewById(R.id.tvNoMyProducts)
        layoutPhoneInput   = findViewById(R.id.layoutPhoneInput)

        recyclerMyProducts.layoutManager = LinearLayoutManager(this)

        myAdapter = MyProductAdapter(
            this,
            myProductList,
            onMarkSold = { product -> markAsSold(product) },
            onDelete   = { product -> deleteProduct(product) },
            onEdit     = { product -> editProduct(product) },    // ✅ Step 12
            onSalesHistory = { openSalesHistory() }              // ✅ Step 15
        )
        recyclerMyProducts.adapter = myAdapter

        btnBackMyProducts.setOnClickListener { finish() }

        btnFindMyProducts.setOnClickListener {
            val phone = etMyPhone.text.toString().trim()
            val pin   = etMyPin.text.toString().trim()
            when {
                phone.length != 10 -> {
                    etMyPhone.error = "Enter valid 10 digit number"
                    return@setOnClickListener
                }
                pin.length != 4 -> {
                    etMyPin.error = "Enter your 4 digit PIN"
                    return@setOnClickListener
                }
            }
            currentPhone = phone
            loadMyProducts(phone, pin)
        }
    }

    private fun loadMyProducts(phone: String, pin: String) {
        db.collection("products")
            .whereEqualTo("sellerPhone", phone)
            .get()
            .addOnSuccessListener { snapshot ->
                myProductList.clear()

                if (snapshot.isEmpty) {
                    Toast.makeText(
                        this, "No products found", Toast.LENGTH_SHORT
                    ).show()
                    return@addOnSuccessListener
                }

                val firstProduct = snapshot.documents[0].toObject(Product::class.java)
                if (firstProduct?.pin?.isNotEmpty() == true && firstProduct.pin != pin) {
                    Toast.makeText(this, "❌ Incorrect PIN!", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                for (doc in snapshot.documents) {
                    val product = doc.toObject(Product::class.java)
                    if (product != null) myProductList.add(product)
                }

                myAdapter.updateList(myProductList)
                layoutPhoneInput.visibility   = View.GONE
                recyclerMyProducts.visibility = View.VISIBLE
                tvMyProductCount.text         = "${myProductList.size} items"
                tvNoMyProducts.visibility     =
                    if (myProductList.isEmpty()) View.VISIBLE else View.GONE

                Toast.makeText(
                    this,
                    "✅ Welcome ${firstProduct?.sellerName}!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun markAsSold(product: Product) {
        db.collection("products").document(product.id)
            .update("isSold", true)
            .addOnSuccessListener {
                Toast.makeText(this, getString(R.string.marked_sold), Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteProduct(product: Product) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete_title))
            .setMessage("'${product.name}' — ${getString(R.string.delete_confirm)}")
            .setPositiveButton(getString(R.string.btn_delete_confirm)) { _, _ ->
                db.collection("products").document(product.id)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, "🗑 Deleted!", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton(getString(R.string.btn_cancel), null)
            .show()
    }

    // ✅ Step 12 — Edit Product
    private fun editProduct(product: Product) {
        val intent = Intent(this, EditProductActivity::class.java).apply {
            putExtra("id",                product.id)
            putExtra("name",              product.name)
            putExtra("price",             product.price)
            putExtra("discount",          product.discount)
            putExtra("description",       product.description)
            putExtra("location",          product.location)
            putExtra("isFeatured",        product.isFeatured)
            putExtra("deliveryAvailable", product.deliveryAvailable)
        }
        startActivity(intent)
    }

    // ✅ Step 15 — Sales History
    private fun openSalesHistory() {
        val intent = Intent(this, SalesHistoryActivity::class.java)
        intent.putExtra("sellerPhone", currentPhone)
        startActivity(intent)
    }
}