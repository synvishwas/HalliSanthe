package com.example.hallisanthe.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hallisanthe.R
import com.example.hallisanthe.model.Product

class MyProductAdapter(
    private val context: Context,
    private var productList: MutableList<Product>,
    private val onMarkSold: (Product) -> Unit,
    private val onDelete: (Product) -> Unit,
    private val onEdit: (Product) -> Unit,
    private val onSalesHistory: () -> Unit
) : RecyclerView.Adapter<MyProductAdapter.MyProductViewHolder>() {

    inner class MyProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgMyProduct: ImageView       = itemView.findViewById(R.id.imgMyProduct)
        val tvMyProductName: TextView     = itemView.findViewById(R.id.tvMyProductName)
        val tvMyProductCategory: TextView = itemView.findViewById(R.id.tvMyProductCategory)
        val tvMyProductPrice: TextView    = itemView.findViewById(R.id.tvMyProductPrice)
        val tvMyProductStatus: TextView   = itemView.findViewById(R.id.tvMyProductStatus)
        val btnMarkSold: Button           = itemView.findViewById(R.id.btnMarkSold)
        val btnDelete: Button             = itemView.findViewById(R.id.btnDelete)
        val btnEdit: Button               = itemView.findViewById(R.id.btnEdit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyProductViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_my_product, parent, false)
        return MyProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyProductViewHolder, position: Int) {
        val product = productList[position]

        holder.tvMyProductName.text     = product.name
        holder.tvMyProductCategory.text = product.category
        holder.tvMyProductPrice.text    = "₹ ${product.price}"

        if (product.isSold) {
            holder.tvMyProductStatus.text = "SOLD"
            holder.tvMyProductStatus.backgroundTintList =
                android.content.res.ColorStateList.valueOf(
                    android.graphics.Color.parseColor("#9E9E9E"))
            holder.btnMarkSold.isEnabled = false
            holder.btnMarkSold.text      = "Sold ✓"
        } else {
            holder.tvMyProductStatus.text = "Available"
            holder.tvMyProductStatus.backgroundTintList =
                android.content.res.ColorStateList.valueOf(
                    android.graphics.Color.parseColor("#27AE60"))
            holder.btnMarkSold.isEnabled = true
            holder.btnMarkSold.text      = "Mark Sold"
        }

        if (product.imageUrl.isNotEmpty()) {
            try {
                val bytes  = Base64.decode(product.imageUrl, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                holder.imgMyProduct.setImageBitmap(bitmap)
            } catch (e: Exception) {
                holder.imgMyProduct.setImageResource(R.drawable.ic_launcher_background)
            }
        }

        holder.btnMarkSold.setOnClickListener { onMarkSold(product) }
        holder.btnDelete.setOnClickListener   { onDelete(product) }
        holder.btnEdit.setOnClickListener     { onEdit(product) }   // ✅ Step 12
    }

    override fun getItemCount(): Int = productList.size

    fun updateList(newList: MutableList<Product>) {
        productList = newList
        notifyDataSetChanged()
    }
}