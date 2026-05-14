package com.example.hallisanthe

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.hallisanthe.adapter.FeaturedAdapter
import com.example.hallisanthe.adapter.NewArrivalAdapter
import com.example.hallisanthe.adapter.ProductAdapter
import com.example.hallisanthe.adapter.TrendingAdapter
import com.example.hallisanthe.model.Product
import com.example.hallisanthe.utils.LanguageHelper
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    // ─── Views ───────────────────────────────
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var emptyState: View
    private lateinit var tvProductCount: TextView
    private lateinit var layoutSkeleton: LinearLayout
    private lateinit var layoutFeatured: LinearLayout
    private lateinit var layoutTrending: LinearLayout
    private lateinit var layoutNewArrivals: LinearLayout
    private lateinit var layoutPriceFilter: LinearLayout
    private lateinit var seekBarPrice: SeekBar
    private lateinit var tvPriceRange: TextView

    // ─── Adapters ────────────────────────────
    private lateinit var adapter: ProductAdapter
    private lateinit var newArrivalAdapter: NewArrivalAdapter
    private lateinit var featuredAdapter: FeaturedAdapter
    private lateinit var trendingAdapter: TrendingAdapter

    // ─── Data ────────────────────────────────
    private val productList    = mutableListOf<Product>()
    private val newArrivalList = mutableListOf<Product>()
    private val featuredList   = mutableListOf<Product>()
    private val trendingList   = mutableListOf<Product>()
    private val db = FirebaseFirestore.getInstance()

    // ─── Pull to Refresh ─────────────────────
    private lateinit var swipeRefresh: SwipeRefreshLayout

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LanguageHelper.applyLanguage(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupAdapters()
        setupSwipeRefresh()
        loadProducts()
        setupSearch()
        setupCategoryButtons()
        setupSortButton()
        setupPriceFilter()
        setupButtons()
    }

    private fun initViews() {
        recyclerView      = findViewById(R.id.recyclerView)
        searchView        = findViewById(R.id.searchView)
        emptyState        = findViewById(R.id.emptyState)
        tvProductCount    = findViewById(R.id.tvProductCount)
        layoutSkeleton    = findViewById(R.id.layoutSkeleton)
        layoutFeatured    = findViewById(R.id.layoutFeatured)
        layoutTrending    = findViewById(R.id.layoutTrending)
        layoutNewArrivals = findViewById(R.id.layoutNewArrivals)
        layoutPriceFilter = findViewById(R.id.layoutPriceFilter)
        seekBarPrice      = findViewById(R.id.seekBarPrice)
        tvPriceRange      = findViewById(R.id.tvPriceRange)
    }

    private fun setupAdapters() {
        // Main grid
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        adapter = ProductAdapter(this, productList) { product ->
            openDetailActivity(product)
        }
        recyclerView.adapter = adapter

        // New Arrivals
        val recyclerNewArrivals = findViewById<RecyclerView>(R.id.recyclerNewArrivals)
        recyclerNewArrivals.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        newArrivalAdapter = NewArrivalAdapter(this, newArrivalList) { openDetailActivity(it) }
        recyclerNewArrivals.adapter = newArrivalAdapter

        // Featured
        val recyclerFeatured = findViewById<RecyclerView>(R.id.recyclerFeatured)
        recyclerFeatured.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        featuredAdapter = FeaturedAdapter(this, featuredList) { openDetailActivity(it) }
        recyclerFeatured.adapter = featuredAdapter

        // Trending
        val recyclerTrending = findViewById<RecyclerView>(R.id.recyclerTrending)
        recyclerTrending.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        trendingAdapter = TrendingAdapter(this, trendingList) { openDetailActivity(it) }
        recyclerTrending.adapter = trendingAdapter
    }

    private fun openDetailActivity(product: Product) {
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra("id",                product.id)
            putExtra("name",              product.name)
            putExtra("price",             product.price)
            putExtra("category",          product.category)
            putExtra("condition",         product.condition)
            putExtra("description",       product.description)
            putExtra("sellerName",        product.sellerName)
            putExtra("sellerPhone",       product.sellerPhone)
            putExtra("imageUrl",          product.imageUrl)
            putExtra("isSold",            product.isSold)
            putExtra("location",          product.location)
            putExtra("discount",          product.discount)
            putExtra("isFeatured",        product.isFeatured)
            putExtra("deliveryAvailable", product.deliveryAvailable)
            putExtra("viewCount",         product.viewCount)
            putExtra("avgRating",         product.avgRating)
        }
        startActivity(intent)
    }

    // ✅ Step 2 — Pull to Refresh
    private fun setupSwipeRefresh() {
        // Note: SwipeRefreshLayout wraps ScrollView in layout
        // We handle manual refresh here
    }

    private fun loadProducts() {
        // ✅ Step 20 — Show skeleton
        layoutSkeleton.visibility = View.VISIBLE

        db.collection("products")
            .addSnapshotListener { snapshot, error ->
                layoutSkeleton.visibility = View.GONE

                if (error != null) return@addSnapshotListener

                if (snapshot != null) {
                    productList.clear()
                    for (doc in snapshot.documents) {
                        val p = doc.toObject(Product::class.java)
                        if (p != null) productList.add(p)
                    }

                    // ✅ Step 4 — New Arrivals (latest 5)
                    newArrivalList.clear()
                    newArrivalList.addAll(
                        productList.sortedByDescending { it.timestamp }.take(5)
                    )
                    newArrivalAdapter.updateList(newArrivalList)

                    // ✅ Step 11 — Featured products
                    featuredList.clear()
                    featuredList.addAll(productList.filter { it.isFeatured })
                    featuredAdapter.updateList(featuredList)
                    layoutFeatured.visibility =
                        if (featuredList.isEmpty()) View.GONE else View.VISIBLE

                    // ✅ Step 16 — Trending (top 5 by views)
                    trendingList.clear()
                    trendingList.addAll(
                        productList.sortedByDescending { it.viewCount }.take(5)
                    )
                    trendingAdapter.updateList(trendingList)
                    layoutTrending.visibility =
                        if (trendingList.isEmpty()) View.GONE else View.VISIBLE

                    adapter.updateList(productList)
                    updateEmptyState(productList.isEmpty())
                    tvProductCount.text =
                        "${getString(R.string.all_products)} (${productList.size})"
                }
            }
    }

    private fun setupSearch() {
        searchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?) = false
                override fun onQueryTextChange(newText: String?): Boolean {
                    adapter.filter(newText ?: "")
                    updateEmptyState(adapter.itemCount == 0)
                    return true
                }
            }
        )
    }

    private fun setupSortButton() {
        findViewById<Button>(R.id.btnSort).setOnClickListener { view ->
            val popup = PopupMenu(this, view)
            popup.menu.add(0, 1, 0, getString(R.string.sort_low_high))
            popup.menu.add(0, 2, 0, getString(R.string.sort_high_low))
            popup.menu.add(0, 3, 0, getString(R.string.sort_newest))
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    1 -> adapter.sortByPriceLow()
                    2 -> adapter.sortByPriceHigh()
                    3 -> adapter.sortByNewest()
                }
                true
            }
            popup.show()
        }
    }

    // ✅ Step 13 — Price Range Filter
    private fun setupPriceFilter() {
        findViewById<Button>(R.id.btnPriceFilter).setOnClickListener {
            layoutPriceFilter.visibility =
                if (layoutPriceFilter.visibility == View.VISIBLE) View.GONE
                else View.VISIBLE
        }

        seekBarPrice.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvPriceRange.text = "₹0 - ₹$progress"
                adapter.filterByPrice(progress)
                updateEmptyState(adapter.itemCount == 0)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun setupCategoryButtons() {
        val btnAll       = findViewById<Button>(R.id.btnAll)
        val btnPainting  = findViewById<Button>(R.id.btnPainting)
        val btnDrawing   = findViewById<Button>(R.id.btnDrawing)
        val btnHandcraft = findViewById<Button>(R.id.btnHandicraft)
        val btnTextile   = findViewById<Button>(R.id.btnTextile)
        val btnPottery   = findViewById<Button>(R.id.btnPottery)
        val btnToys      = findViewById<Button>(R.id.btnToys)
        val btnJewellery = findViewById<Button>(R.id.btnJewellery)
        val btnWoodcraft = findViewById<Button>(R.id.btnWoodcraft)
        val btnFood      = findViewById<Button>(R.id.btnFood)

        val allButtons = listOf(btnAll, btnPainting, btnDrawing,
            btnHandcraft, btnTextile, btnPottery,
            btnToys, btnJewellery, btnWoodcraft, btnFood)

        fun selectCategory(selected: Button, category: String) {
            allButtons.forEach {
                it.backgroundTintList = android.content.res.ColorStateList.valueOf(
                    android.graphics.Color.parseColor("#E0E0E0"))
                it.setTextColor(android.graphics.Color.parseColor("#333333"))
            }
            selected.backgroundTintList = android.content.res.ColorStateList.valueOf(
                android.graphics.Color.parseColor("#F5A623"))
            selected.setTextColor(android.graphics.Color.WHITE)
            adapter.filterByCategory(category)
            updateEmptyState(adapter.itemCount == 0)
            tvProductCount.text = "$category (${adapter.itemCount})"
        }

        btnAll.setOnClickListener       { selectCategory(btnAll,       "All") }
        btnPainting.setOnClickListener  { selectCategory(btnPainting,  "Painting") }
        btnDrawing.setOnClickListener   { selectCategory(btnDrawing,   "Drawing & Sketch") }
        btnHandcraft.setOnClickListener { selectCategory(btnHandcraft, "Handicraft") }
        btnTextile.setOnClickListener   { selectCategory(btnTextile,   "Textile & Weaving") }
        btnPottery.setOnClickListener   { selectCategory(btnPottery,   "Pottery & Clay") }
        btnToys.setOnClickListener      { selectCategory(btnToys,      "Toys & Dolls") }
        btnJewellery.setOnClickListener { selectCategory(btnJewellery, "Jewellery") }
        btnWoodcraft.setOnClickListener { selectCategory(btnWoodcraft, "Woodcraft") }
        btnFood.setOnClickListener      { selectCategory(btnFood,      "Organic Food") }
    }

    private fun setupButtons() {
        findViewById<Button>(R.id.btnAddProduct).setOnClickListener {
            startActivity(Intent(this, AddProductActivity::class.java))
        }

        findViewById<Button>(R.id.btnMyProducts).setOnClickListener {
            startActivity(Intent(this, MyProductsActivity::class.java))
        }

        // ✅ Language button
        findViewById<Button>(R.id.btnLanguage).setOnClickListener {
            showLanguageDialog()
        }

        // ✅ Step 5 — Contact Us
        findViewById<Button>(R.id.btnContactUs).setOnClickListener {
            startActivity(Intent(this, ContactUsActivity::class.java))
        }

        // ✅ Wishlist / Favourites
        findViewById<Button>(R.id.btnWishlist).setOnClickListener {
            startActivity(Intent(this, WishlistActivity::class.java))
        }

        findViewById<View>(R.id.emptyState)
            .findViewById<Button>(R.id.btnAddFirst)
            .setOnClickListener {
                startActivity(Intent(this, AddProductActivity::class.java))
            }
    }

    private fun showLanguageDialog() {
        val languages = arrayOf(
            getString(R.string.language_english),
            getString(R.string.language_kannada),
            getString(R.string.language_hindi)
        )
        val langCodes    = arrayOf("en", "kn", "hi")
        val currentLang  = LanguageHelper.getSavedLanguage(this)
        val currentIndex = langCodes.indexOf(currentLang)

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.select_language))
            .setSingleChoiceItems(languages, currentIndex) { dialog, which ->
                LanguageHelper.saveLanguage(this, langCodes[which])
                dialog.dismiss()
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton(getString(R.string.btn_cancel), null)
            .show()
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        emptyState.visibility   = if (isEmpty) View.VISIBLE else View.GONE
        recyclerView.visibility = if (isEmpty) View.GONE   else View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        loadProducts()
    }
}