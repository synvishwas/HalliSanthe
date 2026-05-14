package com.example.hallisanthe.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hallisanthe.R
import com.example.hallisanthe.model.Product

class FeaturedAdapter(
    private val context: Context,
    private var productList: MutableList<Product>,
    private val onItemClick: (Product) -> Unit
) : RecyclerView.Adapter<FeaturedAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView   = view.findViewById(R.id.imgFeatured)
        val name: TextView   = view.findViewById(R.id.tvFeaturedName)
        val price: TextView  = view.findViewById(R.id.tvFeaturedPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(context)
            .inflate(R.layout.item_featured, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val p = productList[position]
        holder.name.text  = p.name
        holder.price.text = "₹ ${p.price}"

        if (p.imageUrl.isNotEmpty()) {
            try {
                val bytes  = Base64.decode(p.imageUrl, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                holder.img.setImageBitmap(bitmap)
            } catch (e: Exception) {
                holder.img.setImageResource(R.drawable.ic_launcher_background)
            }
        }
        holder.itemView.setOnClickListener { onItemClick(p) }
    }

    override fun getItemCount() = productList.size

    fun updateList(newList: MutableList<Product>) {
        productList = newList
        notifyDataSetChanged()
    }
}