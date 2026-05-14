package com.example.hallisanthe.adapter

import android.content.Context
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.hallisanthe.R
import com.example.hallisanthe.model.Product

class ProductAdapter(
    private val context: Context,
    private var productList: MutableList<Product>,
    private val onItemClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private var fullList: MutableList<Product> = mutableListOf()
    private val prefs: SharedPreferences =
        context.getSharedPreferences("wishlist", Context.MODE_PRIVATE)

    init { fullList.addAll(productList) }

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProduct: ImageView   = itemView.findViewById(R.id.imgProduct)
        val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        val tvPrice: TextView       = itemView.findViewById(R.id.tvPrice)
        val tvCategory: TextView    = itemView.findViewById(R.id.tvCategory)
        val tvSellerName: TextView  = itemView.findViewById(R.id.tvSellerName)
        val tvLocation: TextView    = itemView.findViewById(R.id.tvLocation)
        val tvSoldOverlay: TextView = itemView.findViewById(R.id.tvSoldOverlay)
        val cardProduct: CardView   = itemView.findViewById(R.id.cardProduct)
        val tvViewCount: TextView   = itemView.findViewById(R.id.tvViewCount)
        val tvDiscount: TextView    = itemView.findViewById(R.id.tvDiscount)
        val tvWishlist: TextView    = itemView.findViewById(R.id.tvWishlist)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]

        holder.tvProductName.text = product.name
        holder.tvCategory.text    = product.category
        holder.tvSellerName.text  = "by ${product.sellerName}"
        holder.tvLocation.text    =
            if (product.location.isNotEmpty()) "📍 ${product.location}" else ""
        holder.tvViewCount.text   = "👁️ ${product.viewCount} views"

        // ✅ Price with discount
        if (product.discount.isNotEmpty() && product.discount != "0") {
            val originalPrice = product.price.toDoubleOrNull() ?: 0.0
            val discountPct   = product.discount.toDoubleOrNull() ?: 0.0
            val discountedPrice = originalPrice - (originalPrice * discountPct / 100)
            holder.tvPrice.text    = "₹ ${discountedPrice.toInt()}"
            holder.tvDiscount.text = "${product.discount}% OFF"
            holder.tvDiscount.visibility = View.VISIBLE
        } else {
            holder.tvPrice.text = "₹ ${product.price}"
            holder.tvDiscount.visibility = View.GONE
        }

        // ✅ Wishlist
        val isWishlisted = prefs.getBoolean(product.id, false)
        holder.tvWishlist.text = if (isWishlisted) "❤️" else "🤍"
        holder.tvWishlist.setOnClickListener {
            val currentState = prefs.getBoolean(product.id, false)
            prefs.edit().putBoolean(product.id, !currentState).apply()
            holder.tvWishlist.text = if (!currentState) "❤️" else "🤍"
        }

        // ✅ SOLD badge
        if (product.isSold) {
            holder.tvSoldOverlay.visibility = View.VISIBLE
            holder.cardProduct.alpha        = 0.6f
        } else {
            holder.tvSoldOverlay.visibility = View.GONE
            holder.cardProduct.alpha        = 1.0f
        }

        holder.itemView.setOnClickListener { onItemClick(product) }

        // Load image
        if (product.imageUrl.isNotEmpty()) {
            try {
                val bytes  = Base64.decode(product.imageUrl, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                holder.imgProduct.setImageBitmap(bitmap)
            } catch (e: Exception) {
                holder.imgProduct.setImageResource(R.drawable.ic_launcher_background)
            }
        } else {
            holder.imgProduct.setImageResource(R.drawable.ic_launcher_background)
        }
    }

    override fun getItemCount(): Int = productList.size

    fun updateList(newList: MutableList<Product>) {
        productList = newList
        fullList.clear()
        fullList.addAll(newList)
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        productList = if (query.isEmpty()) fullList.toMutableList()
        else fullList.filter {
            it.name.lowercase().contains(query.lowercase())
        }.toMutableList()
        notifyDataSetChanged()
    }

    fun filterByCategory(category: String) {
        productList = if (category == "All") fullList.toMutableList()
        else fullList.filter { it.category == category }.toMutableList()
        notifyDataSetChanged()
    }

    // ✅ Price range filter — Step 13
    fun filterByPrice(maxPrice: Int) {
        productList = fullList.filter {
            (it.price.toDoubleOrNull() ?: 0.0) <= maxPrice
        }.toMutableList()
        notifyDataSetChanged()
    }

    // ✅ Nearby filter — Step 19
    fun filterByLocation(location: String) {
        productList = if (location.isEmpty()) fullList.toMutableList()
        else fullList.filter {
            it.location.lowercase().contains(location.lowercase())
        }.toMutableList()
        notifyDataSetChanged()
    }

    fun sortByPriceLow() {
        productList.sortBy { it.price.toDoubleOrNull() ?: 0.0 }
        notifyDataSetChanged()
    }

    fun sortByPriceHigh() {
        productList.sortByDescending { it.price.toDoubleOrNull() ?: 0.0 }
        notifyDataSetChanged()
    }

    fun sortByNewest() {
        productList.sortByDescending { it.timestamp }
        notifyDataSetChanged()
    }
}